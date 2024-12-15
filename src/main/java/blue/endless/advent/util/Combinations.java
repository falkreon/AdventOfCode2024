package blue.endless.advent.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Combinations {
	
	public static class Cartesian {

		public static <E extends Enum<E>> Set<List<E>> ofTwo(Class<E> enumClass) {
			HashSet<List<E>> result = new HashSet<>();
			
			for(E a : enumClass.getEnumConstants()) {
				for(E b : enumClass.getEnumConstants()) {
					result.add(List.of(a, b));
				}
			}
			
			return result;
		}

		public static <T> Set<List<T>> ofTwo(Collection<T> availableOptions, boolean allowDuplicates) {
			HashSet<List<T>> result = new HashSet<>();
			
			for(T a : availableOptions) {
				for (T b : availableOptions) {
					if (a == b && !allowDuplicates) continue;
					result.add(List.of(a, b));
				}
			}
			
			return result;
		}

		public static <E extends Enum<E>> Set<List<E>> ofThree(Class<E> enumClass) {
			HashSet<List<E>> result = new HashSet<>();
			
			for(E a : enumClass.getEnumConstants()) {
				for(E b : enumClass.getEnumConstants()) {
					for (E c : enumClass.getEnumConstants()) {
						result.add(List.of(a, b, c));
					}
				}
			}
			
			return result;
		}

		public static <E extends Enum<E>> Set<List<E>> cartesianProductOfFour(Class<E> enumClass) {
			HashSet<List<E>> result = new HashSet<>();
			
			for(E a : enumClass.getEnumConstants()) {
				for(E b : enumClass.getEnumConstants()) {
					for (E c : enumClass.getEnumConstants()) {
						for (E d : enumClass.getEnumConstants()) {
							result.add(List.of(a, b, c, d));
						}
					}
				}
			}
			
			return result;
		}

		/**
		 * We've taken the cartesian product and split it into identical factors. So in the 4-product E * E * E * E,
		 * if E has two entries, then we can start with two lists of one entry each.
		 * @param <E>
		 * @param enumClass
		 * @param subset
		 * @param cur
		 * @param limit
		 * @return
		 */
		private static <E extends Enum<E>> Set<List<E>> stepOf(Class<E> enumClass, Set<List<E>> subset) {
			HashSet<List<E>> result = new HashSet<>();
			
			if (subset.isEmpty()) {
				// 1 * E
				for(E e : enumClass.getEnumConstants()) {
					result.add(List.of(e));
				}
				return result;
			}
			
			for(List<E> sublist : subset) {
				for(E e : enumClass.getEnumConstants()) {
					List<E> list = new ArrayList<>();
					list.addAll(sublist);
					list.add(e);
					result.add(List.copyOf(list));
				}
			}
			return result;
		}

		public static <E extends Enum<E>> Set<List<E>> of(Class<E> enumClass, int symbolCount) {
			Set<List<E>> result = Set.of();
			
			for(int i=0; i<symbolCount; i++) {
				result = Combinations.Cartesian.stepOf(enumClass, result);
			}
			
			return result;
		}
		
	}
	
	/**
	 * Gets a "simple combination" of two.
	 * 
	 * <p>For example, if you give it [A, B] this should yield [[A, B]] as there is only one combination of A and B with no
	 * duplicates or reordering. Generally you should get `factorial(availableOptions.size()) - 1` elements
	 * @param <T> The type of element being selected
	 * @param availableOptions The set of elements to select from - can be any kind of collection, will be evaluated in iteration order.
	 * @return A Set-of-lists, each list containing one complete selection of all elements. The returned Lists are immutable.
	 */
	public static <T> Set<Pair<T>> ofTwo(Collection<T> availableOptions) {
		// Cast or copy availableOptions into a List because while a Set makes more sense for unique options, we need indices.
		List<T> indices = (availableOptions instanceof List<T> l) ? l : List.copyOf(availableOptions);
		
		HashSet<Pair<T>> result = new HashSet<>();
		
		// We will only be using n-1 of the selectable elements in the "first position". This avoids duplicate selections
		
		/*
		 * Imagine a binary number, with size(availableOptions) bits, which is a bitmask of included items in availableOptions.
		 * (as in, a Gray Code) If we just "count up", we will find lots of duplicates and reordered items. But if we select
		 * bits in an organized fashion, such as:
		 * 
		 * 123..
		 * 12.3.
		 * 12..3
		 * 1.23.
		 * 1.2.3
		 * 1..23
		 * .123.
		 * etc.
		 * 
		 * we will arrive at every unique, non-repeating element. And as you can see, "2" in this case will always be greater
		 * than "1", and "1" will never hit the end of the list. That's why the loop starts and counts look weird below.
		 * 
		 * You'll note that we could also just *make* gray codes and use bit shifts to compose the list, I'll do this in a
		 * Stream form later.
		 */
		for(int i = 0; i < indices.size() - 1; i++) {
			for(int j = i + 1; j < indices.size(); j++) {
				result.add(new Pair<>(
						indices.get(i),
						indices.get(j)
						));
			}
		}
		
		return result;
	}
	
	public static <T> Iterator<Pair<T>> iteratorOfTwo(Collection<T> availableOptions) {
		@SuppressWarnings("unchecked")
		final T[] t = (T[]) availableOptions.toArray();
		
		Iterator<Pair<T>> iterator = new Iterator<>() {
			private final T[] indices = t;
			private int i = 0;
			private int j = 1;
			
			@Override
			public boolean hasNext() {
				if (indices.length < 2) return false;
				return i < indices.length - 1;
			}

			@Override
			public Pair<T> next() {
				Pair<T> result = new Pair<>(indices[i], indices[j]);
				j++;
				if (j >= indices.length) {
					i++;
					j = i + 1;
				}
				
				return result;
			}
			
		};
		
		return iterator;
	}
	
	public static <T> Stream<Pair<T>> streamTwo(Collection<T> availableOptions) {
		Iterable<Pair<T>> iterable = () -> iteratorOfTwo(availableOptions);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	
	//Untested
	public static <T> Iterator<Triple<T>> iteratorOfThree(Collection<T> availableOptions) {
		@SuppressWarnings("unchecked")
		final T[] t = (T[]) availableOptions.toArray();
		
		Iterator<Triple<T>> iterator = new Iterator<>() {
			private final T[] indices = t;
			private int i = 0;
			private int j = 1;
			private int k = 2;
			
			@Override
			public boolean hasNext() {
				if (indices.length < 3) return false;
				return i < indices.length - 2;
			}

			@Override
			public Triple<T> next() {
				Triple<T> result = new Triple<>(indices[i], indices[j], indices[k]);
				k++;
				if (k >= indices.length) {
					j++;
					if (j >= indices.length - 1) {
						i++;
						j = i + 1;
					}
					k = j + 1;
				}
				
				return result;
			}
			
		};
		
		return iterator;
	}
	
	public static <T> Stream<Triple<T>> streamThree(Collection<T> availableOptions) {
		Iterable<Triple<T>> iterable = () -> iteratorOfThree(availableOptions);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
