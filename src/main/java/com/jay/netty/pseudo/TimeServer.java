package com.jay.netty.pseudo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.jay.netty.TimeServerHandler;

public class TimeServer
{

	public static void main(String[] ar) throws IOException
	{
		int port = 8080;
		ServerSocket server = null;
		try
		{
			server = new ServerSocket(port);
			System.out.println("Pseudo AIO timeServer start on port: " + port);
			Socket socket = null;
			TimeServerHandlerPool single = new TimeServerHandlerPool(50, 10000);

			while (true)
			{
				socket = server.accept();
				single.execute(new TimeServerHandler(socket));
			}

		}
		finally
		{
			if (server != null)
			{
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
	}
}
