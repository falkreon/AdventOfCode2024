package blue.endless.advent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 implements Day {

	public static final String SAMPLE =
			"""
			xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
			""";
	
	public static final String SAMPLE_B =
			"""
			xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
			""";
	
	// mul\((?<a>\d+),(?<b>\d+)\) captures any "mul(#,#)" with named capturing groups
	public static final Pattern MUL_PATTERN = Pattern.compile("mul\\((?<a>\\d+),(?<b>\\d+)\\)");
	public static final Pattern DO_PATTERN = Pattern.compile("do\\(\\)");
	public static final Pattern DONT_PATTERN = Pattern.compile("don't\\(\\)"); // no escape needed for ' even though it looks like it should need it
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	@Override
	public String getSampleB() {
		return SAMPLE_B;
	}

	@Override
	public void a(String input) {
		Matcher matcher = MUL_PATTERN.matcher(input);
		
		long total = 0L;
		
		// Just recursively find and capture, we don't care where the matches are
		matcher.find();
		while (matcher.hasMatch()) {
			int a = Integer.parseInt(matcher.group("a"));
			int b = Integer.parseInt(matcher.group("b"));
			System.out.println("mul("+a+","+b+") = "+ (a*b));
			
			total += (a*b);
			
			matcher.find();
		}
		
		System.out.println("Total: "+total);
	}

	@Override
	public void b(String input) {
		Matcher mulMatcher = MUL_PATTERN.matcher(input);
		Matcher doMatcher = DO_PATTERN.matcher(input);
		Matcher dontMatcher = DONT_PATTERN.matcher(input);
		
		int offset = 0;
		long total = 0L;
		boolean doMuls = true;
		boolean keepWorking = true;
		
		while(keepWorking && offset < input.length()) {
			mulMatcher.find(offset);
			doMatcher.find(offset);
			dontMatcher.find(offset);
			
			if (!mulMatcher.hasMatch() && !doMatcher.hasMatch() && !dontMatcher.hasMatch()) {
				keepWorking = false;
				break;
			}
			
			int mulOffset = mulMatcher.hasMatch() ? mulMatcher.start() : Integer.MAX_VALUE;
			int doOffset = doMatcher.hasMatch() ? doMatcher.start() : Integer.MAX_VALUE;
			int dontOffset = dontMatcher.hasMatch() ? dontMatcher.start() : Integer.MAX_VALUE;
			
			if (mulOffset < doOffset && mulOffset < dontOffset) {
				// It's a mul operation next
				
				int a = Integer.parseInt(mulMatcher.group("a"));
				int b = Integer.parseInt(mulMatcher.group("b"));
				int product = a * b;
				
				if (doMuls) {
					System.out.println("mul("+a+","+b+") = " + product);
					
					total += product;
				} else {
					System.out.println("Skipping mul("+a+","+b+")");
					
				}
				
				offset = mulMatcher.end();
			} else if (doOffset < mulOffset && doOffset < dontOffset) {
				// It's a do operation next
				doMuls = true;
				offset = doMatcher.end();
			} else {
				// Well, looks like we're doing don'ts now.
				doMuls = false;
				offset = dontMatcher.end();
			}
		}
		
		System.out.println("Total: "+total);
	}

}
