package com.jay.netty.bio;

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
			System.out.println("Time server is start in port: " + port);
			Socket socket = null;
			while (true)
			{
				System.out.println("before server.accept()--------------");
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
				System.out.println("after server.accept()--------------");

			}
		}
		catch (IOException e)
		{
			if (server != null)
			{
				System.out.println("Time server closed!");
				server.close();
				server = null;
			}
		}

	}
}
