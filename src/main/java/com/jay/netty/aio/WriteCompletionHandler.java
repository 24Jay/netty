package com.jay.netty.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer>
{

	private AsynchronousSocketChannel channel;

	public WriteCompletionHandler(AsynchronousSocketChannel channel)
	{
		if (this.channel == null)
			this.channel = channel;
	}

	public void completed(Integer result, ByteBuffer attachment)
	{
		if (attachment.hasRemaining())
			channel.write(attachment, attachment, this);
	}

	public void failed(Throwable exc, ByteBuffer attachment)
	{
		try
		{
			channel.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
