package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Direction;
import blue.endless.advent.util.Vec2i;

public class Day15 implements Day {

	@Override
	public String getSampleA() {
		
		return
				"""
				##########
				#..O..O.O#
				#......O.#
				#.OO..O.O#
				#..O@..O.#
				#O#..O...#
				#O..O..O.#
				#.OO.O.OO#
				#....O...#
				##########
				
				<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
				vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
				><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
				<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
				^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
				^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
				>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
				<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
				^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
				v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
				""";
		
		/*
		return
				"""
				#######
				#...#.#
				#.....#
				#..OO@#
				#..O..#
				#.....#
				#######
				
				<vv<<^^<<^^
				""";*/
	}
	
	public static record InputData(ArrayGrid<Character> map, Vec2i robot, String directions) {
		
		public static InputData of(String rawInput) {
			ArrayGrid<Character> map = null;
			List<String> mapLines = new ArrayList<>();
			StringBuilder directions = new StringBuilder();
			Vec2i robot = null;
			
			for(String s : rawInput.trim().lines().toList()) {
				if (map == null) {
					//Grab all map lines till the first blank line
					if (s.isBlank()) {
						map = new ArrayGrid<>(mapLines.get(0).length(), mapLines.size(), '.');
						map.elementToString((it)->""+it, false);
						
						for(int y=0; y<map.getHeight(); y++) {
							String line = mapLines.get(y);
							for(int x=0; x<map.getWidth(); x++) {
								char ch = line.charAt(x);
								if (ch == '@') {
									ch = '.';
									robot = new Vec2i(x,y);
								}
								
								if (x < line.length()) map.set(x, y, ch);
							}
						}
					} else {
						mapLines.add(s);
					}
				} else {
					directions.append(s);
				}
			}
			
			return new InputData(map, robot, directions.toString());
		}
	}
	
	public static class Robot {
		public Vec2i position;
		public String instructions;
		public int pc = 0;
		
		public void step(Warehouse warehouse) {
			if (pc <= instructions.length()) {
				char instruction = instructions.charAt(pc);
				Direction dir = Direction.of(instruction);
				System.out.println("Attempting to move "+dir);
				char obstacle = warehouse.map.get(position.add(dir.dx(), dir.dy()));
				if (obstacle == '.') {
					System.out.println("  ok");
					position = position.add(dir.dx(), dir.dy());
				} else if (obstacle == 'O' || obstacle == '[' || obstacle == ']') {
					warehouse.tryPush(position, dir);
					obstacle = warehouse.map.get(position.add(dir.dx(), dir.dy()));
					if (obstacle == '.') {
						System.out.println("  pushed obstacles");
						position = position.add(dir.dx(), dir.dy());
					} else {
						System.out.println("  tried to push it but it won't move");
					}
				} else {
					System.out.println("  bonk!");
				}
			}
			pc++;
		}
	}
	
	public static class Warehouse {
		public ArrayGrid<Character> map;
		public Robot robot = new Robot();
		
		public String toString() {
			ArrayGrid<Character> display = map.copy();
			display.set(robot.position, '@');
			return display.toString();
		}
		
		public void step() {
			robot.step(this);
		}
		
		public void tryPush(Vec2i robotLocation, Direction direction) {
			int ix = robotLocation.x() + direction.dx();
			int iy = robotLocation.y() + direction.dy();
			char ch = map.get(ix, iy);
			if (ch != 'O') return; // We can't push if it ain't barrels
			
			while(ix>=0 && iy>=0 && ix<map.getWidth() && iy<map.getHeight()) {
				//scan until we find a free spot or a wall
				char cur = map.get(ix,iy).charValue();
				if (cur == '.') {
					map.set(ix, iy, 'O');
					map.set(robotLocation.add(direction.dx(), direction.dy()), '.');
					return;
				} else if (cur == 'O') {
					// Keep going
				} else {
					// We hit a wall
					return;
				}
				
				ix += direction.dx();
				iy += direction.dy();
			}
		}
		
		public boolean isComplete() {
			return robot.pc >= robot.instructions.length();
		}
		
		public long gps() {
			long result = 0L;
			
			for(int y=0; y<map.getHeight(); y++) {
				for(int x=0; x<map.getWidth(); x++) {
					char cur = map.get(x, y).charValue();
					if (cur == 'O') {
						result += (100L * y) + x;
					}
				}
			}
			
			return result;
		}
	}
	
	@Override
	public void a(String input) {
		InputData inputData = InputData.of(input);
		Warehouse warehouse = new Warehouse();
		warehouse.map = inputData.map();
		warehouse.robot.position = inputData.robot();
		warehouse.robot.instructions = inputData.directions();
		
		System.out.println("Initial state:");
		System.out.println(warehouse);
		
		while(!warehouse.isComplete()) {
			System.out.println();
			warehouse.step();
			System.out.println(warehouse);
		}
		
		System.out.println("Sum of GPS coords: "+warehouse.gps());
	}
	
	
	
	
	public static class WideWarehouse extends Warehouse {
		
		public String toString() {
			ArrayGrid<Character> display = map.copy();
			display.set(robot.position, '@');
			return display.toString();
		}
		
