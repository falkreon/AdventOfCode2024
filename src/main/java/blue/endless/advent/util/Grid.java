package blue.endless.advent.util;

public interface Grid<T> {
	int getWidth();
	int getHeight();
	T get(int x, int y);
	default T get(Vec2i vec) {
		return get(vec.x(), vec.y());
	}
	void set(int x, int y, T value);
	default void set(Vec2i vec, T value) {
		set(vec.x(), vec.y(), value);
	}
	void clear(int x, int y);
	default void clear(Vec2i vec) {
		clear(vec.x(), vec.y());
	}
	void clear();
	default void forEach(GridConsumer<T> consumer) {
		for(int y=0; y<getHeight(); y++) {
			for(int x=0; x<getWidth(); x++) {
				consumer.accept(x, y, get(x, y));
			}
		}
	}
	
	@FunctionalInterface
	public static interface GridConsumer<T> {
		public void accept(int x, int y, T value);
	}
	
	public default boolean contains(Vec2i pos) {
		return pos.x() >= 0 && pos.y() >= 0 &&
				pos.x() < getWidth() &&
				pos.y() < getHeight();
	}
}
