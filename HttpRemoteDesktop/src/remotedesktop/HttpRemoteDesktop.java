package remotedesktop;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import server.HttpRequest;
import server.HttpResponse;
import server.HttpService;
import server.WebServer;
import util.ServerConstants.ContentType;
import util.Utils;

public class HttpRemoteDesktop implements HttpService {
	
	private static String REMOTE_TEMPLATE;
	private Robot robot;
	private WebServer webServer;
	private Keyboard keyboard;
	private int screenHeight = 0;
	private int screenWidth = 0;

	public static void main(String[] args) {
		new HttpRemoteDesktop();
	}
	
	public HttpRemoteDesktop() {
		try {
			webServer = new WebServer(this, "config/rd.properties");
			robot = new Robot();
			init();
		} catch (Exception e) {
			Utils.log(e);
		}
	}
	
	private void init() throws IOException, AWTException {
		File f = new File(webServer.getRoot(), "remote.html");
		REMOTE_TEMPLATE = Utils.readAll(f);
		keyboard = new Keyboard();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = ((Double) screen.getHeight()).intValue();
		screenWidth =  ((Double) screen.getWidth()).intValue();
		System.out.println("Screen size: [" + screenHeight + "x" + screenWidth + "]");
	}
	
	/* ============ Http Methods ============ */
	public void getScreenshot(HttpRequest request, HttpResponse response) throws Exception {
		 System.out.println("** ========================== **");
		 long start = System.currentTimeMillis();
		 
		 String imgType = request.getParameter("imgType");
		 String zoomStr = request.getParameter("zoom");
		 String dWStr = request.getParameter("dWidth");
		 String dHStr = request.getParameter("dHeight");
		 String lxStr = request.getParameter("lx");
		 String lyStr = request.getParameter("ly");		 
		 
		 Rectangle screenRect;
		 
		 int captureW = screenWidth;
		 int captureH = screenHeight;
		 int zoom = 0;
		 
		 if (Utils.isNotEmpty(zoomStr)) {
			 zoom = Integer.valueOf(zoomStr);
		 }
		 
		 if (Utils.isNotEmpty(dWStr) && Utils.isNotEmpty(dHStr)) {
			 int w = Integer.valueOf(dWStr);
			 int h = Integer.valueOf(dHStr);
			 
			 captureW = w + zoom;
			 captureH = h + zoom;
			 
			 if (captureW > screenWidth) {
				 captureW = screenWidth;
			 }
			 if (captureH > screenHeight) {
				 captureH = screenHeight;
			 }
		 } 
		 
		 /* ============================================================================ */
		 String xStr = request.getParameter("x");
		 String yStr = request.getParameter("y");	 
		 String event = request.getParameter("event");
		 String which = request.getParameter("which");
		 String keyCodes = request.getParameter("keyCodes");
		 
		 System.out.println("event [" + event + "] "
				 + "x [" + xStr + "] "
				 + "y [" + yStr + "] "
				 + "which [" + which + "] ");
		 
		 if (Utils.isNotEmpty(event)) {
			 if (Utils.isNotEmpty(xStr) && Utils.isNotEmpty(yStr)) {
				 int x = Integer.valueOf(xStr);
				 int y = Integer.valueOf(yStr);				 

				 int lx = Integer.valueOf(lxStr);
				 int ly = Integer.valueOf(lyStr);				 
				 
				 if (zoom > 0) {
					 int wd = Integer.valueOf(dWStr);
					 int hd = Integer.valueOf(dHStr);

					 x = (captureW * x)/wd;
					 y = (captureH * y)/hd;					 
				 }
				 
				 x += lx;
				 y += ly;
				 
				 robot.mouseMove(x, y);
			 }
			 
			 if (Utils.isNotEmpty(keyCodes)) {
				 String[] codes = keyCodes.split(",");
				 int[] data = new int[codes.length];
				 int l = 0;
				 for (String code : codes) {
					if (Utils.isNotEmpty(code)) {
						data[l++] = Integer.valueOf(code);
					}
				}
				 
				//System.out.println(v);
				keyboard.type(Arrays.copyOf(data, l));				 
			 }
			 
			 if (Utils.isNotEmpty(which)) {
				 if ("mousedown".equalsIgnoreCase(event)) {
					 robot.mousePress(getMouseButtonByCode(which));
				 } else if ("mouseup".equalsIgnoreCase(event)) {
					 robot.mouseRelease(getMouseButtonByCode(which));
				 }
			 }
		 }
		 /* ============================================================================ */
		 
		 screenRect = new Rectangle(captureW, captureH);
		 if (Utils.isNotEmpty(lxStr) && Utils.isNotEmpty(lyStr)) {
			 int x = Integer.valueOf(lxStr);
			 int y = Integer.valueOf(lyStr);
			 screenRect.setLocation(x, y);
		 }

		 BufferedImage capture = robot.createScreenCapture(screenRect);

		 System.out.println("Capture: " + (System.currentTimeMillis() - start));
		 start = System.currentTimeMillis();
		 
		 if ((zoom > 0) && Utils.isNotEmpty(dWStr) && Utils.isNotEmpty(dHStr)) {
			 int w = Integer.valueOf(dWStr);
			 int h = Integer.valueOf(dHStr);			 
			 
			 int type = capture.getType() == 0? BufferedImage.TYPE_INT_ARGB : capture.getType();
			 capture = resizeImageWithHint(capture, type, w, h);
		 }
		 
		 response.setContentType(ContentType.IMAGE_JPG);
		 
		 System.out.println("Resize: " + (System.currentTimeMillis() - start));
		 start = System.currentTimeMillis();
		 
		 imgType = (Utils.isEmpty(imgType) ? "jpg" : imgType);
		 
		 ImageIO.write(capture, imgType, response.getContent());
		 System.out.println("Write: " + (System.currentTimeMillis() - start));
	}
	
	private int getMouseButtonByCode(String which) {
		if ("1".equals(which)) {
			return InputEvent.BUTTON1_MASK;
		} else if ("2".equals(which)) {
			return InputEvent.BUTTON2_MASK;
		} else if ("3".equals(which)) {
			return InputEvent.BUTTON3_MASK;
		}
		throw new IllegalStateException("Unknown mouse button code: " + which);
	}
	
	public void getRemoteDesktop(HttpRequest request, HttpResponse response) throws Exception {
		init();
		response.setContentType(ContentType.TEXT_HTML);
		String content =  REMOTE_TEMPLATE
			.replaceAll("%screenHeight%", String.valueOf(screenHeight))
			.replaceAll("%screenWidth%", String.valueOf(screenWidth));
		response.append(content);
	}
	
	/* ============ End Http Methods ============ */
	
    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int w, int h){
    	 
    	BufferedImage resizedImage = new BufferedImage(w, h, type);
    	Graphics2D g = resizedImage.createGraphics();
    	g.drawImage(originalImage, 0, 0, w, h, null);
    	g.dispose();
    	/* g.setComposite(AlphaComposite.Src);
     
    	g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
    	RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    	g.setRenderingHint(RenderingHints.KEY_RENDERING,
    	RenderingHints.VALUE_RENDER_QUALITY);
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    	RenderingHints.VALUE_ANTIALIAS_ON); */
     
    	return resizedImage;
    }

}
