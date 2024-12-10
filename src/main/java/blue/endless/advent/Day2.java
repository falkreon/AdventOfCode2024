package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Day2 implements Day {
	public static final String SAMPLE =
			"""
			7 6 4 2 1
			1 2 7 8 9
			9 7 6 2 1
			1 3 2 4 5
			8 6 4 4 1
			1 3 6 7 9
			""";

	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	public int[] getReadings(String readingsString) {
		String[] readings = readingsString.split(Pattern.quote(" "));
		int[] result = new int[readings.length];
		
		for(int i = 0; i < readings.length; i++) {
			result[i] = Integer.parseInt(readings[i]);
		}
		
		return result;
	}
	
	public boolean isSafe(String report) {
		int[] readings = getReadings(report);
		
		int prevReading = -1;
		Monotony direction = Monotony.UNKNOWN;
		
		for(int curReading : readings) {
			
			//If we have a previous reading, we have a pair and can test its monotony
			if (prevReading != -1) {
				
				//First things first, is the pair close enough together and far enough apart?
				if (curReading == prevReading) return false;
				if (Math.abs(curReading - prevReading) > 3) {
					
					return false;
				}
				
				if (direction == Monotony.UNKNOWN) {
					direction = Monotony.of(prevReading, curReading);
					if (direction == Monotony.UNKNOWN) return false; // Must be RISING or FALLING
				} else {
					if (Monotony.of(prevReading, curReading) != direction) return false;
					
				}
			}
			
			prevReading = curReading;
		}
		
		return true;
	}
	
	public boolean isSafe(List<Integer> report) {
		int prevReading = -1;
		Monotony direction = Monotony.UNKNOWN;
		
		for(int curReading : report) {
			
			//If we have a previous reading, we have a pair and can test its monotony
			if (prevReading != -1) {
				
				//First things first, is the pair close enough together and far enough apart?
				if (curReading == prevReading) return false;
				if (Math.abs(curReading - prevReading) > 3) {
					return false;
				}
				
				if (direction == Monotony.UNKNOWN) {
					direction = Monotony.of(prevReading, curReading);
					if (direction == Monotony.UNKNOWN) return false; // Must be RISING or FALLING
				} else {
					if (!direction.canTransitionTo(Monotony.of(prevReading, curReading))) return false;
					//if (Monotony.of(prevReading, curReading) != direction) return false;
				}
			}
			
			prevReading = curReading;
		}
		
		return true;
	}
	
	public boolean canDampenError(List<Integer> report) {
		//System.out.println("Dampening "+report.toString());
		for(int i=0; i<report.size(); i++) {
			List<Integer> subreport = new ArrayList<>();
			for(int j=0; j<report.size(); j++) if (j != i ) subreport.add(report.get(j));
			boolean safe = isSafe(subreport);
			if (safe) {
				StringBuilder dampString = new StringBuilder("[");
				for(int k = 0; k < report.size(); k++) {
					if (k != 0) dampString.append(", ");
					if (k == i) dampString.append('*');
					dampString.append(report.get(k));
					if (k == i) dampString.append('*');
				}
				dampString.append(']');
				
				System.out.println("Dampened report "+dampString);
				//System.out.println("Dampened index " + i + " of " + report + " to "+subreport.toString());
				return true;
			}
			if (safe) return true;
		}
		//System.out.println("Could not dampen "+report.toString());
		return false;
	}
	
	@Override
	public void a(String input) {
		int safeReports = 0;
		for(String s : input.lines().toList()) {
			if (s.isBlank()) continue;
			boolean isSafe = isSafe(s);
			System.out.println("["+s+"] isSafe: "+isSafe);
			if (isSafe) safeReports++;
		}
		System.out.println("Safe: "+safeReports);
	}

	@Override
	public void b(String input) {
		int safeReports = 0;
		
		for(String s : input.lines().toList()) {
			if (s.isBlank()) continue;
			
			List<Integer> readings = new ArrayList<>();
			for(int i : getReadings(s)) readings.add(i);
			
			if (isSafe(readings)) {
				safeReports++;
				continue;
			}
			
			if (canDampenError(readings)) {
				safeReports++;
				continue;
			}
		}
		System.out.println("Safe: "+safeReports);
	}
	
	public static enum Monotony {
		UNKNOWN,
		RISING,
		FALLING;
		
		public static Monotony of(int prev, int cur) {
			if (cur > prev) return RISING;
			if (cur < prev) return FALLING;
			return UNKNOWN;
		}
		
		public boolean canTransitionTo(Monotony other) {
			if (this == other) return true;
			if (this == UNKNOWN) return true;
			return false; // RISING -> FALLING or FALLING -> RISING
		}
	}
}
