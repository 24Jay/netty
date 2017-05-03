package com.jay.netty;

public class TImeClient
{
	public static void main(String[] ar)
	{
		int port = 8080;
		new Thread(new TimeClientHandle("localhost", port), "TimeClient-001").start();
	}
}
