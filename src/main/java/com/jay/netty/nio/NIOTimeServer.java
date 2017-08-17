package com.jay.netty.nio;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class NIOTimeServer
{
	public static void main(String[] args) throws IOException
	{

		int port = 8080;

		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MutliplexerTimeServer-001").start();
	}
}
