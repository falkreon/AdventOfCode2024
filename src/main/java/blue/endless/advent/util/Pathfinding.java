package blue.endless.advent.util;

import java.util.ArrayList;
import java.util.List;

public class Pathfinding {
	
	private static final String GRADIENT = "\u2591\u2592\u2593\u2588";
	
	/**
	 * Returns a quick and dirty list of in-bounds neighbors in cardinal directions.
	 * @param map A Grid of any kind, used to detemrine what is in-bounds.
	 * @param pos The position we want to find in-bounds neighbors for.
	 * @return A list of in-bounds neighbors. In a 1x1 map, with pos=0,0, this will return an empty list.
	 */
	private static List<Vec2i> neighbors(Grid<?> map, Vec2i pos) {
		List<Vec2i> result = new ArrayList<>();
		if (pos.x() > 0) result.add(pos.add(-1, 0));
		if (pos.x() < map.getWidth()-1) result.add(pos.add(1, 0));
		if (pos.y() > 0) result.add(pos.add(0, -1));
		if (pos.y() < map.getHeight()-1) result.add(pos.add(0, 1));
		
		return result;
	}
	
	public static ArrayGrid<Integer> createGradientMap(Grid<Character> map, Vec2i start, Vec2i end, boolean shortCircuit) {
		ArrayList<Vec2i> queue = new ArrayList<>();
		queue.addLast(end);
		ArrayGrid<Integer> gradient = new ArrayGrid<>(map.getWidth(), map.getHeight());
		gradient.setDefaultValue(Integer.MAX_VALUE);
		gradient.clear();
		gradient.set(end, 0);
		
		long gradientMax = 0L;
		
		while(!queue.isEmpty()) {
			Vec2i v = queue.removeFirst();
			int ourCost = gradient.get(v);
			
			List<Vec2i> neighbors = neighbors(map, v);
			/*
			if (v.x() > 0) neighbors.add(v.add(-1, 0));
			if (v.x() < map.getWidth()-1) neighbors.add(v.add(1, 0));
			if (v.y() > 0) neighbors.add(v.add(0, -1));
			if (v.y() < map.getHeight()-1) neighbors.add(v.add(0, 1));*/
			
			for(Vec2i neighbor : neighbors) {
				char mapData = map.get(neighbor).charValue();
				if (mapData == '#') continue;
				int curCost = gradient.get(neighbor);
				if (curCost > ourCost + 1) {
					gradient.set(neighbor, ourCost + 1);
					if (gradientMax < ourCost + 1) gradientMax = ourCost + 1;
					if (neighbor.equals(start) && shortCircuit) return gradient;
					queue.addLast(neighbor);
				}
			}
		}
		final double divisor = gradientMax;
		
		gradient.elementToString((it) -> {
			int i = it.intValue();
			if (i == Integer.MAX_VALUE) return " ";
			double percent = i / divisor;
			int index = (int) (percent * GRADIENT.length());
			if (index >= GRADIENT.length()) index = GRADIENT.length()-1;
			return ""+GRADIENT.charAt(index);
		}, false);
		
		return gradient;
	}
	
	public static List<Vec2i> pathFromGradient(Grid<Integer> gradient, Vec2i start, Vec2i end) {
		ArrayList<Vec2i> result = new ArrayList<>();
		result.add(start);
		Vec2i cur = start;
		while(!cur.equals(end)) {
			Vec2i bestNeighbor = null;
			int bestCost = Integer.MAX_VALUE;
			for(Vec2i neighbor : neighbors(gradient, cur)) {
				int neighborValue = gradient.get(neighbor);
				if (neighborValue < bestCost) {
					bestCost = neighborValue;
					bestNeighbor = neighbor;
				}
			}
			if (bestNeighbor == null) {
				return List.of(); //The destination is unreachable.
			}
			
			result.add(bestNeighbor);
			cur = bestNeighbor;
		}
		
		return result;
	}
}
