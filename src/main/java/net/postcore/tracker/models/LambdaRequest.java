package net.postcore.tracker.models;

import java.util.Map;

public class LambdaRequest {
	
	Map<String, String> pathParameters;
	RequestContext requestContext;
	private String body;
	
	public Map<String, String> getPathParameters() {
		return pathParameters;
	}
	
	public void setPathParameters(Map<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}
	
	public RequestContext getRequestContext() {
		return requestContext;
	}
	
	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public String toString() {
		return "LambdaRequest [pathParameters=" + pathParameters + ", requestContext=" + requestContext + ", body="
				+ body + "]";
	}

}
