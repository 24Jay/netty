package com.jay.netty.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer>
{

	private AsynchronousSocketChannel channel;

	public ReadCompletionHandler(AsynchronousSocketChannel channel)
	{
		if (this.channel == null)
			this.channel = channel;
	}

	public void completed(Integer result, ByteBuffer attachment)
	{
		attachment.flip();

		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try
		{
			String req = new String(body);

			System.out.println("AsyncTimeServer receive order: " + req);
			if ("Query Time Order".equalsIgnoreCase(req.trim()))
			{
				doWrite(new Date(System.currentTimeMillis()).toString());
			}
			else
			{
				doWrite("Bad Order!");
			}

		}
		catch (Exception e)
		{	
			e.printStackTrace();
		}

	}

	private void doWrite(String body)
	{
		if (body != null && body.trim().length() > 0)
		{
			byte[] bytes = body.getBytes();
			ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
			buffer.put(bytes);
			buffer.flip();
			channel.write(buffer, buffer, new WriteCompletionHandler(channel));
		}
	}

	public void failed(Throwable exc, ByteBuffer attachment)
	{
		try
		{
			this.channel.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
