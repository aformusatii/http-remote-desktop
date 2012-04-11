package remotedesktop;

import java.awt.AWTException;
import java.awt.Robot;
import static java.awt.event.KeyEvent.*;

public class Keyboard {

    private Robot robot;

    public Keyboard() throws AWTException {
    	this.robot = new Robot();
    }

    public Keyboard(Robot robot) {
    	this.robot = robot;
    }

    public void type(CharSequence characters) {
    	int length = characters.length();
    	for (int i = 0; i < length; i++) {
    		char character = characters.charAt(i);
    		type(character);
    	}
    }
    
    public void type(int characters[]) {
    	int length = characters.length;
    	for (int i = 0; i < length; i++) {
    		int character = characters[i];
    		type(character);
    	}
    }

    public void type(int character) {
    	switch (character) {  	
    	case 'a': doType(VK_A); break;
    	case 'b': doType(VK_B); break;
    	case 'c': doType(VK_C); break;
    	case 'd': doType(VK_D); break;
    	case 'e': doType(VK_E); break;
    	case 'f': doType(VK_F); break;
    	case 'g': doType(VK_G); break;
    	case 'h': doType(VK_H); break;
    	case 'i': doType(VK_I); break;
    	case 'j': doType(VK_J); break;
    	case 'k': doType(VK_K); break;
    	case 'l': doType(VK_L); break;
    	case 'm': doType(VK_M); break;
    	case 'n': doType(VK_N); break;
    	case 'o': doType(VK_O); break;
    	case 'p': doType(VK_P); break;
    	case 'q': doType(VK_Q); break;
    	case 'r': doType(VK_R); break;
    	case 's': doType(VK_S); break;
    	case 't': doType(VK_T); break;
    	case 'u': doType(VK_U); break;
    	case 'v': doType(VK_V); break;
    	case 'w': doType(VK_W); break;
    	case 'x': doType(VK_X); break;
    	case 'y': doType(VK_Y); break;
    	case 'z': doType(VK_Z); break;
    	case 'A': doType(VK_SHIFT, VK_A); break;
    	case 'B': doType(VK_SHIFT, VK_B); break;
    	case 'C': doType(VK_SHIFT, VK_C); break;
    	case 'D': doType(VK_SHIFT, VK_D); break;
    	case 'E': doType(VK_SHIFT, VK_E); break;
    	case 'F': doType(VK_SHIFT, VK_F); break;
    	case 'G': doType(VK_SHIFT, VK_G); break;
    	case 'H': doType(VK_SHIFT, VK_H); break;
    	case 'I': doType(VK_SHIFT, VK_I); break;
    	case 'J': doType(VK_SHIFT, VK_J); break;
    	case 'K': doType(VK_SHIFT, VK_K); break;
    	case 'L': doType(VK_SHIFT, VK_L); break;
    	case 'M': doType(VK_SHIFT, VK_M); break;
    	case 'N': doType(VK_SHIFT, VK_N); break;
    	case 'O': doType(VK_SHIFT, VK_O); break;
    	case 'P': doType(VK_SHIFT, VK_P); break;
    	case 'Q': doType(VK_SHIFT, VK_Q); break;
    	case 'R': doType(VK_SHIFT, VK_R); break;
    	case 'S': doType(VK_SHIFT, VK_S); break;
    	case 'T': doType(VK_SHIFT, VK_T); break;
    	case 'U': doType(VK_SHIFT, VK_U); break;
    	case 'V': doType(VK_SHIFT, VK_V); break;
    	case 'W': doType(VK_SHIFT, VK_W); break;
    	case 'X': doType(VK_SHIFT, VK_X); break;
    	case 'Y': doType(VK_SHIFT, VK_Y); break;
    	case 'Z': doType(VK_SHIFT, VK_Z); break;
    	case '`': doType(VK_BACK_QUOTE); break;
    	case '0': doType(VK_0); break;
    	case '1': doType(VK_1); break;
    	case '2': doType(VK_2); break;
    	case '3': doType(VK_3); break;
    	case '4': doType(VK_4); break;
    	case '5': doType(VK_5); break;
    	case '6': doType(VK_6); break;
    	case '7': doType(VK_7); break;
    	case '8': doType(VK_8); break;
    	case '9': doType(VK_9); break;
    	case '-': doType(VK_MINUS); break;
    	case '=': doType(VK_EQUALS); break;
    	case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
    	case '!': doType(VK_EXCLAMATION_MARK); break;
    	case '@': doType(VK_AT); break;
    	case '#': doType(VK_NUMBER_SIGN); break;
    	case '$': doType(VK_SHIFT, VK_4, VK_SHIFT); break;
    	case '%': doType(VK_SHIFT, VK_5, VK_SHIFT); break;
    	case '^': doType(VK_SHIFT, VK_6, VK_SHIFT); break;
    	case '&': doType(VK_SHIFT, VK_7, VK_SHIFT); break;
    	case '*': doType(VK_SHIFT, VK_8, VK_SHIFT); break;
    	case '(': doType(VK_SHIFT, VK_9, VK_SHIFT); break;
    	case ')': doType(VK_SHIFT, VK_0, VK_SHIFT); break;
    	case '_': doType(VK_UNDERSCORE); break;
    	case '+': doType(VK_PLUS); break;
    	case '\t': doType(VK_TAB); break;
    	case '\n': doType(VK_ENTER); break;
    	case 13: doType(VK_ENTER); break;
    	case '[': doType(VK_OPEN_BRACKET); break;
    	case ']': doType(VK_CLOSE_BRACKET); break;
    	case '\\': doType(VK_BACK_SLASH); break;
    	case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
    	case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
    	case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
    	case ';': doType(VK_SEMICOLON); break;
    	case ':': doType(VK_SHIFT, VK_SEMICOLON, VK_SHIFT); break;
    	//case '\'': doType(VK_QUOTE); break;
    	case '"': doType(VK_QUOTEDBL); break;
    	case ',': doType(VK_COMMA); break;
    	case '<': doType(VK_LESS); break;
    	case '.': doType(VK_PERIOD); break;
    	case '>': doType(VK_GREATER); break;
    	case '/': doType(VK_SLASH); break;
    	case '?': doType(VK_SHIFT, VK_SLASH); break;
    	case ' ': doType(VK_SPACE); break;
    	case (37 - 10): doType(VK_LEFT); break;
    	case (38 - 10): doType(VK_UP); break;
    	case (39 - 10): doType(VK_RIGHT); break;
    	case (40 - 10): doType(VK_DOWN); break;
    	
    	case (4114): doType(VK_LEFT); break;
    	case (4115): doType(VK_UP); break;
    	case (4116): doType(VK_RIGHT); break;
    	case (4117): doType(VK_DOWN); break;
    	case (4100): doType(VK_ENTER); break;
    	case (4128): doType(VK_SHIFT); break;
    	case (4129): doType(VK_CONTROL); break;
    	case (4132): doType(VK_CAPS_LOCK); break;
    	case (4118): doType(VK_PAGE_UP); break;
    	case (4119): doType(VK_PAGE_DOWN); break;
    	case (4113): doType(VK_END); break;
    	case (4112): doType(VK_HOME); break;
    	case (4099): doType(VK_BACK_SPACE); break;
    	
    	default:
    		robot.keyPress(character);
    		//throw new IllegalArgumentException("Cannot type character " + character);
    	}
    }

    private void doType(int... keyCodes) {
    	doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
    	if (length == 0) {
    		return;
    	}

    	try {
			robot.keyPress(keyCodes[offset]);
			doType(keyCodes, offset + 1, length - 1);
			robot.keyRelease(keyCodes[offset]);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " Key Code: " + keyCodes[offset]);
			e.printStackTrace();
		}
    }

}
