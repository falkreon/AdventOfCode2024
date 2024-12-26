package blue.endless.advent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Direction;
import blue.endless.advent.util.Vec2i;

public class Day16 implements Day {

	@Override
	public String getSampleA() {
		/*
		return
				"""
				###############
				#.......#....E#
				#.#.###.#.###.#
				#.....#.#...#.#
				#.###.#####.#.#
				#.#.#.......#.#
				#.#.#####.###.#
				#...........#.#
				###.#.#####.#.#
				#...#.....#.#.#
				#.#.#.###.#.#.#
				#.....#...#.#.#
				#.###.#.#.#.#.#
				#S..#.....#...#
				###############
				""";*/
		
		
		return
				"""
				#################
				#...#...#...#..E#
				#.#.#.#.#.#.#.#.#
				#.#.#.#...#...#.#
				#.#.#.#.###.#.#.#
				#...#.#.#.....#.#
				#.#.#.#.#.#####.#
				#.#...#.#.#.....#
				#.#.#####.#.###.#
				#.#.#.......#...#
				#.#.###.#####.###
				#.#.#...#.....#.#
				#.#.#.#####.###.#
				#.#.#.........#.#
				#.#.#.#########.#
				#S#.............#
				#################
				""";
	}
	
	public static record Step(Vec2i pos, Direction dir, long cost) {}
	
	public static class Cell {
		// MAX_VALUE here is a sentinel value indicating this cell has not been observed being entered by traveling in that direction.
		// It also has the desirable property of any reasonable numeric value being less than it.
		public long n = Long.MAX_VALUE;
		public long s = Long.MAX_VALUE;
		public long e = Long.MAX_VALUE;
		public long w = Long.MAX_VALUE;
		
		public long get(Direction dir) {
			return switch(dir) {
				case NORTH -> n;
				case EAST -> e;
				case SOUTH -> s;
				case WEST -> w;
			};
		}
		
		public void set(Direction dir, long value) {
			switch(dir) {
				case NORTH -> n = value;
				case EAST -> e = value;
				case SOUTH -> s = value;
				case WEST -> w = value;
			}
		}
		
		public boolean setIfLower(Direction dir, long value) {
			long existing = get(dir);
			if (value <= existing) {
				set(dir, value);
				return true;
			} else {
				return false;
			}
		}
		
		public int entranceCount() {
			int result = 0;
			
			if (n != Long.MAX_VALUE) result++;
			if (s != Long.MAX_VALUE) result++;
			if (e != Long.MAX_VALUE) result++;
			if (w != Long.MAX_VALUE) result++;
			
			return result;
		}
		
		public int optimalEntranceCount() {
			long cost = entranceCost();
			if (cost == Long.MAX_VALUE) return 0;
			
			int count = 0;
			if (n == cost || n == cost + 1000L) count++;
			if (e == cost || e == cost + 1000L) count++;
			if (s == cost || s == cost + 1000L) count++;
			if (w == cost || w == cost + 1000L) count++;
			
			return count;
		}
		
		public long entranceCost() {
			long cost = Long.MAX_VALUE;
			if (n < cost) cost = n;
			if (e < cost) cost = e;
			if (s < cost) cost = s;
			if (w < cost) cost = w;
			
			return cost;
		}
		
		public Set<Step> getEntranceSteps(Vec2i pos) {
			Set<Step> result = new HashSet<>();
			if (w != Long.MAX_VALUE) result.add(new Step(pos, Direction.WEST, w));
			if (e != Long.MAX_VALUE) result.add(new Step(pos, Direction.EAST, e));
			if (n != Long.MAX_VALUE) result.add(new Step(pos, Direction.NORTH, n));
			if (s != Long.MAX_VALUE) result.add(new Step(pos, Direction.SOUTH, s));
			return result;
		}
		
		public void clear() {
			n = Long.MAX_VALUE;
			e = Long.MAX_VALUE;
			s = Long.MAX_VALUE;
			w = Long.MAX_VALUE;
		}
		
		@Override
		public String toString() {
			
			if (optimalEntranceCount() > 1) return "+";
			
			if (n < e && n < s && n < w) {
				return "^";
			}
			if (e < s && e < w) {
				return ">";
			}
			if (s < w) {
				return "v";
			}
			
			if (w == Long.MAX_VALUE) {
				return "#";
			}
			
			return "<";
		}

		public String debug() {
			StringBuilder result = new StringBuilder();
			
			result.append("{ ");
			result.append("n: "+n);
			result.append(", e: "+e);
			result.append(", s: "+s);
			result.append(", w: "+w);
			result.append(" }");
			
			return result.toString();
		}
	}
	
