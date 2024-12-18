package blue.endless.advent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Vec2i;

public class Day10 implements Day {
	
	public static final String SAMPLE =
			"""
			89010123
			78121874
			87430965
			96549874
			45678903
			32019012
			01329801
			10456732
			""";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	public Set<Vec2i> traversableNeighbors(ArrayGrid<Integer> map, Vec2i pos) {
		HashSet<Vec2i> result = new HashSet<>();
		int cur = map.get(pos);
		if (map.get(pos.add(1,0)) == cur+1) result.add(pos.add(1,0));
		if (map.get(pos.add(-1,0)) == cur+1) result.add(pos.add(-1,0));
		if (map.get(pos.add(0,1)) == cur+1) result.add(pos.add(0,1));
		if (map.get(pos.add(0,-1)) == cur+1) result.add(pos.add(0,-1));
		return result;
	}
	
	public int getTrailheadScore(ArrayGrid<Integer> map, int x, int y) {
		Set<Vec2i> reachableNines = new HashSet<>();
		ArrayDeque<Vec2i> queue = new ArrayDeque<>();
		queue.add(new Vec2i(x, y));
		
		while(!queue.isEmpty()) {
			Vec2i cur = queue.removeLast();
			if (map.get(cur).intValue() == 9) {
				reachableNines.add(cur);
			} else {
				queue.addAll(traversableNeighbors(map, cur));
			}
		}
		
		return reachableNines.size();
	}
	
	public int recursiveTrailheadScore(ArrayGrid<Integer> map, Vec2i pos) {
		Set<Vec2i> neighbors = traversableNeighbors(map, pos);
		int localScore = 0;
		for(Vec2i v: neighbors) {
			int cur = map.get(v);
			if (cur == 9) {
				localScore++;
			} else {
				localScore += recursiveTrailheadScore(map, v);
			}
		}
		
		return localScore;
	}
	
	@Override
	public void a(String input) {
		ArrayGrid<Integer> map = ArrayGrid.ofNumeric(input);
		map.setDefaultValue(-1);
		
		long scoreSum = 0L;
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				if (map.get(x, y).intValue() == 0) {
					int score = getTrailheadScore(map, x, y);
					scoreSum += score;
					System.out.println("Score for trailhead at "+x+", "+y+": "+score);
				}
			}
		}
		
		System.out.println("Score Sum: "+scoreSum);
	}

	@Override
	public void b(String input) {
		ArrayGrid<Integer> map = ArrayGrid.ofNumeric(input);
		map.setDefaultValue(-1);
		
		long scoreSum = 0L;
		
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				if (map.get(x, y).intValue() == 0) {
					int score = recursiveTrailheadScore(map, new Vec2i(x, y));
					scoreSum += score;
					System.out.println("Score for trailhead at "+x+", "+y+": "+score);
				}
			}
		}
		
		System.out.println("Score Sum: "+scoreSum);
	}
	
}