		public void step() {
			robot.step(this);
		}
		
		/**
		 * Returns true if we can push *something* into the indicated spot (robot, crate, etc). Checks recursive pushes.
		 * @param freeSpot The spot we're trying to push something into.
		 * @param direction The direction of motion, e.g. WEST means this spot's east face is being pushed.
		 * @return
		 */
		public boolean canPushInto(Vec2i freeSpot, Direction direction) {
			char contents = map.get(freeSpot);
			if (contents == '.') return true;
			if (contents == '#') return false;
			
			Vec2i crateLocation = null;
			if (contents == '[') {
				crateLocation = freeSpot;
			} else if (contents == ']') {
				crateLocation = freeSpot.add(-1, 0);
			}
			
			if (crateLocation == null) return false; // Unknown obstruction
			
			return switch(direction) {
				case WEST -> canPushInto(crateLocation.add(-1, 0), direction);
				
				case EAST -> canPushInto(crateLocation.add(2, 0), direction);
				
				case NORTH, SOUTH ->
					canPushInto(crateLocation.add(0, direction.dy()), direction) &&
					canPushInto(crateLocation.add(1, direction.dy()), direction);
			};
		}
		
		public void pushInto(Vec2i freeSpot, Direction direction) {
			char contents = map.get(freeSpot);
			switch(contents) {
				case '.' -> { return; }
				case '#' -> { return; }
				case '[', ']' -> {
					Vec2i crateLoc = (contents == '[') ? freeSpot : freeSpot.add(-1, 0);
					switch(direction) {
						case WEST -> {
							Vec2i destination = crateLoc.offset(direction);
							pushInto(destination, direction);
							if (map.get(destination) != '.') throw new IllegalStateException();
							map.set(destination, '[');
							map.set(crateLoc, ']');
							map.set(crateLoc.add(1, 0), '.');
						}
						case EAST -> {
							Vec2i destination = crateLoc.add(2, 0);
							pushInto(destination, direction);
							if (map.get(destination) != '.') throw new IllegalStateException();
							map.set(destination, ']');
							map.set(crateLoc.add(1, 0), '[');
							map.set(crateLoc, '.');
						}
						case NORTH, SOUTH -> {
							Vec2i leftPush = crateLoc.add(0, direction.dy());
							Vec2i rightPush = crateLoc.add(1, direction.dy());
							pushInto(leftPush, direction);
							pushInto(rightPush, direction);
							if (map.get(leftPush) != '.' || map.get(rightPush) != '.') throw new IllegalStateException();
							map.set(leftPush, '[');
							map.set(rightPush, ']');
							map.set(crateLoc, '.');
							map.set(crateLoc.add(1, 0), '.');
						}
					}
				}
				default -> { return; }
			}
		}
		
		public void tryPush(Vec2i robotLocation, Direction direction) {
			
			Vec2i dest = robotLocation.offset(direction);
			char ch = map.get(dest);
			if (ch != '[' && ch != ']') return; // We can't push if it ain't barrels
			
			if (canPushInto(dest, direction)) {
				pushInto(dest, direction);
			}
		}
		
		public boolean isComplete() {
			return robot.pc >= robot.instructions.length();
		}
		
		public long gps() {
			long result = 0L;
			
			for(int y=0; y<map.getHeight(); y++) {
				for(int x=0; x<map.getWidth(); x++) {
					char cur = map.get(x, y).charValue();
					if (cur == '[') {
						result += (100L * y) + x;
					}
				}
			}
			
			return result;
		}
	}
	
	public static ArrayGrid<Character> widen(ArrayGrid<Character> map) {
		ArrayGrid<Character> wideMap = new ArrayGrid<>(map.getWidth()*2, map.getHeight());
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				char ch = map.get(x, y);
				switch(ch) {
					case '.' -> {
						wideMap.set(x * 2, y, '.');
						wideMap.set(x * 2 + 1, y, '.');
					}
					case '#' -> {
						wideMap.set(x * 2, y, '#');
						wideMap.set(x * 2 + 1, y, '#');
					}
					case 'O' -> {
						wideMap.set(x * 2, y, '[');
						wideMap.set(x * 2 + 1, y, ']');
					}
					default -> {
						//Clear any weird symbols
						wideMap.set(x * 2, y, '.');
						wideMap.set(x * 2 + 1, y, '.');
					}
				}
			}
		}
		
		wideMap.elementToString((it)->""+it, false);
		return wideMap;
	}

	@Override
	public void b(String input) {
		InputData inputData = InputData.of(input);
		
		WideWarehouse warehouse = new WideWarehouse();
		warehouse.map = widen(inputData.map());
		warehouse.robot.position = new Vec2i(inputData.robot().x() * 2, inputData.robot().y());
		warehouse.robot.instructions = inputData.directions();
		
		System.out.println("Initial state:");
		System.out.println(warehouse);
		
		while(!warehouse.isComplete()) {
			System.out.println();
			warehouse.step();
			System.out.println(warehouse);
		}
		
		System.out.println("Sum of GPS coords: "+warehouse.gps());
	}
	
}
