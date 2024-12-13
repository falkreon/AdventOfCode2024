package blue.endless.advent;

import java.util.List;

import blue.endless.advent.util.ArrayGrid;

public class Day4 implements Day {

	public static final String SAMPLE =
			"""
			MMMSXXMASM
			MSAMXMSMSA
			AMXSXMAAMM
			MSAMASMSMX
			XMASAMXAMM
			XXAMMXXAMA
			SMSMSASXSS
			SAXAMASAAA
			MAMMMXMMMM
			MXMXAXMASX
			""";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	public boolean checkDirection(ArrayGrid<Character> grid, int x, int y, int dx, int dy, String str) {
		int ix = x;
		int iy = y;
		for(int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			char cur = grid.get(ix, iy);
			if (ch != cur) return false;
			
			ix += dx;
			iy += dy;
		}
		
		return true;
	}
	
	public int occurrencesStartingFrom(ArrayGrid<Character> grid, int x, int y, String str) {
		if (grid.get(x, y).charValue() != str.charAt(0)) return 0;
		
		int activations = 0;
		
		if (checkDirection(grid, x, y, -1,  0, str)) activations++;
		if (checkDirection(grid, x, y, -1, -1, str)) activations++;
		if (checkDirection(grid, x, y,  0, -1, str)) activations++;
		if (checkDirection(grid, x, y,  1, -1, str)) activations++;
		if (checkDirection(grid, x, y,  1,  0, str)) activations++;
		if (checkDirection(grid, x, y,  1,  1, str)) activations++;
		if (checkDirection(grid, x, y,  0,  1, str)) activations++;
		if (checkDirection(grid, x, y, -1,  1, str)) activations++;
		
		return activations;
	}
	
	public ArrayGrid<Character> gridIt(String input) {
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
	
	@Override
	public void a(String input) {
		ArrayGrid<Character> grid = gridIt(input);
		/*
		input = input.trim();
		
		List<String> lines =  input.lines().toList();
		ArrayGrid<Character> grid = new ArrayGrid<>(lines.get(0).length(), lines.size(), '.');
		grid.elementToString((it)->""+it, false);
		
		for(int y=0; y<grid.getHeight(); y++) {
			String line = lines.get(y);
			for(int x=0; x<grid.getWidth(); x++) {
				if (x < line.length()) grid.set(x, y, line.charAt(x));
			}
		}*/
		
		long total = 0;
		ArrayGrid<Integer> occurrences = new ArrayGrid<>(grid.getWidth(), grid.getHeight(), 0);
		occurrences.elementToString((it)->""+it, false);
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				int thisCell = occurrencesStartingFrom(grid, x, y, "XMAS");
				occurrences.set(x, y, thisCell);
				total += thisCell;
			}
		}
		
		System.out.println("Occurrence map:");
		System.out.println(occurrences);
		
		System.out.println();
		System.out.println("Total: "+total);
	}
	
	public boolean isMas(ArrayGrid<Character> grid, int x, int y) {
		//If the middle is not an 'A', it is not an X-MAS
		if (grid.get(x, y).charValue() != 'A') return false;
		
		//Get the 4 important neighbors
		char ul = grid.get(x-1, y-1).charValue();
		char ur = grid.get(x+1, y-1).charValue();
		char dl = grid.get(x-1, y+1).charValue();
		char dr = grid.get(x+1, y+1).charValue();
		
		//Match the X pattern
		boolean backslash = (ul == 'M' && dr == 'S') || (ul == 'S' && dr == 'M');
		boolean forwardslash = (ur == 'M' && dl == 'S') || (ur == 'S' && dl == 'M');
		
		return backslash && forwardslash;
	}

	@Override
	public void b(String input) {
		ArrayGrid<Character> grid = gridIt(input);
		
		long total = 0;
		ArrayGrid<Integer> occurrences = new ArrayGrid<>(grid.getWidth(), grid.getHeight(), 0);
		occurrences.elementToString((it)->""+it, false);
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				boolean thisCell = isMas(grid, x, y);
				if (thisCell) occurrences.set(x, y, 1);
				if (thisCell) total++;
			}
		}
		
		System.out.println("Occurrence map:");
		System.out.println(occurrences);
		
		System.out.println();
		System.out.println("Total: "+total);
	}

}
