package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import blue.endless.advent.util.Range;

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
		if (max > 200) max = 200;
		
		for(int i=0; i<max; i++) {
			int cur = disk[i];
			if (cur == -1) {
				result.append('.');
			} else if (cur>0xF) {
				result.append('0');
			} else {
				char c = Integer.toHexString(disk[i]).charAt(0);
				result.append(c);
			}
		}
		
		result.append('\n');
		for(int i=0; i<max; i++) {
			int cur = disk[i];
			if (cur == -1) {
				result.append('.');
			} else if (cur>0xF) {
				char c = Integer.toHexString(disk[i]).charAt(0);
				result.append(c);
			} else {
				result.append('0');
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
	
	public Range findFile(int[] disk, int fileId) {
		Range result = null;
		
		for(int i=disk.length-1; i>=0; i--) {
			if (result == null) {
				if (disk[i] == fileId) {
					result = new Range(i, 1);
				}
			} else {
				if (disk[i] == fileId) {
					result = new Range(result.start() - 1, result.length() + 1);
				} else {
					if (result != null) return result;
				}
			}
		}
		
		//if (result == null) throw new IllegalStateException("File "+fileId+" couldn't be found!");
		
		return result;
	}
	
	public Optional<Range> findFreeSpot(int[] disk, int size) {
		int start = -1;
		int len = -1;
		
		for(int i=0; i<disk.length; i++) {
			if (start == -1) {
				if (disk[i] == -1) {
					//System.out.println("  examining run starting at "+i);
					start = i ;
					len = 1;
				}
			} else {
				if (disk[i] == -1) {
					len++;
				} else {
					if (len < size) {
						//System.out.println("  run "+new Range(start, len)+" is insufficient");
						start = -1;
						len = -1;
					} else {
						//System.out.println("  run "+new Range(start, len)+" works!");
						return Optional.of(new Range(start, len));
					}
				}
			}
		}
		return Optional.empty();
	}
	
	public void moveFile(int[] disk, Range file, Range freeSpot) {
		if (freeSpot.length() < file.length()) throw new IllegalArgumentException();
		
		int fileId = disk[file.start()];
		
		for(int i=0; i<file.length(); i++) {
			disk[file.start() + i] = -1;
			disk[freeSpot.start() + i] = fileId;
		}
	}
	
	public long fullChecksum(int[] disk) {
		long result = 0L;
		
		for(int i=0; i<disk.length; i++) {
			int fileId = disk[i];
			
			// If the disk is defragmented, there's nothing after the first free cell
			if (fileId == -1) continue;
			
			result += fileId * i;
		}
		
		return result;
	}
	
	@Override
	public void b(String input) {
		int[] disk = createDisk(input.trim());
		System.out.println(examineDisk(disk));
		System.out.println();
		
		int lastFileId = input.length() / 2;
		System.out.println("Last File Id: "+lastFileId);
		
		while(findFile(disk, lastFileId) == null) lastFileId--;
		System.out.println("Oops, last File Id: "+lastFileId);
		
		int filesMoved = 0;
		
		for (int i=lastFileId; i>=0; i--) {
			//System.out.println("Compacting File: "+i);
			Range lastFile = findFile(disk, i);
			Optional<Range> freeSpot = findFreeSpot(disk, lastFile.length());
			if (freeSpot.isEmpty() || freeSpot.get().start() > lastFile.start()) {
				//System.out.println("Cannot compact file "+i);
				//System.out.println();
			} else {
				System.out.println("Move file "+i+" from "+lastFile+" to "+freeSpot);
				moveFile(disk, lastFile, freeSpot.get());
				filesMoved++;
				System.out.println(examineDisk(disk));
				System.out.println();
			}
		}
		
		System.out.println("Checksum: "+fullChecksum(disk));
		System.out.println("Files Successfully Compacted: " + filesMoved);
	}

}
