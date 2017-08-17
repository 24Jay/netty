package com.jay.netty.nio;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class NIOTimeClient
{

	public static void main(String[] ar) throws IOException
	{

		int cont = 500;
		Selector selector = Selector.open();
		new Thread(new SelectorHandler(selector, cont)).start();
		;
		for (int i = 0; i < cont; i++)
		{

			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			new ConnectionHandler(selector, socketChannel);
			// new Thread(new MyTimeClientHandler("127.0.0.1", 8080)).start();
		}

	}
}
