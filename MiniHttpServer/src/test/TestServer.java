package test;

import server.HttpRequest;
import server.HttpResponse;
import server.HttpService;
import server.WebServer;

public class TestServer implements HttpService {
	
	public static void main(String args[]) {
		new WebServer(new TestServer());		
	}
	
	
	/* 
	 * Open: http://localhost:8080/getTest.action 
	 * */
	public void getTest(HttpRequest request, HttpResponse response) throws Exception {
		 response.append("Hello world!");
	}
}
