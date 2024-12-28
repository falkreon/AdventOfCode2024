package blue.endless.advent.util;

import java.util.List;

public class Strings {
	public static String removePrefix(String s, String toRemove) {
		if (!s.startsWith(toRemove)) throw new IllegalArgumentException("Expected: \""+toRemove+"\", Found: \""+s+"\"");
		return s.substring(toRemove.length());
	}
	
	public static String sideBySide(String a, String b) {
		StringBuilder result = new StringBuilder();
		
		List<String> aLines = a.lines().toList();
		List<String> bLines = b.lines().toList();
		
		int aWidth = 0;
		for(String s : aLines) if (s.length() > aWidth) aWidth = s.length();
		
		int lines = Math.max(aLines.size(), bLines.size());
		for(int i=0; i<lines; i++) {
			String as = (i<aLines.size()) ? aLines.get(i) : "";
			String bs = (i<bLines.size()) ? bLines.get(i) : "";
			
			if (as.length() < aWidth) as = as + " ".repeat(aWidth - as.length());
			result.append(as);
			result.append(' ');
			result.append(bs);
			result.append('\n');
		}
		
		return result.toString();
	}
}
