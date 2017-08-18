package com.jay.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeClient
{
	public static void main(String[] ar)
	{
		int port = 8080;
		// new Thread(new TimeClientHandle("localhost", port),
		// "TimeClient-001").start();

		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;

		for (int i = 0; i < 50000; i++)
		{
			try
			{
				socket = new Socket("127.0.0.1", port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println("Query Time Order");
				System.out.println("Send order to server succeed");
				String resp = in.readLine();
				System.out.println("Now is : " + resp);

			}
			catch (Exception e)
			{
				if (out != null)
				{
					out.close();
					out = null;
				}

				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					in = null;
				}

				if (socket != null)
				{
					try
					{
						socket.close();
					}
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					socket = null;
				}

			}
		}

	}
}
