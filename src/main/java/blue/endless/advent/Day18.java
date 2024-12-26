package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Grid;
import blue.endless.advent.util.Pathfinding;
import blue.endless.advent.util.Vec2i;

public class Day18 implements Day {

	@Override
	public String getSampleA() {
		return
				"""
				5,4
				4,2
				4,5
				3,0
				2,1
				6,3
				2,4
				1,5
				0,6
				3,3
				2,6
				5,1
				1,2
				5,5
				2,5
				6,5
				1,4
				0,4
				6,4
				1,1
				6,1
				1,0
				0,5
				1,6
				2,0
				""";
	}
	
	public static List<Vec2i> parseInput(String input) {
		ArrayList<Vec2i> result = new ArrayList<>();
		
		for(String s : input.trim().lines().toList()) {
			String[] parts = s.split(",");
			result.add(new Vec2i(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
		}
		
		return result;
	}
	
	public void partOne(ArrayGrid<Character> map, Vec2i start, Vec2i end) {
		System.out.println(map);
		Grid<Integer> gradient = Pathfinding.createGradientMap(map, start, end, false);
		System.out.println(gradient);
		List<Vec2i> path = Pathfinding.pathFromGradient(gradient, start, end);
		System.out.println(path);
		
		ArrayGrid<Character> display = map.copy();
		for(Vec2i vec : path) {
			display.set(vec, 'O');
		}
		display.forEach((x, y, c) -> {
			if (c.charValue() == '#') display.set(x, y, (char) 0x2588);
		});
		
		System.out.println(display);
		System.out.println("Path length: "+path.size()+", Number of steps required: "+(path.size()-1));
	}
	
	@Override
	public void a(String input) {
		List<Vec2i> badSectors = parseInput(input);
		
		ArrayGrid<Character> map = new ArrayGrid<>(71, 71);
		map.setDefaultValue('.');
		map.clear();
		map.elementToString((it)->""+it.charValue(), false);
		
		for(int i=0; i<1024; i++) {
			if (i >= badSectors.size()) break;
			
			Vec2i badSector = badSectors.get(i);
			if (badSector.x() < 0 || badSector.y() < 0) System.out.println("Sector x/y too small! "+badSector);
			if (badSector.x() >= map.getWidth() || badSector.y() >= map.getHeight()) System.out.println("Sector x/y too big! "+badSector);
			map.set(badSectors.get(i), '#');
		}
		
		Vec2i start = new Vec2i(0, 0);
		Vec2i end = new Vec2i(70, 70);
		
		partOne(map, start, end);
	}

	@Override
	public void b(String input) {
		List<Vec2i> badSectors = parseInput(input);
		
		ArrayGrid<Character> map = new ArrayGrid<>(71, 71);
		map.setDefaultValue('.');
		map.clear();
		map.elementToString((it)->""+it.charValue(), false);
		
		Vec2i start = new Vec2i(0, 0);
		Vec2i end = new Vec2i(70, 70);
		
		System.out.println(map);
		int nanosElapsed = 0;
		
		for(Vec2i vec : badSectors) {
			map.set(vec, '#');
			nanosElapsed++;
			
			Grid<Integer> gradient = Pathfinding.createGradientMap(map, start, end, false);
			List<Vec2i> path = Pathfinding.pathFromGradient(gradient, start, end);
			if (path.isEmpty()) {
				System.out.println("Found total data blockage after "+nanosElapsed+" nanoseconds, from "+vec);
				return;
			}
		}
		
		System.out.println("No blocking data detected! Take as long as you want!");
	}
	
}