	public boolean shouldEnqueue(ArrayGrid<Character> map, ArrayGrid<Cell> costMap, Step step) {
		// Can the spot be entered at all?
		if (map.get(step.pos()).charValue() == '.') {
			// Is the current proposed Step the lowest-cost known way to entere that spot from that side?
			if (step.cost() < costMap.get(step.pos()).get(step.dir())) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayGrid<Cell> createCostMap(ArrayGrid<Character> map, Vec2i start, Vec2i end) {
		ArrayGrid<Cell> costMap = new ArrayGrid<>(map.getWidth(), map.getHeight());
		costMap.elementToString(it->it.toString(), false);
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				costMap.set(x, y, new Cell());
			}
		}
		
		ArrayDeque<Step> queue = new ArrayDeque<>();
		queue.addFirst(new Step(start, Direction.EAST, 0));
		
		long lastDisplay = System.nanoTime();
		
		while(!queue.isEmpty()) {
			Step next = queue.removeLast();
			
			/*
			 * We can do three things from each step:
			 * - Move forward, at a cost of 1
			 * - Rotate counterclockwise and move forward, at a cost of 1001
			 * - Rotate clockwise and move forward, at a cost of 1001
			 * - Rotate 180 degrees and move forward (as three moves: CW, CW, Forward), at a cost of 2001. This is unlikely to be useful after the first step.
			 *   It's also not useful at the first step, since we're always placed at the bottom-left corner of the maze.
			 * 
			 * On visiting a tile, we record the lowest cost to visit the tile moving the specified direction.
			 * 
			 * When we're done, we should have either 1 or 2 costs for visiting the "E" tile, depending on how many sides it's reachable from. Our problem
			 * answer is the lower of these two "final" costs.
			 */
			
			// Check forward
			Step forward = new Step(next.pos().offset(next.dir()), next.dir(), next.cost() + 1);
			if (shouldEnqueue(map, costMap, forward)) {
				//System.out.println("Enqueueing "+forward);
				queue.addFirst(forward);
				costMap.get(forward.pos()).setIfLower(forward.dir(), forward.cost());
			}
			
			Direction leftDir = next.dir().counterClockwise();
			Step left = new Step(next.pos().offset(leftDir), leftDir, next.cost() + 1001);
			if (shouldEnqueue(map, costMap, left)) {
				//System.out.println("Enqueueing "+left);
				queue.addFirst(left);
				costMap.get(left.pos()).setIfLower(left.dir(), left.cost());
			}
			
			Direction rightDir = next.dir().clockwise();
			Step right = new Step(next.pos().offset(rightDir), rightDir, next.cost() + 1001);
			if (shouldEnqueue(map, costMap, right)) {
				//System.out.println("Enqueueing "+right);
				queue.addFirst(right);
				costMap.get(right.pos()).setIfLower(right.dir(), right.cost());
			}
			
			
			long elapsed = (System.nanoTime() - lastDisplay) / 1_000_000L;
			if (elapsed > 1_000L) {
				lastDisplay = System.nanoTime();
				System.out.println("Queue size: "+queue.size());
				System.out.println(costMap);
			}
		}
		
		return costMap;
	}
	
