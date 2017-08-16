package com.jay.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable
{

	private int port;

	CountDownLatch latch;

	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncTimeServerHandler(int port)
	{
		this.port = port;

		try
		{
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("Asynchronous Time Server is start on port: " + port);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		latch = new CountDownLatch(1);
		doAccept();
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void doAccept()
	{
		asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
	}

}
