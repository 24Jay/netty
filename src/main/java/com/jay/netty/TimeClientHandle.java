package com.jay.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable
{

	private String host;

	private int port;

	private Selector selector;

	private SocketChannel channel;

	private volatile boolean stop;

	public TimeClientHandle(String host, int port)
	{
		this.host = host;
		this.port = port;

		try
		{
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);
		}
		catch (Exception e)
		{
			System.exit(1);
		}

	}

	public void run()
	{
		try
		{
			doConnect();

		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		while (!stop)
		{
			try
			{
				selector.select(1000);

				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;

				while (it.hasNext())
				{
					key = it.next();
					it.remove();
					try
					{
						handleInput(key);
					}
					catch (Exception e)
					{
						if (key != null)
						{
							key.cancel();
							if (key.channel() != null)
								key.channel().close();
						}
					}

				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}

		}

		if (selector != null)
		{
			try
			{
				selector.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	private void handleInput(SelectionKey key) throws IOException
	{
		if (key.isValid())
		{
			SocketChannel sc = (SocketChannel) key.channel();
			if (key.isConnectable())
			{
				if (sc.finishConnect())
				{
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				}
				else
				{
					System.exit(1);
				}
			}
		}
	}

	private void doConnect() throws IOException
	{
		if (channel.connect(new InetSocketAddress(host, port)))
		{
			channel.register(selector, SelectionKey.OP_READ);
			doWrite(channel);
		}
		else
		{
			channel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel sc) throws IOException
	{
		byte[] req = "QUERY TIEM ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);

		if (!writeBuffer.hasRemaining())
			System.out.println("Send order 2 server succed.");
	}

}
