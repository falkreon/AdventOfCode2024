package blue.endless.advent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Day1 implements Day {
	public String getSampleA() {
		return
		"""
		3   4
		4   3
		2   5
		1   3
		3   9
		3   3
		""";
	}
	
	public void a(String input) {
		//Populate lists
		List<Integer> listA = new ArrayList<>();
		List<Integer> listB = new ArrayList<>();
		
		Iterator<String> iter = input.lines().iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.isBlank()) continue;
			String[] parts = s.split(Pattern.quote("   "));
			if (parts.length != 2) throw new IllegalArgumentException("Error in input: '"+s+"'.");
			listA.add(Integer.parseInt(parts[0]));
			listB.add(Integer.parseInt(parts[1]));
		}
		
		listA.sort(Integer::compare);
		listB.sort(Integer::compare);
		
		System.out.println("List A: "+listA.toString());
		System.out.println("List B: "+listB.toString());
		
		int totalDistance = 0;
		for(int i=0; i<listA.size(); i++) {
			int distance = Math.abs(listA.get(i) - listB.get(i));
			totalDistance += distance;
			
			System.out.println("  distance: "+distance);
		}
		
		System.out.println("TotalDistance: "+totalDistance);
	}

	@Override
	public String getSampleB() {
		return getSampleA();
	}

	@Override
	public void b(String input) {
		//Populate lists
		List<Integer> listA = new ArrayList<>();
		List<Integer> listB = new ArrayList<>();
		
		Iterator<String> iter = input.lines().iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.isBlank()) continue;
			String[] parts = s.split(Pattern.quote("   "));
			if (parts.length != 2) throw new IllegalArgumentException("Error in input: '"+s+"'.");
			listA.add(Integer.parseInt(parts[0]));
			listB.add(Integer.parseInt(parts[1]));
		}
		
		//We no longer need to sort them
		
		long totalSimilarity = 0L;
		for(int a : listA) {
			int histogramCount = 0;
			for(int b : listB) {
				if (a==b) histogramCount++;
			}
			
			long similarityScore = a * histogramCount;
			totalSimilarity += similarityScore;
			
			System.out.println("  value: "+a+", similarity: "+similarityScore);
		}
		
		System.out.println("Total Similarity: "+totalSimilarity);
	}

	@Override
	public boolean sameDataForB() {
		return true;
	}
}
