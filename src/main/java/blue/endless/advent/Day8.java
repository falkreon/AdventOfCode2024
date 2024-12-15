package blue.endless.advent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Combinations;
import blue.endless.advent.util.Grid;
import blue.endless.advent.util.Vec2i;

public class Day8 implements Day {
	
	public static String SAMPLE =
			"""
			............
			........0...
			.....0......
			.......0....
			....0.......
			......A.....
			............
			............
			........A...
			.........A..
			............
			............
			""";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	public Map<Character, List<Vec2i>> getLogicalMap(Grid<Character> grid) {
		Map<Character, List<Vec2i>> result = new HashMap<>(); // If we had guava this would be a multimap
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				Character signal = grid.get(x,y);
				if (signal.charValue() == '.') continue;
				Vec2i loc = new Vec2i(x, y);
				
				if (result.containsKey(signal)) {
					result.get(signal).add(loc);
				} else {
					ArrayList<Vec2i> list = new ArrayList<>();
					list.add(loc);
					result.put(signal, list);
				}
			}
		}
		
		return result;
	}

	@Override
	public void a(String input) {
		ArrayGrid<Character> grid = ArrayGrid.of(input.trim());
		
		// Examine the grid to create a logical map of signals and towers
		Map<Character, List<Vec2i>> logicalTowerMap = getLogicalMap(grid);
		
		System.out.println(logicalTowerMap);
		System.out.println();
		
		Set<Vec2i> nodes = new HashSet<>();
		
		for(Map.Entry<Character, List<Vec2i>> entry : logicalTowerMap.entrySet()) {
			
			System.out.println("Examining signal "+entry.getKey());
			
			ArrayGrid<Character> display = new ArrayGrid<>(grid.getWidth(), grid.getHeight(), '.');
			display.elementToString((it)->""+it.charValue(), false);
			for(Vec2i v : entry.getValue()) display.set(v, entry.getKey());
			
			Combinations.streamTwo(entry.getValue()).forEach((towers) -> {
				System.out.println("  Examining towers "+towers+" for antinodes within map.");
				int dx = towers.right().x() - towers.left().x();
				int dy = towers.right().y() - towers.left().y();
				
				Vec2i nodeB = towers.right().add(dx, dy);
				Vec2i nodeA = towers.left().subtract(dx, dy);
				if (nodeA.x() >= 0 && nodeA.y() >= 0 && nodeA.x() < grid.getWidth() && nodeA.y() < grid.getHeight()) {
					display.set(nodeA, '#');
					nodes.add(nodeA);
				}
				if (nodeB.x() >= 0 && nodeB.y() >= 0 && nodeB.x() < grid.getWidth() && nodeB.y() < grid.getHeight()) {
					display.set(nodeB, '#');
					nodes.add(nodeB);
				}
			});
			
			System.out.println(display);
		}
		
		System.out.println("Total nodes: "+nodes.size());
		
	}

	@Override
	public void b(String input) {
		ArrayGrid<Character> grid = ArrayGrid.of(input.trim());
		
		// Examine the grid to create a logical map of signals and towers
		Map<Character, List<Vec2i>> logicalTowerMap = getLogicalMap(grid);
		
		System.out.println(logicalTowerMap);
		System.out.println();
		
		Set<Vec2i> nodes = new HashSet<>();
		
		for(Map.Entry<Character, List<Vec2i>> entry : logicalTowerMap.entrySet()) {
			
			System.out.println("Examining signal "+entry.getKey());
			
			ArrayGrid<Character> display = new ArrayGrid<>(grid.getWidth(), grid.getHeight(), '.');
			display.elementToString((it)->""+it.charValue(), false);
			for(Vec2i v : entry.getValue()) display.set(v, entry.getKey());
			
			Combinations.streamTwo(entry.getValue()).forEach((towers) -> {
				//Each pair of towers is always sitting on resonant nodes - we can add them here because they'll dedupe in the set
				nodes.add(towers.left());
				nodes.add(towers.right());
				
				System.out.println("  Examining towers "+towers+" for antinodes within map.");
				int dx = towers.right().x() - towers.left().x();
				int dy = towers.right().y() - towers.left().y();
				
				int maxStepsX = (grid.getWidth() / dx) + 1;
				int maxStepsY = (grid.getHeight() / dy) + 1;
				int maxSteps = Math.max(maxStepsX, maxStepsY);
				
				for(int i=0; i<maxSteps; i++) {
					Vec2i nodeB = towers.right().add(dx * i, dy * i);
					Vec2i nodeA = towers.left().subtract(dx * i, dy * i);
				
					if (nodeA.x() >= 0 && nodeA.y() >= 0 && nodeA.x() < grid.getWidth() && nodeA.y() < grid.getHeight()) {
						display.set(nodeA, '#');
						nodes.add(nodeA);
					}
					if (nodeB.x() >= 0 && nodeB.y() >= 0 && nodeB.x() < grid.getWidth() && nodeB.y() < grid.getHeight()) {
						display.set(nodeB, '#');
						nodes.add(nodeB);
					}
				}
			});
			
			System.out.println(display);
		}
		
		System.out.println("Total nodes: "+nodes.size());
	}

}
