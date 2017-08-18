package com.jay.netty.aio;

public class AsyncTimeServer
{

	public static void main(String[] ar)
	{
		int port = 8080;

		AsyncTimeServerHandler server = new AsyncTimeServerHandler(port);

		new Thread(server).start();
	}
}
