package blue.endless.advent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class Day5 implements Day {
	
	public static final String EXAMPLE =
			"""
			47|53
			97|13
			97|61
			97|47
			75|29
			61|13
			75|53
			29|13
			97|29
			53|29
			61|53
			97|53
			61|29
			47|13
			75|47
			97|75
			47|61
			75|61
			47|29
			75|13
			53|13
			
			75,47,61,53,29
			97,61,53,29,13
			75,29,13
			75,97,47,61,53
			61,13,29
			97,13,75,29,47
			""";
	
	@Override
	public String getSampleA() {
		return EXAMPLE;
	}

	record Rule(int a, int b) implements Comparator<Integer> {
		@Override
		public int compare(Integer first, Integer second) {
			if (first==null || second==null) throw new IllegalArgumentException();
			if (first.intValue()==a && second.intValue()==b) return -1;
			if (first.intValue()==b && second.intValue()==a) return 1;
			return 0;
		}
		
		public static Rule of(String s) {
			String[] parts = s.split(Pattern.quote("|"));
			int a = Integer.parseInt(parts[0]);
			int b = Integer.parseInt(parts[1]);
			return new Rule(a, b);
		}
		
		public boolean isInOrder(int earlierItem, int laterItem) {
			if (earlierItem == a && laterItem == b) return true;
			if (earlierItem == b && laterItem == a) return false;
			return true; //It doesn't (yet) matter whether we affirmatively or passively declare the order is correct.
		}
	}
	
	public boolean isInOrder(List<Rule> rules, int[] pages) {
		for (int i=0; i<pages.length-1; i++) {
			for (int j=i+1; j<pages.length; j++) {
				for(Rule rule : rules) {
					if (!rule.isInOrder(pages[i], pages[j])) {
						System.out.println("  Rule Violation: "+rule.a()+"|"+rule.b());
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void a(String input) {
		List<String> lines = input.trim().lines().toList();
		boolean scrapingRules = true;
		List<Rule> rules = new ArrayList<>();
		
		long total = 0L;
		for(String s : lines) {
			if (scrapingRules) {
				if (s.isBlank()) {
					scrapingRules = false;
					continue;
				} else {
					rules.add(Rule.of(s));
				}
			} else {
				//Split on commas and then test if the list is sorted
				if (s.isBlank()) continue;
				String[] pagesParts = s.split(",");
				int[] pages = new int[pagesParts.length];
				for(int i=0; i<pagesParts.length; i++) {
					pages[i] = Integer.parseInt(pagesParts[i]);
				}
				
				System.out.println("Testing " + Arrays.toString(pages));
				boolean inOrder = isInOrder(rules, pages);
				System.out.println("  isInOrder?: "+inOrder);
				
				if (inOrder) {
					int middle = pages[pages.length/2];
					System.out.println("  Middle value: "+middle);
					total += middle;
				}
			}
		}
		
		System.out.println("Total: "+total);
	}
	
	public int[] fix(List<Rule> rules, int[] pages) {
		int[] result = Arrays.copyOf(pages, pages.length);
		
		for (int i=0; i<result.length-1; i++) {
			for (int j=i+1; j<result.length; j++) {
				for (Rule rule : rules) {
					if (!rule.isInOrder(result[i], result[j])) {
						int tmp = result[i];
						result[i] = result[j];
						result[j] = tmp;
					}
				}
			}
		}
		
		return result;
	}
	
	public int[] fixFully(List<Rule> rules, int[] pages) {
		for(int i=0; i<pages.length; i++) {
			pages = fix(rules, pages);
			if (isInOrder(rules, pages)) return pages;
		}
		
		return pages;
	}

	@Override
	public void b(String input) {
		List<String> lines = input.trim().lines().toList();
		boolean scrapingRules = true;
		List<Rule> rules = new ArrayList<>();
		
		long total = 0L;
		for(String s : lines) {
			if (scrapingRules) {
				if (s.isBlank()) {
					scrapingRules = false;
					continue;
				} else {
					rules.add(Rule.of(s));
				}
			} else {
				//Split on commas and then test if the list is sorted
				if (s.isBlank()) continue;
				String[] pagesParts = s.split(",");
				int[] pages = new int[pagesParts.length];
				for(int i=0; i<pagesParts.length; i++) {
					pages[i] = Integer.parseInt(pagesParts[i]);
				}
				
				System.out.println("Testing " + Arrays.toString(pages));
				boolean inOrder = isInOrder(rules, pages);
				System.out.println("  isInOrder?: "+inOrder);
				
				if (!inOrder) {
					int[] newPages = fixFully(rules, pages);
					System.out.println("  Reordered "+Arrays.toString(pages)+" into "+Arrays.toString(newPages));
					int middle = newPages[newPages.length/2];
					System.out.println("  Middle value: "+middle);
					total += middle;
				}
			}
		}
		
		System.out.println("Total: "+total);
	}

}
