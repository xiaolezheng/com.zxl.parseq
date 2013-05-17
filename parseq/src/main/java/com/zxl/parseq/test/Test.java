package com.zxl.parseq.test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;

/** 
 * @title: Test.java
 * @description:
 * @author zhengxiaoluo
 * @version 1.0
 * @create 2013-5-16 下午3:30:18
 */
public class Test {
	private static Logger log = org.slf4j.LoggerFactory.getLogger(Test.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		final int numCores = Runtime.getRuntime().availableProcessors();
		log.debug("cpu cores is "+numCores);
		final ExecutorService taskScheduler = Executors.newFixedThreadPool(numCores + 1);
		final ScheduledExecutorService timerScheduler = Executors.newSingleThreadScheduledExecutor(); 

		final Engine engine = new EngineBuilder()
		        .setTaskExecutor(taskScheduler)
		        .setTimerScheduler(timerScheduler)
		        .build();
		
		final Task<Integer> task = Tasks.callable("length of 'test str'", new GetLengthTask("test str"));
		task.addListener(new PromiseListener<Integer>(){
			public void onResolved(Promise<Integer> promise){
				log.debug("Length : " + promise.get());
			}
		});
		task.addListener(new PromiseListener<Integer>(){
			public void onResolved(Promise<Integer> promise){
				log.debug("Length : " + promise.get());
			}
		});
		engine.run(task);
		log.debug("has submited");
		
		//task.await(3, TimeUnit.MILLISECONDS);
		//log.debug("Length : " + task.get());
		
		
		engine.shutdown();
		engine.awaitTermination(2, TimeUnit.SECONDS);
		taskScheduler.shutdown();
		timerScheduler.shutdown();
	}
	static class GetLengthTask implements Callable<Integer>
	{
	  private final String _string;
	  
	  public GetLengthTask(String string) {
	    _string = string;
	  }
	  
	  @Override
	  public Integer call() throws Exception {
		  try{
			  //Thread.sleep(1000 * 5);
		  }catch(Exception e){
			  log.error("", e);
		  }
	    return _string.length();
	  }
	}
}
