package com.jay.netty.nio;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class TimeServer
{
	public static void main(String[] args) throws IOException
	{
		System.out.println("Time Server!");

		int port = 8080;
		if (args != null && args.length > 0)
		{
			try
			{
				port = Integer.valueOf(args[0]);

			}
			catch (Exception e)
			{
			}
		}

		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MutliplexerTimeServer-001").start();
	}
}
