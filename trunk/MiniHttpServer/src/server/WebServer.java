package server;

import static util.ServerConstants.Properties.HTTP_ROOT;
import static util.ServerConstants.Properties.PORT;
import static util.ServerConstants.Properties.ROOT;
import static util.ServerConstants.Properties.TIMEOUT;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Hashtable;

import util.ServerConstants;
import util.Utils;

public class WebServer implements HttpConstants, Runnable {
	static final int BUF_SIZE = 2048;
    static final byte[] EOL = {(byte)'\r', (byte)'\n' };	
	
	private HttpService httpService;
	private File root;
	private int timeout = 0;
	private int port = 8080;
	private String http_root= "index.html";

	public WebServer(HttpService httpService, String configFile) {
		ServerConstants.loadConfig(configFile);
		this.httpService = httpService;
		init();
	}	
	
	public WebServer(HttpService httpService) {
		this.httpService = httpService;
		init();
	}
	
	private void init() {
		Thread t = new Thread(this);
		t.start();		
	}
	
	public File getRoot() {
		return root;
	}
	
	@Override
	public void run() {
		try {
			loadProps();
			ServerSocket ss = new ServerSocket(port, 1000);
			while (true) {
			    Socket s = ss.accept();
			    new WebServer.Worker(s);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private void loadProps() throws IOException {
        String r = ROOT.getValue();
        if (r != null) {
            root = new File(r);
            if (!root.exists()) {
            	//root.mkdirs();
                throw new Error(root + " doesn't exist as server root");
            }
            Utils.log("Root path=" + root.getAbsolutePath());            
        }
        r = TIMEOUT.getValue();
        if (r != null) {
            timeout = Integer.parseInt(r);
        }
        r = PORT.getValue();
        if (r != null) {
            port = Integer.parseInt(r);
        }

        /* if no properties were specified, choose defaults */
        if (root == null) {
            root = new File(System.getProperty("user.dir"));
        }
        if (timeout <= 1000) {
            timeout = 5000;
        }
        r = HTTP_ROOT.getValue();
        if (r != null) {
        	http_root = r;
        }
        
    	Utils.log("port="+port);
        Utils.log("root="+root);
        Utils.log("timeout="+timeout);        
    }
    
    class Worker implements HttpConstants, Runnable {

        /* buffer to use for requests */
        byte[] buf;
        /* Socket to client we're handling */
        private Socket s;

        protected Worker(Socket s) {
            buf = new byte[BUF_SIZE];
            this.s = s;
            Thread t = new Thread(this);
            t.start();
        }

        public synchronized void run() {
            try {
                handleClient();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        }

        void handleClient() throws IOException {
            InputStream is = new BufferedInputStream(s.getInputStream());
            PrintStream ps = new PrintStream(s.getOutputStream());
            /* we will only block in read for this many milliseconds
             * before we fail with java.io.InterruptedIOException,
             * at which point we will abandon the connection.
             */
            s.setSoTimeout(timeout);
            s.setTcpNoDelay(true);
            /* zero out the buffer from last time */
            for (int i = 0; i < BUF_SIZE; i++) {
                buf[i] = 0;
            }
            try {
                /* We only support HTTP GET/HEAD, and don't
                 * support any fancy HTTP options,
                 * so we're only interested really in
                 * the first line.
                 */
                int nread = 0, r = 0;

    outerloop:
                while (nread < BUF_SIZE) {
                    r = is.read(buf, nread, BUF_SIZE - nread);
                    if (r == -1) {
                        /* EOF */
                        return;
                    }
                    int i = nread;
                    nread += r;
                    for (; i < nread; i++) {
                        if (buf[i] == (byte)'\n' || buf[i] == (byte)'\r') {
                            /* read one line */
                            break outerloop;
                        }
                    }
                }

                /* are we doing a GET or just a HEAD */
                boolean doingGet;
                /* beginning of file name */
                int index;
                if (buf[0] == (byte)'G' &&
                    buf[1] == (byte)'E' &&
                    buf[2] == (byte)'T' &&
                    buf[3] == (byte)' ') {
                    doingGet = true;
                    index = 4;
                } else if (buf[0] == (byte)'H' &&
                           buf[1] == (byte)'E' &&
                           buf[2] == (byte)'A' &&
                           buf[3] == (byte)'D' &&
                           buf[4] == (byte)' ') {
                    doingGet = false;
                    index = 5;
                } else {
                    /* we don't support this method */
                    ps.print("HTTP/1.0 " + HTTP_BAD_METHOD +
                               " unsupported method type: ");
                    ps.write(buf, 0, 5);
                    ps.write(EOL);
                    ps.flush();
                    s.close();
                    return;
                }

                int i = 0;
                /* find the file name, from:
                 * GET /foo/bar.html HTTP/1.0
                 * extract "/foo/bar.html"
                 */
                for (i = index; i < nread; i++) {
                    if (buf[i] == (byte)' ') {
                        break;
                    }
                }
                String fname = (new String(buf, 0, index, i-index)).replace('/', File.separatorChar);
                
                if (fname.toLowerCase().contains(".action")) {
                	int beginIndex = 1;
                	int endIndex = fname.indexOf(".action");
                	String action = fname.substring(beginIndex, endIndex).trim();
                	try {
                        Class params[] = {HttpRequest.class, HttpResponse.class};
                        HttpResponse response = new HttpResponse();
                        Object paramsObj[] = {new HttpRequest(fname), response};
                        
                		Method thisMethod = httpService.getClass().getDeclaredMethod(action, params);
                		thisMethod.invoke(httpService, paramsObj);
                		printHeaders(response, ps);
                		ps.write(EOL);
                		ps.write(response.getContent().toByteArray());
                		ps.flush();
    				} catch (Exception e) {
    					printHeaders(null, ps);
    					send404(null, ps);
    					e.printStackTrace(); 
    				}

                } else if (Utils.isEmpty(fname) || fname.trim().equals("\\")) {
                	printRedirectHeaders(ps, http_root);
                	
                } else {
    	            if (fname.startsWith(File.separator)) {
    	                fname = fname.substring(1);
    	            }
    	            File targ = new File(root, fname);
    	            if (targ.isDirectory()) {
    	                File ind = new File(targ, "index.html");
    	                if (ind.exists()) {
    	                    targ = ind;
    	                }
    	            }
    	            boolean OK = printFileHeaders(targ, ps);
    	            if (doingGet) {
    	                if (OK) {
    	                    sendFile(targ, ps);
    	                } else {
    	                    send404(targ, ps);
    	                }
    	            }

                }
                ps.flush();
            } finally {
            	try {
                	ps.close();            		
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	try {
            		s.close();
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}                
            }
        }
        
        void printRedirectHeaders(PrintStream ps, String location) throws IOException {
            ps.print("HTTP/1.0 " + HTTP_MOVED_PERM + " Moved Permanently");
            ps.write(EOL);
            ps.print("Location: " + location);
            ps.write(EOL);
            ps.print("Server: Mini Server");
            ps.write(EOL);
            ps.print("Content-Length: 0");
            ps.write(EOL);
            ps.print("Date: " + (new Date()));
            ps.write(EOL);
        }
        
        void printHeaders(HttpResponse response, PrintStream ps) throws IOException {
        	if (response == null) {
                ps.print("HTTP/1.0 " + HTTP_NOT_FOUND + " not found");
                ps.write(EOL);
    	        ps.print("Server: Mini Server");
    	        ps.write(EOL);
    	        ps.print("Date: " + (new Date()));
    	        ps.write(EOL);            
        	} else {
    	        ps.print("HTTP/1.0 " + HTTP_OK + " OK");
    	        ps.write(EOL);    
    	        ps.print("Server: Mini Server");
    	        ps.write(EOL);
    	        ps.print("Date: " + (new Date()));
    	        ps.write(EOL);	        
    	        ps.print("Content-length: "+response.getContent().toByteArray().length);
    	        ps.write(EOL);
    	        ps.print("Content-type: " + response.getContentType().getType());
    	        ps.write(EOL);
        	}
        }

        boolean printFileHeaders(File targ, PrintStream ps) throws IOException {
            boolean ret = false;
            int rCode = 0;
            if (!targ.exists()) {
                rCode = HTTP_NOT_FOUND;
                ps.print("HTTP/1.0 " + HTTP_NOT_FOUND + " not found");
                ps.write(EOL);
                ret = false;
            }  else {
                rCode = HTTP_OK;
                ps.print("HTTP/1.0 " + HTTP_OK+" OK");
                ps.write(EOL);
                ret = true;
            }
            Utils.log("From " +s.getInetAddress().getHostAddress()+": GET " +
                targ.getAbsolutePath()+"-->"+rCode);
            ps.print("Server: Mini Server");
            ps.write(EOL);
            /* Cache */
            if ((ServerConstants.Properties.DEVELOPMENT.getValue() != null) 
            		&& !ServerConstants.Properties.DEVELOPMENT.getValue().equals("true") 
            		&& Boolean.FALSE.equals(targ.isDirectory())) {
    	        ps.print("Last-Modified: Mon, 14 Nov 2011 05:14:22 GMT");
    	        ps.write(EOL);
    	        ps.print("Cache-Control: public, max-age=630658897");
    	        ps.write(EOL);
    	        ps.print("Expires: Fri, 14 Nov 2031 08:00:09 GMT");
    	        ps.write(EOL);
            }
            /* End Cache */
            ps.print("Date: " + (new Date()));
            ps.write(EOL);
            if (ret) {
                if (!targ.isDirectory()) {
                    ps.print("Content-length: "+targ.length());
                    ps.write(EOL);
                    ps.print("Last Modified: " + (new
                                  Date(targ.lastModified())));
                    ps.write(EOL);
                    String name = targ.getName();
                    int ind = name.lastIndexOf('.');
                    String ct = null;
                    if (ind > 0) {
                        ct = (String) map.get(name.substring(ind));
                    }
                    if (ct == null) {
                        ct = "unknown/unknown";
                    }
                    ps.print("Content-type: " + ct);
                    ps.write(EOL);
                } else {
                    ps.print("Content-type: text/html");
                    ps.write(EOL);
                }
            }
            return ret;
        }

        void send404(File targ, PrintStream ps) throws IOException {
            ps.write(EOL);
            ps.write(EOL);
            ps.println("Not Found\n\n"+
                       "The requested resource was not found.\n");
        }

        void sendFile(File targ, PrintStream ps) throws IOException {
            InputStream is = null;
            ps.write(EOL);
            if (targ.isDirectory()) {
                listDirectory(targ, ps);
                return;
            } else {
                is = new FileInputStream(targ.getAbsolutePath());
            }

            try {
                int n;
                while ((n = is.read(buf)) > 0) {
                    ps.write(buf, 0, n);
                }
            } finally {
                is.close();
            }
        }

        void listDirectory(File dir, PrintStream ps) throws IOException {
            ps.println("<TITLE>Directory listing</TITLE><P>\n");
            ps.println("<A HREF=\"..\">Parent Directory</A><BR>\n");
            String[] list = dir.list();
            for (int i = 0; list != null && i < list.length; i++) {
                File f = new File(dir, list[i]);
                if (f.isDirectory()) {
                    ps.println("<A HREF=\""+list[i]+"/\">"+list[i]+"/</A><BR>");
                } else {
                    ps.println("<A HREF=\""+list[i]+"\">"+list[i]+"</A><BR");
                }
            }
            ps.println("<P><HR><BR><I>" + (new Date()) + "</I>");
        }    	
    	
    }
    
    /* mapping of file extensions to content-types */
    static Hashtable<String, String> map = new java.util.Hashtable<String, String>();

    static {
        fillMap();
    }
    static void setSuffix(String k, String v) {
        map.put(k, v);
    }

    static void fillMap() {
        setSuffix("", "content/unknown");
        setSuffix(".uu", "application/octet-stream");
        setSuffix(".exe", "application/octet-stream");
        setSuffix(".ps", "application/postscript");
        setSuffix(".zip", "application/zip");
        setSuffix(".sh", "application/x-shar");
        setSuffix(".tar", "application/x-tar");
        setSuffix(".snd", "audio/basic");
        setSuffix(".au", "audio/basic");
        setSuffix(".wav", "audio/x-wav");
        setSuffix(".gif", "image/gif");
        setSuffix(".jpg", "image/jpeg");
        setSuffix(".jpeg", "image/jpeg");
        setSuffix(".png", "image/png");
        setSuffix(".htm", "text/html");
        setSuffix(".html", "text/html");
        setSuffix(".text", "text/plain");
        setSuffix(".c", "text/plain");
        setSuffix(".cc", "text/plain");
        setSuffix(".c++", "text/plain");
        setSuffix(".h", "text/plain");
        setSuffix(".pl", "text/plain");
        setSuffix(".txt", "text/plain");
        setSuffix(".java", "text/plain");
        setSuffix(".js", "application/x-javascript");
        setSuffix(".css", "text/css");
    }    
    
}

interface HttpConstants {
    /** 2XX: generally "OK" */
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_NOT_AUTHORITATIVE = 203;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_RESET = 205;
    public static final int HTTP_PARTIAL = 206;

    /** 3XX: relocation/redirect */
    public static final int HTTP_MULT_CHOICE = 300;
    public static final int HTTP_MOVED_PERM = 301;
    public static final int HTTP_MOVED_TEMP = 302;
    public static final int HTTP_SEE_OTHER = 303;
    public static final int HTTP_NOT_MODIFIED = 304;
    public static final int HTTP_USE_PROXY = 305;

    /** 4XX: client error */
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_BAD_METHOD = 405;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_PROXY_AUTH = 407;
    public static final int HTTP_CLIENT_TIMEOUT = 408;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_GONE = 410;
    public static final int HTTP_LENGTH_REQUIRED = 411;
    public static final int HTTP_PRECON_FAILED = 412;
    public static final int HTTP_ENTITY_TOO_LARGE = 413;
    public static final int HTTP_REQ_TOO_LONG = 414;
    public static final int HTTP_UNSUPPORTED_TYPE = 415;

    /** 5XX: server error */
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_INTERNAL_ERROR = 501;
    public static final int HTTP_BAD_GATEWAY = 502;
    public static final int HTTP_UNAVAILABLE = 503;
    public static final int HTTP_GATEWAY_TIMEOUT = 504;
    public static final int HTTP_VERSION = 505;
}