package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

public class Day11 implements Day {

	@Override
	public String getSampleA() {
		return "125 17";
	}
	
	public List<Long> simulate(List<Long> list) {
		List<Long> output = new ArrayList<>();
		for(Long value : list) {
			if (value.intValue() == 0) {
				output.add(Long.valueOf(1));
			} else if (value.toString().length() % 2 == 0) {
				// Even number of digits
				String s = value.toString();
				int digits = s.length() / 2;
				output.add(Long.valueOf(s.substring(0,digits)));
				output.add(Long.valueOf(s.substring(digits)));
			} else {
				output.add(value * 2024L);
			}
		}
		
		return output;
	}
	
	public long[] simulate(long value) {
		if (value == 0) {
			return new long[] { 1L };
		} else if (Long.toString(value).length() % 2 == 0) {
			// Even number of digits
			String s = Long.toString(value);
			int digits = s.length() / 2;
			return new long[] {
					Long.valueOf(s.substring(0,digits)),
					Long.valueOf(s.substring(digits))
				};
		} else {
			return new long[] { value * 2024L };
		}
	}
	
	public long recursiveExplosionCount(long seed, int remainingSteps) {
		List<Long> step = simulate(List.of(seed));
		if (remainingSteps > 1) {
			long result = 0L;
			for(long l : step) {
				result += recursiveExplosionCount(l, remainingSteps - 1);
			}
			return result;
		} else {
			return step.size();
		}
	}
	
	public long getExplosionCount(long seed, int steps) {
		List<Long> stones = List.of(seed);
		for(int i=0; i<steps; i++) {
			stones = simulate(stones);
		}
		return stones.size();
	}
	
	@Override
	public void a(String input) {
		List<Long> stones = new ArrayList<>();
		for(String s : input.trim().split(" ")) stones.add(Long.parseLong(s));
		
		System.out.println(stones);
		
		for(int i=0; i<25; i++) {
			stones = simulate(stones);
			
			StringBuilder display = new StringBuilder();
			for(int j=0; j<stones.size(); j++) {
				display.append(stones.get(j).toString());
				if (j<stones.size() - 1) display.append(' ');
				if (j > 100) break;
			}
			System.out.println(display);
		}
		
		System.out.println("Final Stone Count: "+stones.size());
		
	}
	
