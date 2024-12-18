package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {
	
	public static final List<Day> DAYS = List.of(
			new Day1(), new Day2(), new Day3(), new Day4(), new Day5(), new Day6(), new Day7(),
			new Day8(), new Day9(), new Day10(), new Day11(), new Day12()
			);
	
	public static String sneakyData(String name) {
		try {
			return Files.readString(Path.of("src/data/"+name+".txt"));
		} catch (IOException ex) {
			System.out.println("Couldn't get data file for problem "+name);
			System.exit(-1);
		}
		
		return null;
	}
	
	public static void main(String... args) {
		boolean useTestData = false;
		if (args.length == 2 && args[1].equals("--testdata")) {
			useTestData = true;
		} else if (args.length != 1) {
			exitWithUsage();
		}
		
		if (args[0].endsWith("a")) {
			//Part 1
			int day = Integer.valueOf(args[0].substring(0,args[0].length()-1));
			if (day < 1 || day > DAYS.size()) {
				System.out.println("Invalid day: "+day);
				System.exit(-1);
				return;
			}
			
			System.out.println("Day " + day + " Part 1:");
			Day dayObject = DAYS.get(day - 1);
			
			String data = (useTestData) ? dayObject.getSampleA() : sneakyData(args[0]);
			
			dayObject.a(data);
		} else if (args[0].endsWith("b")) {
			//Part 2
			int day = Integer.valueOf(args[0].substring(0,args[0].length()-1));
			if (day < 1 || day > DAYS.size()) {
				System.out.println("Invalid day: "+day);
				System.exit(-1);
				return;
			}
			
			System.out.println("Day " + day + " Part 2:");
			Day dayObject = DAYS.get(day-1);
			
			String data = (useTestData) ? dayObject.getSampleB() : (dayObject.sameDataForB() ? sneakyData(day+"a") : sneakyData(args[0])) ;
			
			dayObject.b(data);
		} else {
			//Part 1
			int day = 0;
			try {
				day = Integer.valueOf(args[0]);
			} catch (NumberFormatException ex) {
				exitWithUsage();
			}
			
			if (day < 1 || day > DAYS.size()) {
				System.out.println("Invalid day: "+day);
				System.exit(-1);
				return;
			}
			
			System.out.println("Day " + day + " Part 1:");
			Day dayObject = DAYS.get(day - 1);
			
			String data = (useTestData) ? dayObject.getSampleA() : sneakyData(args[0]+"a");
			
			dayObject.a(data);
		}
	}
	
	public static void exitWithUsage() {
		System.out.println("usage: AdventOfCode2024 <day><a|b> [--testdata]");
		System.out.println("example: AdventOfCode2024 3a");
		System.exit(-1);
	}
}
