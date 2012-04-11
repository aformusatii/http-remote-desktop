package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import util.ServerConstants;
import util.ServerConstants.ContentType;

public class HttpResponse {
	private ByteArrayOutputStream content = new ByteArrayOutputStream();
	private ContentType contentType = ContentType.TEXT_HTML;

	public ByteArrayOutputStream getContent() {
		return content;
	}
	public void setContent(ByteArrayOutputStream content) {
		this.content = content;
	}
	public ContentType getContentType() {
		return contentType;
	}
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	public void append(String content) throws UnsupportedEncodingException, IOException {
		this.content.write(content.getBytes(ServerConstants.CONTENT_CHARSET));
	}
}
