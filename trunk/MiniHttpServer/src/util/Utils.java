package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Utils {
	
    /* print to stdout */
    public static void log(String s) {
        System.out.println(s);
    }
    
    public static void log(Exception e) {
        e.printStackTrace();
    }    
	
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
	
    public static boolean isEmpty(String value) {
    	return (value == null) || ("".equals(value.trim()));
    }
    
    public static boolean isNotEmpty(String value) {
    	return (value != null) && (!"".equals(value.trim()));
    }
    
    public static boolean areNotEmptyAll(String... values) {
    	for (String value : values) {
    		if (isEmpty(value)) {
    			return false;
    		}
    	}
    	return true;
    }	    
    
	public static String getSubString(String value, String beginStr, String endStr) {
		
		if (beginStr != null) {
			int beginIndex = value.indexOf(beginStr);
			if (beginIndex > -1) {
				value = value.substring(beginIndex + beginStr.length());
			}
		}
		
		if (endStr != null) {
			int endIndex = value.indexOf(endStr);
			if (endIndex > -1) {
				value = value.substring(0, endIndex);
			}
		}
		
		return value;
	}
	
	public static String cleanHtml(String html) {
		if (html == null) {
			return "";
		}
		return html.replaceAll("\r", "").replaceAll("\n", " ").replaceAll("\\&nbsp\\;", " ").trim();
	}
	
	public static String cleanHtmlLines(String html) {
		if (html == null) {
			return "";
		}
		return html.replaceAll("\r", "").replaceAll("\n", ",").replaceAll("\\&nbsp\\;", " ").trim();		
	}
	
	public static String clearNull(String value) {
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}
	
    /**
     * Read all Content from InputStream and return it as String.
     * 
     * @param in InputStream to read.
     * @return String obtained.
     * @throws IOException read error.
     */
    public static String readAll(InputStream in) throws IOException {
        if (in != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = reader.readLine()) != null) {
               sb.append(line).append("\n");
            }
            return sb.toString();
        } else {
            return "";
        }
    }
    
    /**
     * Read all Content from File and return it as String.
     * 
     * @param in InputStream to read.
     * @return String obtained.
     * @throws IOException read error.
     */    
    public static String readAll(File file) throws IOException {
    	FileInputStream fis = null;
    	try {
    		fis = new FileInputStream(file);
        	return readAll(fis);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    }    
}