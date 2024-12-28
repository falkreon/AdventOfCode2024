package blue.endless.advent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day19 implements Day {

	@Override
	public String getSampleA() {
		return
				"""
				r, wr, b, g, bwu, rb, gb, br
				
				brwrr
				bggr
				gbbr
				rrbgbr
				ubwu
				bwurrg
				brgr
				bbrgwb
				""";
	}
	
	public List<List<String>> complete(String word, List<String> prefix, int startingPos, List<String> symbols, boolean shortCircuit) {
		String base = (startingPos == 0) ? "" : word.substring(0, startingPos);
		List<List<String>> result = new ArrayList<>();
		
		for(String symbol : symbols) {
			String partial = base + symbol;
			if (!word.startsWith(partial)) continue;
			
			int subStart = startingPos + symbol.length();
			ArrayList<String> solution = new ArrayList<>();
			solution.addAll(prefix);
			solution.add(symbol);
			
			if (word.length() <= subStart) {
				result.add(solution);
				if (shortCircuit) return result;
			} else {
				// Recurse
				List<List<String>> solutions = complete(word, solution, subStart, symbols, shortCircuit);
				result.addAll(solutions);
				if (shortCircuit && solutions.size() > 0) return result;
			}
		}
		
		return result;
	}

	@Override
	public void a(String input) {
		List<String> lines = new ArrayList<>(input.trim().lines().toList());
		String symbolListString = lines.removeFirst();
		lines.removeFirst(); //Remove blank line
		
		String[] symbolParts = symbolListString.split(",");
		List<String> symbols = new ArrayList<>();
		for(String s : symbolParts) {
			if (s.isBlank()) continue;
			symbols.add(s.trim());
		}
		
		int successes = 0;
		for(String word : lines) {
			// We need to create an ordered list of symbols that creates the desired word.
			// There may be multiple combinations of symbols that creates the word; we will recover ALL OF THEM,
			// because we do not know what part 2 may hold, and we have the technology.
			
			System.out.println("Word: "+word);
			List<List<String>> solutions = complete(word, List.of(), 0, symbols, true);
			if (solutions.size() > 0) {
				for(List<String> solution : solutions) {
					System.out.println("  "+solution);
				}
				
				successes++;
			} else {
				System.out.println("  Cannot make this word.");
			}
		}
		
		System.out.println("We are able to make "+successes+" of these patterns.");
	}
	
	public long completions(String word, int startingPos, List<String> symbols, boolean shortCircuit) {
		String base = (startingPos == 0) ? "" : word.substring(0, startingPos);
		long results = 0;
		
		for(String symbol : symbols) {
			String partial = base + symbol;
			if (!word.startsWith(partial)) continue;
			
			int subStart = startingPos + symbol.length();
			
			if (word.length() <= subStart) {
				results++;
				if (startingPos < 20) System.out.println("    Found "+partial+" ("+results+" so far)");
				if (shortCircuit) return results;
			} else {
				// Recurse
				if (startingPos < 20) System.out.println("    Recursing from "+partial+" ("+results+" so far)");
				long subresults = completions(word, subStart, symbols, shortCircuit);
				results += subresults;
				if (shortCircuit && subresults > 0) return results;
			}
		}
		
		return results;
	}
	
	public long tableSuffix(String word, int startingPos, List<String> symbols, long[] table) {
		// This shouldn't exist since we're traversing backwards, but just in case
		long existing = table[startingPos];
		if (existing != -1) return existing;
		
		/*
		 * As long as we traverse backwards, every possible prefix we generate can only be
		 * one of three possibilities:
		 * 
		 * - It is longer than the target word, making it unviable
		 * - It does not 
		 */
		
		long hits = 0L;
		
		for(String symbol : symbols) {
			String prefix = word.substring(0, startingPos) + symbol;
			
			// Unviable
			if (prefix.length() > word.length()) continue;
			
			// Exact match - no other possibilities exist for this symbol
			if (word.equals(prefix)) {
				hits++;
				continue;
			}
			
			// Partial match - this is a really straightforward table lookup
			if (word.startsWith(prefix)) {
				int newStartingPos = prefix.length();
				hits += table[newStartingPos];
			}
		}
		
		table[startingPos] = hits;
		
		return hits;
	}
	
	public long tableSuffix(String word, List<String> symbols) {
		long[] table = new long[word.length()];
		Arrays.fill(table, -1L);
		System.out.print("  ");
		for (int i=word.length()-1; i>=0; i--) {
			System.out.print(".");
			tableSuffix(word, i, symbols, table);
		}
		System.out.println(" : "+table[0]);
		return table[0];
	}

	@Override
	public void b(String input) {
		List<String> lines = new ArrayList<>(input.trim().lines().toList());
		String symbolListString = lines.removeFirst();
		lines.removeFirst(); //Remove blank line
		
		String[] symbolParts = symbolListString.split(",");
		List<String> symbols = new ArrayList<>();
		for(String s : symbolParts) {
			if (s.isBlank()) continue;
			symbols.add(s.trim());
		}
		
		long successes = 0;
		for(String word : lines) {
			// We need to create an ordered list of symbols that creates the desired word.
			// There may be multiple combinations of symbols that creates the word; we will recover ALL OF THEM,
			// because we do not know what part 2 may hold, and we have the technology.
			
			System.out.println("Word: "+word);
			long solutions = tableSuffix(word, symbols);
			if (solutions > 0) {
				System.out.println("  "+solutions+" ways");
				successes+= solutions;
			} else {
				System.out.println("  Cannot make this word.");
			}
		}
		
		System.out.println("We are able to make "+successes+" total combinations of these patterns.");
	}
	
}