	@Override
	public void a(String input) {
		ArrayGrid<Character> map = ArrayGrid.of(input.trim());
		
		Vec2i start = null;
		Vec2i end = null;
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				char cur = map.get(x, y).charValue();
				if (cur == 'S') {
					map.set(x, y, '.');
					start = new Vec2i(x, y);
				} else if (cur == 'E') {
					map.set(x, y, '.');
					end = new Vec2i(x, y);
				}
			}
		}
		
		ArrayGrid<Cell> costMap = createCostMap(map, start, end);
		
		
		//System.out.println(map);
		//System.out.println();
		System.out.println(costMap);
		
		Cell endCell = costMap.get(end);
		long cost = Math.min(endCell.n, endCell.e);
		if (cost == Long.MAX_VALUE) {
			System.out.println("Did not reach the end!");
		} else {
			System.out.println("Fastest route score: "+cost);
		}
	}
	
	public static String debugMap(ArrayGrid<Character> map, ArrayGrid<Cell> costMap) {
		StringBuilder result = new StringBuilder();
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				char ch = map.get(x, y).charValue();
				Cell c = costMap.get(x, y);
				
				switch(ch) {
					case '#' -> result.append(ch);
					case '.' -> {
						if (c.entranceCount() == 0) {
							result.append(ch);
						} else {
							result.append(c.toString());
						}
					}
					default -> result.append(ch);
				}
			}
			result.append('\n');
		}
		
		return result.toString();
	}
	
	public boolean check(Step input, Step output) {
		if (input.dir() == output.dir()) {
			return output.cost() == input.cost() + 1L;
		}
		
		if (output.dir() == input.dir().clockwise() || output.dir == input.dir().counterClockwise()) {
			return output.cost() == input.cost() + 1001L;
		}
		
		return false; // Turning around is never optimal unless the start location is facing a wall.
	}
	
	@Override
	public void b(String input) {
		/**
		 * To recover the optimal path(s) from our gradient, we will need to prune all non-optimal paths.
		 * 
		 * One option is to, for each Cell, erase all entry directions which do not have the lowest cost of all
		 * entry directions (a cell may be entered from two directions with the same cost). Because of the
		 * turning cost, however, the resulting path may not be globally optimal.
		 * 
		 * We will define a pruning operation as the following:
		 * 
		 * - If there is only one entry direction into the cell, and the cell is not the Start or Exit Cell,
		 *   delete all path information in the Cell (reset it to the sentinal value of Long.MAX_VALUE)
		 * 
		 * - Check the cell in the opposite direction of travel (the cell we moved *into* this cell from),
		 *   and if it has one remaining entry direction, delete its contents as well.
		 * 
		 * - Repeat until you reach a cell with more than one point of entry. This is not a recursive operation,
		 *   just a simple while loop.
		 * 
		 * Before the first pruning step, we will "snip" all links into the Exit Cell with non-optimal cost.
		 * 
		 * Finally, if the neighboring cells never enter *from this cell*, it is also a dead end.
		 */
		
		ArrayGrid<Character> map = ArrayGrid.of(input.trim());
		
		Vec2i start = null;
		Vec2i end = null;
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				char cur = map.get(x, y).charValue();
				if (cur == 'S') {
					map.set(x, y, '.');
					start = new Vec2i(x, y);
				} else if (cur == 'E') {
					map.set(x, y, '.');
					end = new Vec2i(x, y);
				}
			}
		}
		
		ArrayGrid<Cell> costMap = createCostMap(map, start, end);
		costMap.setDefaultValue(new Cell());
		
		System.out.println(costMap);
		
		Cell endCell = costMap.get(end);
		long cost = Math.min(endCell.n, endCell.e);
		// Snip snip
		if (endCell.n != cost) endCell.n = Long.MAX_VALUE;
		if (endCell.e != cost) endCell.e = Long.MAX_VALUE;
		if (cost == Long.MAX_VALUE) {
			System.out.println("Did not reach the end!");
			return;
		} else {
			System.out.println("Fastest route score: "+cost);
		}
		
		System.out.println("Extracting optimal paths...");
		
		ArrayGrid<Character> optimal = map.copy();
		
		ArrayDeque<Vec2i> queue = new ArrayDeque<>();
		queue.addLast(end);
		while (!queue.isEmpty()) {
			Vec2i cur = queue.removeFirst();
			if (map.get(cur).charValue() == '.') { // Sanity check; should always succeed
				optimal.set(cur, 'O');
				
				if (!cur.equals(start)) {
					Cell costs = costMap.get(cur);
					long curCost = costs.entranceCost();
					if (curCost == Long.MAX_VALUE) throw new IllegalStateException();
					
					Set<Step> entranceSteps = costs.getEntranceSteps(cur);
					for(Step s : entranceSteps) {
						// Only process this step if its cost *could* make it the optimal route.
						// This prunes almost all routes!
						// ....... but not enough routes! It's still not only optimal routes!
						// ....... and maybe prunes too much! Manually deleting extraneous routes doesn't seem to be a fix!
						if (s.cost() == curCost || s.cost() == curCost + 1000L) {
							
							Vec2i neighbor = cur.offset(s.dir().opposite());
							for(Step r : costMap.get(neighbor).getEntranceSteps(neighbor)) {
								//System.out.println("Checking "+r+" -> "+s);
								if (check(r, s)) {
									queue.addLast(neighbor);
								}
							}
							//for(Step )
							
						}
					}
				}
			} else {
				System.out.println("Failed sanity check with '"+map.get(cur).charValue()+"'");
			}
			
			
		}
		
		optimal.set(start, 'O');
		
		long result = 0L;
		for(int y=0; y<optimal.getHeight(); y++) {
			for(int x=0; x<optimal.getWidth(); x++) {
				if (optimal.get(x, y).charValue() == 'O') result++;
				if (optimal.get(x, y).charValue() == '#') optimal.set(x, y, (char) 0x2588);
			}
		}
		System.out.println(optimal);
		System.out.println("Cells along optimal path(s): "+result);
		
		//NOTE: THIS DOES NOT WORK
	}
	
}
