package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import blue.endless.advent.util.Strings;
import blue.endless.advent.util.Vec2i;
import blue.endless.advent.util.Vec2l;

public class Day13 implements Day {
	
	@Override
	public String getSampleA() {
		return
				"""
				Button A: X+94, Y+34
				Button B: X+22, Y+67
				Prize: X=8400, Y=5400
				
				Button A: X+26, Y+66
				Button B: X+67, Y+21
				Prize: X=12748, Y=12176
				
				Button A: X+17, Y+86
				Button B: X+84, Y+37
				Prize: X=7870, Y=6450
				
				Button A: X+69, Y+23
				Button B: X+27, Y+71
				Prize: X=18641, Y=10279
				""";
	}
	
	public record ClawMachine(Vec2l aPress, Vec2l bPress, Vec2l prizeLocation) {}
	
	public Vec2l parseVec(String input) {
		String[] parts = input.split(Pattern.quote(", "));
		if (parts.length != 2) throw new IllegalArgumentException();
		
		int x = Integer.parseInt(parts[0].substring(2));
		int y = Integer.parseInt(parts[1].substring(2));
		
		return new Vec2l(x, y);
	}
	
	public List<ClawMachine> parseInput(String input) {
		Vec2l buttonA = null;
		Vec2l buttonB = null;
		Vec2l prize = null;
		List<ClawMachine> result = new ArrayList<>();
		
		for(String s : input.trim().lines().toList()) {
			if (s.isBlank()) {
				if (buttonA != null && buttonB != null && prize != null) {
					result.add(new ClawMachine(buttonA, buttonB, prize));
				} else if (buttonA != null || buttonB != null || buttonB != null) {
					System.out.println("Partial claw machine in input discarded.");
				}
				
				buttonA = null;
				buttonB = null;
				prize = null;
			} else {
				if (s.startsWith("Button A: ")) {
					buttonA = parseVec(Strings.removePrefix(s, "Button A: "));
				} else if (s.startsWith("Button B: ")) {
					buttonB = parseVec(Strings.removePrefix(s, "Button B: "));
				} else if (s.startsWith("Prize: ")) {
					prize = parseVec(Strings.removePrefix(s, "Prize: "));
				} else {
					System.out.println("Discarding unreadable line in input: \""+s+"\"");
				}
			}
		}
		
		if (buttonA != null && buttonB != null && prize != null) {
			result.add(new ClawMachine(buttonA, buttonB, prize));
		}
		
		return result;
	}
	
	public long solve(List<ClawMachine> machines) {
		long total = 0L;
		
		for(ClawMachine machine : machines) {
			System.out.println(machine);
			
			/*
			 * Today is a system of linear equations problem. We want to find a value such that
			 * prizeX = aCount * aPressX + bCount * bPressX
			 * prizeY = aCount * aPressY + bCount * bPressY
			 * 
			 * 
			 * Let's solve for bCount in the second equation
			 * 
			 *                    prizeY  =  aCount * aPressY + bCount * bPressY
			 *        - aCount * aPressY     - aCount * aPressY
			 * prizeY - aCount * aPressY  = bCount * bPressY
			 * -------------------------    ----------------
			 *          bPressY                   bPressY
			 * 
			 * therefore:
			 * 
			 * bCount = (prizeY - aCount * aPressY) / bPressY
			 * 
			 * Substituting in:
			 * 
			 * prizeX = aCount * aPressX + ((prizeY - aCount * aPressY) / bPressY) * bPressX
			 * 
			 * Solving for aCount:
			 * 
			 * aCount = (prizeX * bPressY - prizeY * bPressX) / (aPressX * bpressY - aPressY * bPressX)
			 * 
			 * 
			 */
			
			double aCount =
					(machine.prizeLocation().x() * machine.bPress.y() - machine.prizeLocation.y() * machine.bPress.x()) /
					(double)((machine.aPress.x() * machine.bPress.y() - machine.aPress().y() * machine.bPress.x()));
			
			/*
			 * Now we reverse the ordeal, solve for bCount and substitute our "known" aCount in:
			 * 
			 *   aCount * aPressX + bCount * bPressX  =  prizeX
			 * - aCount * aPressX                        - aCount * aPressX
			 *                      bCount * bPressX  =  prizeX - aCount * aPressX
			 *                      ----------------     -------------------------
			 *                            bPressX               bPressX
			 * 
			 * Therefore:
			 * bCount = (prizeX - aCount * aPressX) / bPressX
			 */
			
			double bCount = machine.prizeLocation.x() - ((aCount) * machine.aPress().x());
			bCount /= (float) machine.bPress().x();
			
			if (aCount == Math.floor(aCount) && bCount == Math.floor(bCount)) {
				long aPresses = (long) aCount;
				long bPresses = (long) bCount;
				long cost = (aPresses * 3) + bPresses;
				total += cost;
				System.out.println("  Solved with "+aPresses+" A-Presses and "+bPresses+" B-Presses for "+cost+" tokens.");
				System.out.println();
			} else {
				System.out.println("  This prize is unreachable.");
				System.out.println();
			}
		}
		
		return total;
	}
	
	@Override
	public void a(String input) {
		List<ClawMachine> machines = parseInput(input);
		
		long total = solve(machines);
		
		System.out.println();
		System.out.println("Total tokens used for all obtainable prizes: "+total);
	}
	
	public static final long TRANSLATION = 10000000000000L;
	
	@Override
	public void b(String input) {
		List<ClawMachine> machines = parseInput(input);
		List<ClawMachine> fixed = new ArrayList<>();
		for(ClawMachine machine : machines) {
			ClawMachine fixedMachine = new ClawMachine(
					machine.aPress(),
					machine.bPress(),
					new Vec2l(machine.prizeLocation().x() + TRANSLATION, machine.prizeLocation().y() + TRANSLATION)
					);
			fixed.add(fixedMachine);
		}
		
		long total = solve(fixed);
		
		System.out.println();
		System.out.println("Total tokens used for all obtainable prizes: "+total);
	}

}
