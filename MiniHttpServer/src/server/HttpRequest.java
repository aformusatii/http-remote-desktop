package server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpRequest {
	private String url;
	public HttpRequest(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getParameter(String name) throws UnsupportedEncodingException {
		int beginIndex = url.indexOf("?");
		String query = "";
		if (beginIndex > -1) {
			query = url.substring(beginIndex + 1);
			String[] params = query.split("&");
			for (String param : params) {
				String[] nameAndValue = param.split("=");
				if (name.equalsIgnoreCase(nameAndValue[0])) {
					if (nameAndValue.length < 2) {
						return null;
					} else {
						return URLDecoder.decode(nameAndValue[1], "UTF-8");
					}
				}
			}
		}
		return null;
	}
}
