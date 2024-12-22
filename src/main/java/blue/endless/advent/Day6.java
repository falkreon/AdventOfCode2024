package blue.endless.advent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Direction;
import blue.endless.advent.util.Grid;
import blue.endless.advent.util.Vec2i;

public class Day6 implements Day {
	
	public static final String SAMPLE =
			"""
			....#.....
			.........#
			..........
			..#.......
			.......#..
			..........
			.#..^.....
			........#.
			#.........
			......#...
			""";
	
	public static final String PATROLS = "^>v<";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	public static class Patrol {
		public int x;
		public int y;
		public Direction dir;
		
		public Patrol(int x, int y, Direction dir) {
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
		
		public void simulate(Grid<Character> grid, boolean markPath) {
			int nextX = x + dir.dx();
			int nextY = y + dir.dy();
			char path = grid.get(nextX, nextY);
			if (path == '#') {
				// Turn instead
				dir = dir.clockwise();
			} else {
				x = nextX;
				y = nextY;
				if (markPath) {
					grid.set(x, y, 'X');
				}
			}
		}
		
		public boolean simulatePartTwo(Grid<Character> grid) {
			int nextX = x + dir.dx();
			int nextY = y + dir.dy();
			char path = grid.get(nextX, nextY);
			if (path == '#' || path == 'O') {
				// Turn instead
				dir = dir.clockwise();
				grid.set(x, y, '+');
				
				return true;
			} else {
				x = nextX;
				y = nextY;
				
				char existing = grid.get(x,y);
				if (existing == '.') {
					grid.set(x, y, dir.axisMarker());
				} else {
					if (existing != dir.axisMarker()) {
						grid.set(x, y, '+');
					}
				}
				return false;
			}
		}
		
		public Patrol copy() {
			return new Patrol(x, y, dir);
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Patrol p) {
				return x == p.x && y == p.y && dir == p.dir;
			} else {
				return false;
			}
		}
		
		@Override
		public final int hashCode() {
			return Objects.hash(x, y, dir);
		}
	}

	@Override
	public void a(String input) {
		ArrayList<Patrol> patrols = new ArrayList<>();
		
		ArrayGrid<Character> grid = ArrayGrid.of(input);
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				if (PATROLS.indexOf(grid.get(x, y)) != -1) {
					patrols.add(new Patrol(x, y, Direction.of(grid.get(x, y))));
					grid.set(x, y, 'X');
				}
			}
		}
		
		boolean keepRunning = true;
		while (keepRunning) {
			Iterator<Patrol> i = patrols.iterator();
			while(i.hasNext()) {
				Patrol p = i.next();
				p.simulate(grid, true);
				//System.out.println("Patrol now at "+p.x+", "+p.y);
				if (p.x < 0 || p.x >= grid.getWidth() || p.y < 0 || p.y >= grid.getHeight()) i.remove();
			}
			
			if (patrols.isEmpty()) break;
		}
		
		System.out.println(grid.toString());
		
		int pathTotal = 0;
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				if (grid.get(x, y).charValue() == 'X') pathTotal++;
			}
		}
		
		System.out.println("Total visited: "+pathTotal);
	}

	public boolean subSim(ArrayGrid<Character> grid, Patrol initial, HashSet<Patrol> bonks) {
		Patrol patrol = initial.copy();
		
		while (true) {
			
			if (patrol.simulatePartTwo(grid)) {
				Patrol bonk = new Patrol(patrol.x, patrol.y, patrol.dir.counterClockwise());
				if (bonks.contains(bonk)) return false;
				bonks.add(bonk);
			}
			
			if (patrol.x < 0 || patrol.x >= grid.getWidth() || patrol.y < 0 || patrol.y >= grid.getHeight()) return true;
		}
	}
	
	@Override
	public void b(String input) {
		Patrol initialPatrol = null;
		Patrol patrol = null;
		
		ArrayGrid<Character> grid = ArrayGrid.of(input);
		findGuard:
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				if (PATROLS.indexOf(grid.get(x, y)) != -1) {
					initialPatrol = new Patrol(x, y, Direction.of(grid.get(x, y)));
					patrol = initialPatrol;
					grid.set(x, y, patrol.dir.axisMarker());
					break findGuard;
				}
			}
		}
		
		if (initialPatrol == null) throw new IllegalStateException("There must be a Patrol marked on the map input");
		
		HashSet<Vec2i> possibleObstructions = new HashSet<Vec2i>();
		HashSet<Patrol> bonks = new HashSet<>();
		while (true) {
			if (patrol.simulatePartTwo(grid)) {
				// Log the bonk
				bonks.add(new Patrol(patrol.x, patrol.y, patrol.dir.counterClockwise()));
			}
			if (patrol.x < 0 || patrol.x >= grid.getWidth() || patrol.y < 0 || patrol.y >= grid.getHeight()) break;
			
			// This is the new part for part 2: Copy the map and bonks, and run a second sim from here with a new obstacle.
			Vec2i obstacle = new Vec2i(patrol.x+patrol.dir.dx(), patrol.y+patrol.dir.dy());
			boolean useObstacle = true;
			
			if (obstacle.x() == initialPatrol.x && obstacle.y() == initialPatrol.y) useObstacle = false;
			if (grid.get(obstacle).charValue() != '.') useObstacle = false;
			if (obstacle.x() < 0 || obstacle.y() < 0 || obstacle.x() >= grid.getWidth() || obstacle.y() >= grid.getHeight()) useObstacle = false;
			
			if (useObstacle) {
				ArrayGrid<Character> blocked = grid.copy();
				blocked.set(obstacle, 'O');
				HashSet<Patrol> bonksCopy = new HashSet<Patrol>(bonks);
				boolean halts = subSim(blocked, patrol.copy(), bonksCopy);
				if (!halts) {
					System.out.println("Valid obstruction:");
					blocked.set(initialPatrol.x, initialPatrol.y, initialPatrol.dir.marker());
					System.out.println(blocked);
					possibleObstructions.add(new Vec2i(patrol.x+patrol.dir.dx(), patrol.y+patrol.dir.dy()));
					System.out.println();
				}
			}
		}
		
		System.out.println("Obstruction Options: "+possibleObstructions.size());
	}

}
