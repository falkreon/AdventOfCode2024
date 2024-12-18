package blue.endless.advent.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ArrayGrid<T> implements Grid<T> {
	private T[] data;
	private int width;
	private int height;
	private T defaultValue = null;
	private Function<T, String> toString = this::altToString;
	private boolean commas = true;
	
	public ArrayGrid(int width, int height) {
		this(width, height, null);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayGrid(int width, int height, T defaultValue) {
		data = (T[]) new Object[width * height];
		Arrays.fill(data, defaultValue);
		this.defaultValue = defaultValue;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public T get(int x, int y) {
		if (x<0 || y<0 || x>=width || y>=height) return defaultValue;
		return data[y * width + x];
	}
	
	@Override
	public void set(int x, int y, T value) {
		if (x<0 || y<0 || x>=width || y>=height) return;
		data[y * width + x] = value;
	}
	
	public void setDefaultValue(T value) {
		this.defaultValue = value;
	}
	
	public void elementToString(Function<T, String> func, boolean commas) {
		this.toString = func;
		this.commas = commas;
	}
	
	@Override
	public void clear(int x, int y) {
		set(x, y, defaultValue);
	}
	
	@Override
	public void clear() {
		Arrays.fill(data, defaultValue);
	}
	
	public ArrayGrid<T> copy() {
		ArrayGrid<T> result = new ArrayGrid<T>(width, height);
		result.defaultValue = this.defaultValue;
		result.commas = this.commas;
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				result.set(x, y, this.get(x, y));
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				result.append(toString.apply(get(x,y)));
				if (commas) result.append(',');
				//result.append(altToString(get(x,y)));
			}
			result.append("\n");
		}
		
		if (result.length() > 0) {
			result.deleteCharAt(result.length()-1); //Delete last newline
			if (commas) result.deleteCharAt(result.length()-1); //Delete last comma
		}
		
		return result.toString();
	}
	
	private String altToString(T o) {
		if (o instanceof Boolean) {
			if (o.equals(Boolean.TRUE)) return "#";
			return ".";
		} else {
			return (o==null) ? "null" : o.toString();
		}
	}
	
	public static ArrayGrid<Character> of(String input) {
		List<String> lines =  input.trim().lines().toList();
		ArrayGrid<Character> grid = new ArrayGrid<>(lines.get(0).length(), lines.size(), '.');
		grid.elementToString((it)->""+it, false);
		
		for(int y=0; y<grid.getHeight(); y++) {
			String line = lines.get(y);
			for(int x=0; x<grid.getWidth(); x++) {
				if (x < line.length()) grid.set(x, y, line.charAt(x));
			}
		}
		
		return grid;
	}
	
	public static ArrayGrid<Integer> ofNumeric(String input) {
		List<String> lines =  input.trim().lines().toList();
		ArrayGrid<Integer> grid = new ArrayGrid<>(lines.get(0).length(), lines.size(), 0);
		grid.elementToString((it)->""+it, false);
		
		for(int y=0; y<grid.getHeight(); y++) {
			String line = lines.get(y);
			for(int x=0; x<grid.getWidth(); x++) {
				if (x < line.length()) {
					char cur = line.charAt(x);
					try {
						grid.set(x, y, Integer.parseInt(""+cur));
					} catch (NumberFormatException ex) {
						// do nothing
					}
				}
			}
		}
		
		return grid;
	}
}
