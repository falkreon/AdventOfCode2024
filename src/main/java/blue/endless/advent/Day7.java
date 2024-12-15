package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongBinaryOperator;

import blue.endless.advent.util.Combinations.Cartesian;

public class Day7 implements Day {
	
	public static final String SAMPLE =
			"""
			190: 10 19
			3267: 81 40 27
			83: 17 5
			156: 15 6
			7290: 6 8 6 15
			161011: 16 10 13
			192: 17 8 14
			21037: 9 7 18 13
			292: 11 6 16 20
			""";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	static enum SimpleOperator implements LongBinaryOperator {
		PLUS( '+', (a,b)->a+b),
		TIMES('*', (a,b)->a*b);
		
		private final char symbol;
		private final LongBinaryOperator op;
		
		SimpleOperator(char symbol, LongBinaryOperator op) {
			this.symbol = symbol;
			this.op = op;
		}
		
		public char symbol() {
			return this.symbol;
		}

		@Override
		public long applyAsLong(long a, long b) {
			return op.applyAsLong(a, b);
		}
	}
	
	static enum ComplexOperator implements LongBinaryOperator {
		PLUS( "+", (a,b)->a+b),
		TIMES("*", (a,b)->a*b),
		CONCAT("||", (a,b)->Long.parseLong(""+a+b));
		
		private final String symbol;
		private final LongBinaryOperator op;
		
		ComplexOperator(String symbol, LongBinaryOperator op) {
			this.symbol = symbol;
			this.op = op;
		}
		
		public String symbol() {
			return this.symbol;
		}
		
		@Override
		public long applyAsLong(long left, long right) {
			return op.applyAsLong(left, right);
		}
	}
	
	public String display(List<Long> numbers, List<SimpleOperator> operators) {
		StringBuilder builder = new StringBuilder();
		
		for(int i=0; i<numbers.size(); i++) {
			builder.append(numbers.get(i));
			if (i < operators.size()) {
				builder.append(' ');
				builder.append(operators.get(i).symbol());
				builder.append(' ');
			}
		}
		
		return builder.toString();
	}
	
	public String displayComplex(List<Long> numbers, List<ComplexOperator> operators) {
		StringBuilder builder = new StringBuilder();
		
		for(int i=0; i<numbers.size(); i++) {
			builder.append(numbers.get(i));
			if (i < operators.size()) {
				builder.append(' ');
				builder.append(operators.get(i).symbol());
				builder.append(' ');
			}
		}
		
		return builder.toString();
	}
	
	public long evaluate(List<Long> numbers, List<SimpleOperator> operators) {
		long result = 0;
		
		for(int i=0; i<numbers.size(); i++) {
			long cur = numbers.get(i);
			if (i == 0) {
				result = cur;
				continue;
			}
			
			if (i <= operators.size()) {
				result = operators.get(i-1).applyAsLong(result, cur);
			}
		}
		
		return result;
	}
	
	public long evaluateComplex(List<Long> numbers, List<ComplexOperator> operators) {
		long result = 0;
		
		for(int i=0; i<numbers.size(); i++) {
			long cur = numbers.get(i);
			if (i == 0) {
				result = cur;
				continue;
			}
			
			if (i <= operators.size()) {
				result = operators.get(i-1).applyAsLong(result, cur);
			}
		}
		
		return result;
	}
	
	public Optional<String> getAnySolution(List<Long> numbers, long result) {
		int numOperators = numbers.size() - 1; // We'll need to test every combination of this many operators
		Set<List<SimpleOperator>> product = Cartesian.of(SimpleOperator.class, numOperators);
		for(List<SimpleOperator> list : product) {
			long operatorResult = evaluate(numbers, list);
			if (operatorResult == result) return Optional.of(display(numbers, list) + " = " + result);
		}
		return Optional.empty();
	}
	
	public Optional<String> getAnyComplexSolution(List<Long> numbers, long result) {
		int numOperators = numbers.size() - 1; // We'll need to test every combination of this many operators
		Set<List<ComplexOperator>> product = Cartesian.of(ComplexOperator.class, numOperators);
		for(List<ComplexOperator> list : product) {
			long operatorResult = evaluateComplex(numbers, list);
			if (operatorResult == result) return Optional.of(displayComplex(numbers, list) + " = " + result);
		}
		return Optional.empty();
	}
	
	@Override
	public void a(String input) {
		long answer = 0L;
		
		for(String line : input.trim().lines().toList()) {
			if (line.isBlank()) continue;
			// Parse input. We need `int result` (the part before the colon) and `List<Integer> numbers`.
			String[] parts = line.split(":");
			long result = Long.parseLong(parts[0]);
			String[] numberParts = parts[1].split(" ");
			List<Long> numbers = new ArrayList<>();
			for(String s : numberParts) {
				if (s.isBlank()) continue;
				numbers.add(Long.parseLong(s));
			}
			
			Optional<String> solution = getAnySolution(numbers, result);
			if (solution.isPresent()) {
				System.out.println(""+result+" : "+numbers+" can be solved as "+solution.get());
				answer += result;
			} else {
				System.out.println(""+result+" : "+numbers+" cannot possibly be true.");
			}
		}
		
		System.out.println("Answer: "+answer);
	}

	@Override
	public void b(String input) {
long answer = 0L;
		
		for(String line : input.trim().lines().toList()) {
			if (line.isBlank()) continue;
			// Parse input. We need `int result` (the part before the colon) and `List<Integer> numbers`.
			String[] parts = line.split(":");
			long result = Long.parseLong(parts[0]);
			String[] numberParts = parts[1].split(" ");
			List<Long> numbers = new ArrayList<>();
			for(String s : numberParts) {
				if (s.isBlank()) continue;
				numbers.add(Long.parseLong(s));
			}
			
			Optional<String> solution = getAnyComplexSolution(numbers, result);
			if (solution.isPresent()) {
				System.out.println(""+result+" : "+numbers+" can be solved as "+solution.get());
				answer += result;
			} else {
				System.out.println(""+result+" : "+numbers+" cannot possibly be true.");
			}
		}
		
		System.out.println("Answer: "+answer);
	}

}
