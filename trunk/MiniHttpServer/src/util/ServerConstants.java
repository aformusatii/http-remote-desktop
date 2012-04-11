package util;

import java.io.FileInputStream;

public class ServerConstants {

	public static final java.util.Properties props = new java.util.Properties();
	public static final String CODEPAGE = "8859_1";
	public static final String STORAGE_CHARSET = "8859_1";
	public static final String CONTENT_CHARSET = "UTF-8";
	public static final String CRLF = "\r\n";
	public static final String CRLF2 = CRLF + CRLF;
	public static final String LF = "\n";
	public static final String LF2 = LF + LF;
	public static final String DIR_PREFIX = "DIR_";
	public static final int ERROR_STATUS = 0;
	public static final int SUCCESS_STATUS = 1;
	public static final int DEFAULT_FILES_PER_PAGE = 50;
	
	public enum Properties {
		PORT,
		ROOT,
		HTTP_ROOT,
		TIMEOUT,
		DEVELOPMENT("true");
		
		private String value;
		
		private Properties() {
			this.value = null;
		}
		
		private Properties(String value) {
			this.value = value;
		}		

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		public static void loadValues() {
			for (Properties prop : Properties.values()) {
				prop.value = props.getProperty(prop.name().toLowerCase().replaceAll("_", "."));				
			}
		}
	}	
	
	public static void loadConfig(String configFile) {
		try {
			props.load(new FileInputStream(configFile));
			Properties.loadValues();
		} catch (Exception e) {
			Utils.log(e);
		}		
	}
	
	public enum ContentType {
		IMAGE_PNG("image/png"),
		IMAGE_JPG("image/jpg"),
		TEXT_HTML("text/html");
		
		private String type;
		
		private ContentType(String type)  {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

}
