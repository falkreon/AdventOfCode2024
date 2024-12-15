package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

public class Day9 implements Day {
	
	public static final String SAMPLE = "2333133121414131402";
	
	@Override
	public String getSampleA() {
		return SAMPLE;
	}
	
	public int[] createDisk(String input) {
		List<Integer> disk = new ArrayList<>();
		
		boolean used = true; //First number is a "used" amount
		int fileIndex = 0;
		for(char ch : input.toCharArray()) {
			int writeValue = -1;
			
			if (used) {
				writeValue = fileIndex;
				fileIndex++;
			}
			
			int cellsToWrite = Integer.parseInt(""+ch);
			
			for(int i=0; i<cellsToWrite; i++) {
				disk.add(writeValue);
			}
			
			used = !used;
		}
		
		//Copy it over into the fixed-size array
		int[] result = new int[disk.size()];
		for(int i=0; i<disk.size(); i++) result[i] = disk.get(i);
		return result;
	}
	
	public String examineDisk(int[] disk) {
		StringBuilder result = new StringBuilder();
		
		int max = disk.length;
		if (max > 100) max = 100;
		
		for(int i=0; i<max; i++) {
			int cur = disk[i];
			if (cur == -1) {
				result.append('.');
			} else if (cur>0xF) {
				result.append(' ');
			} else {
				char c = Integer.toHexString(disk[i]).charAt(0);
				result.append(c);
			}
		}
		
		return result.toString();
	}
	
	public boolean defragStep(int[] disk) {
		/*
		 * - find the last filled cell in the disk
		 * - find the first free cell in the disk
		 * - copy the value of the last filled cell into the first free cell
		 * - "erase" the last filled cell
		 * 
		 * We are done if any of the conditions are true:
		 * - The first free spot was never found (disk is full)
		 * - The last full spot was never found (disk is empty)
		 * - The first free spot is greater than the last filled spot (disk is fully defragmented)
		 */
		
		int firstFree = Integer.MAX_VALUE;
		int lastFilled = -1;
		for(int i=0; i<disk.length; i++) {
			int curCell = disk[i];
			if (curCell == -1 && i < firstFree) {
				firstFree = i;
			}
			if (curCell != -1 && i > lastFilled) lastFilled = i;
		}
		
		if (firstFree < Integer.MAX_VALUE && lastFilled >= 0 && firstFree < lastFilled) {
			disk[firstFree] = disk[lastFilled];
			disk[lastFilled] = -1;
			
			return false;
		} else {
			return true;
		}
	}
	
	public long checksum(int[] disk) {
		long result = 0L;
		
		for(int i=0; i<disk.length; i++) {
			int fileId = disk[i];
			
			// If the disk is defragmented, there's nothing after the first free cell
			if (fileId == -1) return result;
			
			result += fileId * i;
		}
		
		return result;
	}
	
	@Override
	public void a(String input) {
		int[] disk = createDisk(input.trim());
		System.out.println(examineDisk(disk));
		
		while(!defragStep(disk)) {
			System.out.println(examineDisk(disk));
		}
		
		System.out.println();
		System.out.println("Checksum: "+checksum(disk));
	}
	
	
	public static class VolumeData {
		
		
	}
	
	
	@Override
	public void b(String input) {
		// TODO Auto-generated method stub
		
	}

}
