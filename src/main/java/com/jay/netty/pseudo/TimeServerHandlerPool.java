package com.jay.netty.pseudo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandlerPool
{

	private ExecutorService executor;

	public TimeServerHandlerPool(int maxPoolSize, int queueSize)
	{
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 120, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(queueSize));
	}

	public void execute(Runnable task)
	{
		executor.execute(task);
	}

}
