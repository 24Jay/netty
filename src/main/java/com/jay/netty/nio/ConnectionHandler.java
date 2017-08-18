package com.jay.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Handler;

public class ConnectionHandler
{

	private final static String host = "127.0.0.1";

	private final static int port = 8080;

	private Selector selector;

	private SocketChannel channel;

	private boolean stop = false;

	public ConnectionHandler(Selector selector, SocketChannel ch)
	{
		this.selector = selector;
		this.channel = ch;
		try
		{
			this.channel.configureBlocking(false);

			/***
			 * 首先对connect()进行判断，如果连接成功，则注册到selector上面<br>
			 * 否则，说明服务器没有返回TCP握手的应答消息，这时候并不代表失败，<br>
			 * 我们需要将channel注册到selector上面，事件为SelectionKey.OP_CONNECT
			 */

			// synchronized (selector)
			if (channel.connect(new InetSocketAddress(host, port)))
			{
				System.out.println(this + "-------Connected to 127.0.0.1:8080 successfully!");
				channel.register(selector, SelectionKey.OP_READ);
			}
			else
			{
				System.out.println(this + "-------Connected to 127.0.0.1:8080 failed!");
				channel.register(selector, SelectionKey.OP_CONNECT);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

}