	/*
	 * This is a pre-computed table out to 40 steps, which lifts some of the work off of the recursive algorithm 
	 * 
	 * 
	 * The table is computed as the following:
	 * 
	 * long index;
	 * int[] counts = new int[40];
	 *   List<Long> stones = List.of(index);
	 *   for(int i=0; i<40; i++) {
	 *     stones = simulate(stones);
	 *     counts[i] = stones.size();
	 *   }
	 *   
	 * The "counts" array then goes at that index.
	 */
	int[][] table = {
		{ 1, 1, 2, 4, 4,  7, 14, 16, 20, 39, 62,  81, 110, 200, 328, 418,  667, 1059, 1546, 2377, 3572, 5602,  8268, 12343, 19778, 29165, 43726,  67724, 102131, 156451, 234511, 357632, 549949,  819967, 1258125, 1916299, 2886408, 4414216,  6669768, 10174278 },
		{ 1, 2, 4, 4, 7, 14, 16, 20, 39, 62, 81, 110, 200, 328, 418, 667, 1059, 1546, 2377, 3572, 5602, 8268, 12343, 19778, 29165, 43726, 67724, 102131, 156451, 234511, 357632, 549949, 819967, 1258125, 1916299, 2886408, 4414216, 6669768, 10174278, 15458147 },
		{ 1, 2, 4, 4, 6, 12, 16, 19, 30, 57, 92, 111, 181, 295, 414, 661,  977, 1501, 2270, 3381, 5463, 7921, 11819, 18712, 27842, 42646, 64275,  97328, 150678, 223730, 343711, 525238, 784952, 1208065, 1824910, 2774273, 4230422, 6365293,  9763578, 14777945 },
		{ 1, 2, 4, 4, 5, 10, 16, 26, 35, 52, 79, 114, 202, 294, 401, 642,  987, 1556, 2281, 3347, 5360, 7914, 12116, 18714, 27569, 42628, 64379,  98160, 150493, 223231, 344595, 524150, 788590, 1210782, 1821382, 2779243, 4230598, 6382031,  9778305, 14761601 },
		{ 1, 2, 4, 4, 4,  8, 16, 27, 30, 47, 82, 115, 195, 269, 390, 637,  951, 1541, 2182, 3204, 5280, 7721, 11820, 17957, 26669, 41994, 62235,  95252, 146462, 216056, 336192, 508191, 766555, 1178119, 1761823, 2709433, 4110895, 6188994,  9515384, 14316637 },
		{ 1, 1, 2, 4, 8,  8, 11, 22, 32, 45, 67, 109, 163, 223, 383, 597,  808, 1260, 1976, 3053, 4529, 6675, 10627, 15847, 23822, 37090, 55161,  84208, 128121, 194545, 298191, 444839, 681805, 1042629, 1565585, 2396146, 3626619, 5509999,  8396834, 12678459 },
		{ 1, 1, 2, 4, 8,  8, 11, 22, 32, 54, 68, 103, 183, 250, 401, 600,  871, 1431, 2033, 3193, 4917, 7052, 11371, 16815, 25469, 39648, 57976,  90871, 136703, 205157, 319620, 473117, 727905, 1110359, 1661899, 2567855, 3849988, 5866379,  8978479, 13464170 },
		{ 1, 1, 2, 4, 8,  8, 11, 22, 32, 52, 72, 106, 168, 242, 413, 602,  832, 1369, 2065, 3165, 4762, 6994, 11170, 16509, 25071, 39034, 57254,  88672, 134638, 203252, 312940, 465395, 716437, 1092207, 1637097, 2519878, 3794783, 5771904,  8814021, 13273744 },
		{ 1, 1, 2, 4, 7,  7, 11, 22, 31, 48, 69, 103, 161, 239, 393, 578,  812, 1322, 2011, 3034, 4580, 6798, 10738, 16018, 24212, 37525, 55534,  85483, 130183, 196389, 301170, 450896, 691214, 1054217, 1583522, 2428413, 3669747, 5573490,  8505207, 12835708 },
		{ 1, 1, 2, 4, 8,  8, 11, 22, 32, 54, 70, 103, 183, 262, 419, 586,  854, 1468, 2131, 3216, 4888, 7217, 11617, 17059, 25793, 40124, 58820,  92114, 139174, 208558, 322818, 480178, 740365, 1126352, 1685448, 2602817, 3910494, 5953715,  9102530, 13675794 },
		{ 2, 2, 3, 6, 8, 11, 21, 30, 36, 59,101, 143, 191, 310, 528, 746, 1085, 1726, 2605, 3923, 5949, 9174, 13870, 20611, 32121, 48943, 72891, 111450, 169855, 258582, 390962, 592143, 907581, 1369916, 2078092, 3174424, 4802707, 7300624, 11083984, 16844046 },
	};
	
	public long recursiveCountWithTables(long seed, int remainingSteps) {
		if (seed >=0 && seed <= 10L && remainingSteps <= 40) {
			int[] relevantTable = table[(int) seed];
			//System.out.println("  "+remainingSteps+" skipped using table lookup!");
			return relevantTable[remainingSteps-1];
		}
		
		//System.out.print('/');
		
		long[] step = simulate(seed);
		//System.out.print('-');
		//List<Long> step = simulate(List.of(seed));
		if (remainingSteps > 1) {
			long result = 0L;
			for(long l : step) {
				result += recursiveCountWithTables(l, remainingSteps - 1);
			}
			//System.out.print('\\');
			return result;
		} else {
			//System.out.print('\\');
			return step.length;
		}
	}
	
	@Override
	public void b(String input) {
		long startTime = System.nanoTime();
		
		List<Long> stones = new ArrayList<>();
		for(String s : input.trim().split(" ")) stones.add(Long.parseLong(s));
		
		
		long total = 0L;
		for(Long l : stones) {
			System.out.println("Simulating count for "+l);
			long cur = recursiveCountWithTables(l, 75);
			System.out.println("Stone count: " + cur);
			total += cur;
		}
		
		System.out.println("Final Stone Count: "+total);
		
		long elapsed = System.nanoTime() - startTime;
		elapsed /= 1_000_000; //nanos -> millis
		double elapsedSeconds = elapsed / 1000.0d;
		System.out.println("Elapsed time: "+elapsedSeconds+"s ("+elapsed+" msec)");
		/*
		int[] counts = new int[40];
		List<Long> stones = List.of(10L);
		for(int i=0; i<40; i++) {
			stones = simulate(stones);
			counts[i] = stones.size();
		}
		
		System.out.println("Table: "+Arrays.toString(counts));*/
	}

}
