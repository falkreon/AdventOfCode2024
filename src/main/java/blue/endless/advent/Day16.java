package blue.endless.advent;

import java.util.ArrayDeque;

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
				""";
		*/
		
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
			if (value < existing) {
				set(dir, value);
				return true;
			} else {
				return false;
			}
			
		}
		
		@Override
		public String toString() {
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
	
	@Override
	public void a(String input) {
		ArrayGrid<Character> map = ArrayGrid.of(input.trim());
		ArrayGrid<Cell> costMap = new ArrayGrid<>(map.getWidth(), map.getHeight());
		costMap.elementToString(it->it.toString(), false);
		
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
				System.out.println("Enqueueing "+forward);
				queue.addFirst(forward);
				costMap.get(forward.pos()).setIfLower(forward.dir(), forward.cost());
			}
			
			Direction leftDir = next.dir().counterClockwise();
			Step left = new Step(next.pos().offset(leftDir), leftDir, next.cost() + 1001);
			if (shouldEnqueue(map, costMap, left)) {
				System.out.println("Enqueueing "+left);
				queue.addFirst(left);
				costMap.get(left.pos()).setIfLower(left.dir(), left.cost());
			}
			
			Direction rightDir = next.dir().clockwise();
			Step right = new Step(next.pos().offset(rightDir), rightDir, next.cost() + 1001);
			if (shouldEnqueue(map, costMap, right)) {
				System.out.println("Enqueueing "+right);
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
		
		System.out.println(map);
		System.out.println();
		System.out.println(costMap);
		
		Cell endCell = costMap.get(end);
		long cost = Math.min(endCell.n, endCell.e);
		if (cost == Long.MAX_VALUE) {
			System.out.println("Did not reach the end!");
		} else {
			System.out.println("Fastest route score: "+cost);
		}
	}

	@Override
	public void b(String input) {
		// TODO Auto-generated method stub
		
	}
	
}
