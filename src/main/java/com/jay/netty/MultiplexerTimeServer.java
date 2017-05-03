package com.jay.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable
{
	private Selector selector;

	private ServerSocketChannel channel;

	private volatile boolean stop;

	public MultiplexerTimeServer(int port)
	{
		try
		{
			selector = Selector.open();
			channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(port), 1024);
			channel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port: " + port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void stop()
	{
		this.stop = true;
	}

	public void run()
	{
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
			}

		}

		if (selector != null)
		{
			try
			{
				selector.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException
	{
		if (key.isValid())
		{
			if (key.isAcceptable())
			{
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}

			if (key.isReadable())
			{
				SocketChannel ch = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = ch.read(readBuffer);
				if (readBytes > 0)
				{
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order : " + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
							? new java.util.Date(System.currentTimeMillis()).toString()
							: "BAD ORDER";
					doWrite(ch, currentTime);
				}
				else if (readBytes < 0)
				{
					key.cancel();
					ch.close();
				}
				else
				{

				}
			}
		}
	}

	private void doWrite(SocketChannel ch, String response) throws IOException
	{
		if (response != null && response.trim().length() > 0)
		{
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			ch.write(writeBuffer);
		}
	}

}
