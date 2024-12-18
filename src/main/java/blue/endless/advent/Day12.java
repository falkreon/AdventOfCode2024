package blue.endless.advent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Vec2i;

public class Day12 implements Day {
	
	public static final String SAMPLE =
			"""
			RRRRIICCFF
			RRRRIICCCF
			VVRRRCCFFF
			VVRCCCJFFF
			VVVVCJJCFE
			VVIVCCJJEE
			VVIIICJJEE
			MIIIIIJJEE
			MIIISIJEEE
			MMMISSJEEE
			""";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	@Override
	public String getSampleB() {
		return
				"""
				AAAAAA
				AAABBA
				AAABBA
				ABBAAA
				ABBAAA
				AAAAAA
				""";
	}
	
	public record Region(char flower, ArrayList<Vec2i> coverage) {
		public Region(char flower) {
			this(flower, new ArrayList<>());
		}
		
		public boolean isAdjacent(Vec2i vec) {
			return isAdjacent(vec.x(), vec.y());
		}
		
		public boolean isAdjacent(int x, int y) {
			for(Vec2i vec : coverage) {
				if (vec.x() == x && (Math.abs(vec.y() - y) == 1)) return true;
				if (vec.y() == y && (Math.abs(vec.x() - x) == 1)) return true;
			}
			return false;
		}
		
		public void incorporate(int x, int y) {
			coverage.add(new Vec2i(x, y));
		}
		
		public String display() {
			int dx = Integer.MAX_VALUE;
			int dy = Integer.MAX_VALUE;
			int maxX = 0;
			int maxY = 0;
			
			for(Vec2i vec : coverage) {
				if (vec.x() < dx) dx = vec.x();
				if (vec.y() < dy) dy = vec.y();
				if (vec.x() > maxX) maxX = vec.x();
				if (vec.y() > maxY) maxY = vec.y();
			}
			
			ArrayGrid<Character> display = new ArrayGrid<>(maxX - dx + 1, maxY - dy + 1);
			display.setDefaultValue('.');
			display.clear();
			display.elementToString((it)->""+it, false);
			
			for(Vec2i vec : coverage) {
				display.set(vec.x() - dx, vec.y() - dy, flower);
			}
			
			return display.toString();
		}
		
		public boolean contains(int x, int y) {
			for(Vec2i vec : coverage) {
				if (vec.x() == x && vec.y() == y) return true;
			}
			return false;
		}
		
		public boolean contains(Vec2i vec) {
			return coverage.contains(vec);
		}
		
		public long price() {
			Set<Vec2i> coverageCopy = new HashSet<>(coverage);
			long area = 0L;
			long perimeter = 0L;
			for(Vec2i vec : coverageCopy) {
				area++;
				if (!contains(vec.x()-1, vec.y())) perimeter++;
				if (!contains(vec.x()+1, vec.y())) perimeter++;
				if (!contains(vec.x(), vec.y()-1)) perimeter++;
				if (!contains(vec.x(), vec.y()+1)) perimeter++;
			}
			return area * perimeter;
		}
		
		/**
		 * Most people are going to employ a perimeter-walking algorithm. I thought about it, and today
		 * is all about greedy algorithms - perimeter-walking changes pretty dramatically with each
		 * edge orientation in an error-prone way, and I felt that *using* a greedy approach could
		 * scale very nicely to any number of dimensions.
		 * 
		 * @return The number of "merged" sides in this shape.
		 */
		public int sides() {
			/*
			 * These subregion lists include only the points inside the region but on the edge in that
			 * direction. So the north list *only* contains points that are inside the shape, but whose
			 * y-minus neighbor is outside.
			 * 
			 * If you think about it, the list of edges facing that direction is dual to the list of
			 * contiguous point regions with those restrictions.
			 */
			ArrayList<Region> north = new ArrayList<>();
			ArrayList<Region> south = new ArrayList<>();
			ArrayList<Region> east = new ArrayList<>();
			ArrayList<Region> west = new ArrayList<>();
			
			for(Vec2i vec : coverage) {
				if (!contains(vec.x()-1, vec.y())) Day12.incorporate(west, vec.x(), vec.y(), flower);
				if (!contains(vec.x()+1, vec.y())) Day12.incorporate(east, vec.x(), vec.y(), flower);
				if (!contains(vec.x(), vec.y()-1)) Day12.incorporate(north, vec.x(), vec.y(), flower);
				if (!contains(vec.x(), vec.y()+1)) Day12.incorporate(south, vec.x(), vec.y(), flower);
			}
			
			return north.size() + south.size() + east.size() + west.size();
		}
	}
	
	public static void incorporate(List<Region> regions, int x, int y, char flower) {
		List<Region> adjacentRegions = new ArrayList<>();
		
		for(Region r : regions) {
			if (r.flower() == flower && r.isAdjacent(x, y)) adjacentRegions.add(r);
		}
		
		if (adjacentRegions.size() == 1) {
			//System.out.println("Incorporating '"+flower+"' into existing region.");
			adjacentRegions.get(0).incorporate(x, y);
		} else if (adjacentRegions.size() > 1) {
			//System.out.println("Using '"+flower+"' to merge regions.");
			Region merged = new Region(flower);
			merged.coverage().add(new Vec2i(x, y));
			for(Region r : adjacentRegions) {
				for(Vec2i vec : r.coverage()) merged.coverage().add(vec);
				regions.remove(r);
			}
			regions.add(merged);
		} else {
			//System.out.println("Creating new region for '"+flower+"' at "+x+", "+y);
			Region r = new Region(flower);
			r.incorporate(x, y);
			regions.add(r);
		}
	}
	
	@Override
	public void a(String input) {
		ArrayGrid<Character> grid = ArrayGrid.of(input.trim());
		ArrayList<Region> regions = new ArrayList<>();
		
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				incorporate(regions, x, y, grid.get(x, y).charValue());
			}
		}
		
		long total = 0L;
		System.out.println("Regions:");
		for(Region r : regions) {
			//System.out.println(r.display());
			long price = r.price();
			total += price;
			System.out.println("'"+r.flower+"': "+price);
		}
		
		System.out.println();
		System.out.println("Total: "+total);
	}

	@Override
	public void b(String input) {
		ArrayGrid<Character> grid = ArrayGrid.of(input.trim());
		ArrayList<Region> regions = new ArrayList<>();
		
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				incorporate(regions, x, y, grid.get(x, y).charValue());
			}
		}
		
		long total = 0L;
		for (Region r : regions) {
			long sides = r.sides();
			long area = r.coverage().size();
			
			System.out.println("Region '"+r.flower+"': "+sides+" sides x "+area+" area = "+ (sides*area));
			
			total += sides * area;
		}
		
		System.out.println("Part 2 (Discount) Cost: "+total);
	}

}
