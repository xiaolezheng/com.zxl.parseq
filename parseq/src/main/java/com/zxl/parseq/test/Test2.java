package com.zxl.parseq.test;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.ParTask;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.promise.Promise;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

/**
 * @title: Test2.java
 * @description:
 * @author zhengxiaoluo
 * @version 1.0
 * @create 2013-5-16 下午8:10:59
 */
public class Test2 {
	private static Logger log = org.slf4j.LoggerFactory.getLogger(Test2.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) { 
		// TODO Auto-generated method stub
		final Task<String> googleContentType = getContentType("http://www.baidu.com");
		final Task<String> bingContentType = getContentType("http://www.bing.com");
		final Task<String> yahooContentType = getContentType("http://www.yahoo.com");
		final ParTask<String> fetchContentTypes = Tasks.par(googleContentType, bingContentType, yahooContentType);
		
		List<String> list = fetchContentTypes.getSuccessful();
		
		for(String content: list){
			log.debug(content);
		}
		
	}

	public static Task<String> getContentType(final String url) {
		return new BaseTask<String>("Get content type: " + url) {
			@Override
			protected Promise<String> run(final Context context)
					throws Exception {
				// We only need to make a HEAD request to get the Content-Type
				// header.
				final Request request = new RequestBuilder().setUrl(url)
						.setMethod("HEAD").build();

				// Create a task to make the HTTP request
				final Task<Response> httpResponse = new HttpRequestTask(request);

				// Create a task to extract the Content-Type header from the
				// response
				final Task<String> contentType = Tasks.callable(
						"Extract content type: " + url, new Callable<String>() {
							@Override
							public String call() throws Exception {
								return httpResponse.get().getContentType();
							}
						});

				// Sequence the tasks
				final Task<String> plan = Tasks.seq(httpResponse, contentType);
				context.run(plan);
				return plan;
			}
		};
	}
}
