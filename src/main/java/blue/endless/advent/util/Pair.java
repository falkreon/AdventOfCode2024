package blue.endless.advent.util;

import java.util.List;
import java.util.stream.Stream;

public record Pair<T>(T left, T right) {
	public Stream<T> stream() { return Stream.of(left, right); }
	public List<T> toList() { return List.of(left, right); }
}
