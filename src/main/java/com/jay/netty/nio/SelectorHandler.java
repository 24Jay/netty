package com.jay.netty.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorHandler implements Runnable
{
	private Selector selector;

	private int count = 0;

	private int success = 0;

	private boolean stop = false;

	public SelectorHandler(Selector sl, int c)
	{
		this.selector = sl;
		this.count = c;
	}

	public void run()
	{

		while (!stop)
		{
//			System.out.println("***************");
			try
			{
				selector.select(2000);
				Set<SelectionKey> keys = selector.selectedKeys();
				Set<SelectionKey> allKeys = selector.keys();
				System.out.println("keys.count()========" + keys.size());
				System.out.println("alllkeys.count()========" + allKeys.size());
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;

				while (it.hasNext())
				{
					key = it.next();
					// it.remove();
					try
					{
						if (key.isValid())
						{
							hanlder(key);
							it.remove();
							if (count == 0)
								stop = true;
//							System.out.println("Successfully operation count=" + success);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						/*
						 * if (key != null) { key.cancel(); if (key.channel() !=
						 * null) key.channel().close(); }
						 */
					}

				}
			}

			catch (IOException e)
			{
				e.printStackTrace();
				// System.exit(1);
			}

		}

	}

	private void hanlder(SelectionKey key) throws IOException
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
			}

			if (key.isReadable())
			{
				doRead(sc);
			}
		}

	}

	private void doRead(SocketChannel sc) throws IOException
	{
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		if (sc.read(readBuffer) > 0)
		{
			readBuffer.flip();
			byte[] bys = new byte[readBuffer.remaining()];
			readBuffer.get(bys);
			String body = new String(bys);
			System.out.println("Time is :" + body);
			count--;
			success++;
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
