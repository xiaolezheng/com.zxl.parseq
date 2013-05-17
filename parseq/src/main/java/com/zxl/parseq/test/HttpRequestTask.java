package com.zxl.parseq.test;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import com.ning.http.client.AsyncHttpClient;

public class HttpRequestTask extends BaseTask<Response> {
	private final Request _request;

	public HttpRequestTask(Request request) {
		super("Http request: " + request);
		_request = request;
	}

	@Override
	protected Promise<Response> run(final Context context) throws Exception {
		// Create a settable promise. We'll use this to signal completion of
		// this
		// task once the response is received from the HTTP client.
		final SettablePromise<Response> promise = Promises.settable();

		// Send the request and register a callback with the client that will
		// set the response on our promise.
		
		AsyncHttpClient client = new AsyncHttpClient();
		
		client.prepareRequest(_request).execute(
				new AsyncCompletionHandler<Response>() {
					@Override
					public Response onCompleted(final Response response)
							throws Exception {
						// At this point the HTTP client has given us the HTTP
						// response
						// asynchronously. We set the response value on our
						// promise to indicate
						// that the task is complete.
						promise.done(response);
						return response;
					}

					@Override
					public void onThrowable(final Throwable t) {
						// If there was an error then we should set it on the
						// promise.
						promise.fail(t);
					}
				});

		// Return the promise to the ParSeq framework. It may or may not be
		// resolved by the time we return this promise.
		return promise;
	}
}