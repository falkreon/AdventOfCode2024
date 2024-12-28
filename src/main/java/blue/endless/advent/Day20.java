package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Grid;
import blue.endless.advent.util.Pathfinding;
import blue.endless.advent.util.Strings;
import blue.endless.advent.util.Vec2i;

public class Day20 implements Day {
	
	@Override
	public String getSampleA() {
		return
				"""
				###############
				#...#...#.....#
				#.#.#.#.#.###.#
				#S#...#.#.#...#
				#######.#.#.###
				#######.#.#...#
				#######.#.###.#
				###..E#...#...#
				###.#######.###
				#...###...#...#
				#.#####.#.###.#
				#.#...#.#.#...#
				#.#.#.#.#.#.###
				#...#...#...###
				###############
				""";
	}
	
	public static List<Vec2i> adjacent(Grid<?> map, Vec2i pos) {
		ArrayList<Vec2i> result = new ArrayList<>();
		
		Vec2i w = pos.add(-1, 0);
		if (map.contains(w)) result.add(w);
		Vec2i e = pos.add(1, 0);
		if (map.contains(e)) result.add(e);
		Vec2i n = pos.add(0, -1);
		if (map.contains(n)) result.add(n);
		Vec2i s = pos.add(0, 1);
		if (map.contains(s)) result.add(s);
		
		return result;
	}
	
	@Override
	public void a(String input) {
		ArrayGrid<Character> map = ArrayGrid.of(input.trim());
		Vec2i start = null;
		Vec2i end = null;
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				char cur = map.get(x, y).charValue();
				if (cur == 'E') {
					end = new Vec2i(x, y);
					map.set(x, y, '.');
				} else if (cur == 'S') {
					start = new Vec2i(x, y);
					map.set(x, y, '.');
				}
			}
		}
		
		ArrayGrid<Integer> gradient = Pathfinding.createGradientMap(map, start, end, false);
		List<Vec2i> path = Pathfinding.pathFromGradient(gradient, start, end);
		
		System.out.println(Strings.sideBySide(map.toString(), gradient.toString()));
		//System.out.println(gradient);
		//System.out.println(path);
		
		TreeMap<Long, Long> savingsToRouteCount = new TreeMap<>();
		long savingsOf100OrMore = 0L;
		
		for(Vec2i cur : path) {
			long remainingSteps = gradient.get(cur).longValue();
			
			for(Vec2i neighbor : adjacent(map, cur)) {
				if (map.get(neighbor).charValue() != '#') continue; // We only benefit from cheating through walls
				for(Vec2i cheatEnd : adjacent(map, neighbor)) {
					if (cheatEnd.equals(cur)) continue;
					if (map.get(cheatEnd).charValue() != '.') continue; // We need to be back in-bounds on this step
					
					long cheatSteps = gradient.get(cheatEnd).longValue();
					long saved = remainingSteps - cheatSteps - 2; //We should add 2 here
					if (saved <= 0) continue;
					//System.out.println("Found cheat at "+neighbor+" that goes from "+remainingSteps+" to "+cheatSteps+" ("+saved+" saved)");
					long existing = savingsToRouteCount.getOrDefault(saved, 0L);
					savingsToRouteCount.put(saved, existing + 1L);
					
					if (saved >= 100L) savingsOf100OrMore++;
				}
			}
		}
		
		/*
		for(Map.Entry<Long, Long> entry : savingsToRouteCount.entrySet()) {
			if (entry.getValue() > 1L) {
				System.out.println("- There are "+entry.getValue()+" cheats that save "+entry.getKey()+" picoseconds.");
			} else if (entry.getValue() == 1L) {
				System.out.println("- There is one cheat that save "+entry.getKey()+" picoseconds.");
			} else {
				throw new IllegalStateException();
			}
		}*/
		
		System.out.println();
		System.out.println("There are "+savingsOf100OrMore+" cheats that save 100 picoseconds or more.");
	}
	
	@Override
	public void b(String input) {
		// TODO Auto-generated method stub
		
	}
	
}
