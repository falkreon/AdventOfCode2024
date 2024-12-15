package blue.endless.advent.util;

import java.util.List;
import java.util.stream.Stream;

public record Triple<T>(T first, T second, T third) {
	public Stream<T> stream() { return Stream.of(first, second, third); }
	public List<T> toList() { return List.of(first, second, third); }
}
