package blue.endless.advent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Display;
import blue.endless.advent.util.Vec2i;

public class Day14 implements Day {

	@Override
	public String getSampleA() {
		return
				"""
				p=0,4 v=3,-3
				p=6,3 v=-1,-3
				p=10,3 v=-1,2
				p=2,0 v=2,-1
				p=0,0 v=1,3
				p=3,0 v=-2,-2
				p=7,6 v=-1,-3
				p=3,0 v=-1,-2
				p=9,3 v=2,3
				p=7,3 v=-1,2
				p=2,4 v=2,-3
				p=9,5 v=-3,-3
				""";
	}
	
	public static Vec2i parse(String s) {
		String[] parts = s.split(",");
		return new Vec2i(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}
	
	public static long wrap(long value, long extent) {
		long result = value % extent;
		
		if (value < 0L) {
			result = (extent + result) % extent;
		}
		
		return result;
	}
	
	public static class Robot {
		public long x;
		public long y;
		public long vx;
		public long vy;
		
		public Robot(Vec2i position, Vec2i velocity) {
			x = position.x();
			y = position.y();
			vx = velocity.x();
			vy = velocity.y();
		}
		
		public void advance(int numSteps, long width, long height) {
			x = wrap(x + vx * numSteps, width);
			y = wrap(y + vy * numSteps, height);
		}
	}
	
	public void run(List<Robot> robots, int width, int height) {
		//Display
		ArrayGrid<Integer> display = new ArrayGrid<>(width, height);
		display.elementToString((it)->""+it, false);
		display.setDefaultValue(0);
		display.clear();
		
		for(Robot r : robots) {
			r.advance(100, width, height);
			int cur = display.get((int) r.x, (int) r.y);
			display.set((int) r.x, (int) r.y, cur + 1);
			//System.out.println("Robot at "+r.x+", "+r.y);
		}
		
		System.out.println(display);
		
		int halfWidth = display.getWidth() / 2;
		int halfXOfs = (display.getWidth() % 2 == 0) ? halfWidth : halfWidth + 1;
		int halfHeight = display.getHeight() / 2;
		int halfYOfs = (display.getHeight() % 2 == 0) ? halfHeight : halfHeight + 1;
		
		long nw = 0L;
		long ne = 0L;
		long sw = 0L;
		long se = 0L;
		long uncounted = 0L;
		
		for(Robot r : robots) {
			if (r.x < halfWidth) {
				if (r.y < halfHeight) {
					nw++;
				} else if (r.y >= halfYOfs) {
					sw++;
				} else {
					uncounted++;
				}
			} else if (r.x >= halfXOfs) {
				if (r.y < halfHeight) {
					ne++;
				} else if (r.y >= halfYOfs) {
					se++;
				} else {
					uncounted++;
				}
			} else {
				uncounted++;
			}
		}
		
		System.out.println("nw: "+nw+", ne: "+ne+", sw: "+sw+", se: "+se+" (uncounted: "+uncounted+")");
		System.out.println("Product: "+(nw*ne*sw*se));
	}
	
	@Override
	public void a(String input) {
		List<Robot> robots = new ArrayList<>();
		
		for(String s : input.trim().lines().toList()) {
			String[] parts = s.split(" ");
			String p = parts[0].substring(2);
			String v = parts[1].substring(2);
			robots.add(new Robot(parse(p), parse(v)));
		}
		
		run(robots, 101, 103);
	}

	@Override
	public void b(String input) {
		List<Robot> robots = new ArrayList<>();
		
		for(String s : input.trim().lines().toList()) {
			String[] parts = s.split(" ");
			String p = parts[0].substring(2);
			String v = parts[1].substring(2);
			robots.add(new Robot(parse(p), parse(v)));
		}
		
		Display d = new Display();
		d.setGrid(new ArrayGrid<Integer>(103, 101));
		d.mapColor(1, Color.cyan);
		d.mapColor(2, Color.yellow);
		d.mapColor(3, Color.orange);
		d.mapColor(4, Color.red);
		d.mapColor(5, Color.pink);
		
		System.out.println(int[].class.getCanonicalName());
		//Integer.TYPE;
	}
	

}
