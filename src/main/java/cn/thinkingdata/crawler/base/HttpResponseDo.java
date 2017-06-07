package cn.thinkingdata.crawler.base;

import java.util.Map;

public class HttpResponseDo {
	private String responseBody;
	private Map<String, String> responseHeaderMap;
	public HttpResponseDo(String responseBody,
			Map<String, String> responseHeaderMap) {
		super();
		this.responseBody = responseBody;
		this.responseHeaderMap = responseHeaderMap;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	public Map<String, String> getResponseHeaderMap() {
		return responseHeaderMap;
	}
	public void setResponseHeaderMap(Map<String, String> responseHeaderMap) {
		this.responseHeaderMap = responseHeaderMap;
	}
	
}
