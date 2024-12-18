package blue.endless.advent.util;

public record Vec2l(long x, long y) {
	public Vec2l add(long x, long y) {
		return new Vec2l(this.x + x, this.y + y);
	}
	
	public Vec2l add(Vec2l vec) {
		return new Vec2l(this.x + vec.x, this.y + vec.y);
	}
	
	public Vec2l subtract(long x, int y) {
		return new Vec2l(this.x - x, this.y - y);
	}
	
	public Vec2l subtract(Vec2l vec) {
		return new Vec2l(this.x - vec.x, this.y - vec.y);
	}
}
