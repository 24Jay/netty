package com.jay.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.BreakIterator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 负责轮询多路复用器selector，以处理多个客户端的并发访问
 * 
 * @author jay
 *
 */
public class MultiplexerTimeServer implements Runnable
{
	private Selector selector;

	private ServerSocketChannel channel;

	private volatile boolean stop;

	private AtomicInteger messageCount = new AtomicInteger(0);

	/***
	 * 初始化多路复用器，绑定监听端口
	 * 
	 * @param port
	 */
	public MultiplexerTimeServer(int port)
	{
		try
		{
			// 创建selector
			selector = Selector.open();
			// 创建serverSocketChannel

			channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(port), 1024);
			channel.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("MultiplexerTimeServer is start in port: " + port);
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
				// timeout=1000ms, this method will blocking until any channel
				// is selected or timeout expires
				selector.select(1000);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				System.out.println("keys---" + keys);

				int count = 0;
				while (it.hasNext())
				// for (SelectionKey key : keys)
				{
					System.out.println("count=" + count++);
					key = it.next();
					it.remove();
					try
					{
						if (key.isValid())
						{
							System.out.println("keys===" + keys);
							doAccept(key);
							doRead(key);
						}
					}
					catch (Exception e)
					{

						if (key != null)
						{
							key.cancel();
							if (key.channel() != null)
								key.channel().close();
						}
						e.printStackTrace();
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

	private void doAccept(SelectionKey key) throws IOException
	{

		if (key.isAcceptable())
		{
			ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
			SocketChannel sc = ssc.accept();
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
			System.out.println("add op_read.....");
		}

	}

	private void doRead(SelectionKey key) throws IOException
	{
		// 根据selectionKey的标志位判断网络事件类型
		String result = "Bad Order!";

		if (key.isReadable())
		{
			System.out.println("isReadable? " + true);
			SocketChannel ch = (SocketChannel) key.channel();
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			int readBytes = ch.read(readBuffer);
			if (readBytes > 0)
			{
				readBuffer.flip();
				byte[] bytes = new byte[readBuffer.remaining()];
				readBuffer.get(bytes);
				String body = new String(bytes);// , "UTF-8");
				// 去掉最后面的分行符号
				System.out.println("MultiplexerTimeServer receive order : " + body.trim());
				body = body.trim();
				if ("Query Time Order".equalsIgnoreCase(body))
					result = new Date(System.currentTimeMillis()).toString();
				doWrite(ch, result);
			}
		}

	}

	private void doWrite(SocketChannel channel, String response) throws IOException
	{
		if (channel == null)
			return;

		if (response != null && response.trim().length() > 0)
		{
			/**
			 * 这里要么加上"\n"，要么在最后执行channel.close(),否则一直阻塞在write()方法中
			 */
			;
			response += "------->>>" + messageCount.incrementAndGet() + "\n";
			response.trim();
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			while (writeBuffer.hasRemaining())
			{
				System.out.println(channel.write(writeBuffer));
			}
			channel.close();
		}
	}

}
