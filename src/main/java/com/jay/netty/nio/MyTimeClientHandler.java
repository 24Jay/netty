package com.jay.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MyTimeClientHandler implements Runnable
{

	private String host;

	private int port;

	private Selector selector;

	private SocketChannel channel;

	private volatile boolean stop;

	public MyTimeClientHandler(String host, int port)
	{
		this.host = host;
		this.port = port;
		try
		{
			this.channel = SocketChannel.open();
			this.channel.configureBlocking(false);
			this.selector = Selector.open();
			System.out.println("---selector + " + selector);
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
				for(SelectionKey key:keys)
					System.out.println("---selectionKey = "+key.toString());
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;

				while (it.hasNext())
				{
					key = it.next();
					// it.remove();
					try
					{
						doRead(key);
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

	private void doRead(SelectionKey key) throws IOException
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

			if (key.isReadable())
			{
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				if (sc.read(readBuffer) > 0)
				{
					readBuffer.flip();
					byte[] bys = new byte[readBuffer.remaining()];
					readBuffer.get(bys);
					String body = new String(bys);
					System.out.println("Time is :" + body);
					this.stop = true;
				}
			}
		}
	}

	private void doConnect() throws IOException
	{
		/***
		 * 首先对connect()进行判断，如果连接成功，则注册到selector上面<br>
		 * 否则，说明服务器没有返回TCP握手的应答消息，这时候并不代表失败，<br>
		 * 我们需要将channel注册到selector上面，事件为SelectionKey.OP_CONNECT
		 */

		// synchronized (selector)
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

	}

	private void doWrite(SocketChannel sc) throws IOException
	{
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);

		if (!writeBuffer.hasRemaining())
			System.out.println("Send order to  server succed.");

	}

}
