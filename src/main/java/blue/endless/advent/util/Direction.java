package blue.endless.advent.util;

public enum Direction {
	NORTH( 0, -1, '^', '|'),
	EAST ( 1,  0, '>', '-'),
	SOUTH( 0,  1, 'v', '|'),
	WEST (-1,  0, '<', '-');
	
	private final char marker;
	private final char axisMarker;
	final int dx;
	final int dy;
	
	Direction(int dx, int dy, char marker, char axisMarker) {
		this.marker = marker;
		this.axisMarker = axisMarker;
		this.dx = dx;
		this.dy = dy;
	}
	
	public Direction clockwise() {
		return switch(this) {
			case NORTH -> EAST;
			case EAST  -> SOUTH;
			case SOUTH -> WEST;
			case WEST  -> NORTH;
		};
	}
	
	public Direction counterClockwise() {
		return switch(this) {
			case NORTH -> WEST;
			case EAST  -> NORTH;
			case SOUTH -> EAST;
			case WEST  -> SOUTH;
		};
	}
	
	public Direction opposite() {
		return switch(this) {
			case NORTH -> SOUTH;
			case EAST -> WEST;
			case SOUTH -> NORTH;
			case WEST -> EAST;
		};
	}
	
	public int dx() { return this.dx; }
	public int dy() { return this.dy; }
	public char marker() { return this.marker; }
	public char axisMarker() { return this.axisMarker; }
	
	public static Direction of(char c) {
		for(Direction d : values()) {
			if (d.marker == c) return d;
		}
		
		return Direction.NORTH;
	}
}