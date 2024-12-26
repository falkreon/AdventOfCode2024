package blue.endless.advent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import blue.endless.advent.util.Strings;

public class Day17 implements Day {

	@Override
	public String getSampleA() {
		/*
		return
				"""
				Register A: 729
				Register B: 0
				Register C: 0
				
				Program: 0,1,5,4,3,0
				""";
		*/
		return
				"""
				Register A: 0
				Register B: 2024
				Register C: 43690
				
				Program: 4,0
				""";
	}
	
	public static interface Opcode {
		public void apply(Cpu cpu, long operand);
	}
	
	public static interface OperandType {
		public long get(Cpu cpu, byte literal);
	}
	
	public static String getComboOperandName(byte literal) {
		return switch(literal) {
			case 0, 1, 2, 3 -> "" + (long) literal;
			case 4 -> "A";
			case 5 -> "B";
			case 6 -> "C";
			default -> "!!INVALID!!";
		};
	}
	
	public static final OperandType LITERAL = (cpu, literal) -> (long) literal;
	public static final OperandType COMBO = (cpu, literal) -> {
		return switch(literal) {
			case 0, 1, 2, 3 -> (long) literal;
			case 4 -> cpu.a;
			case 5 -> cpu.b;
			case 6 -> cpu.c;
			default -> {
				cpu.ip = Integer.MAX_VALUE; //Halt the CPU
				yield 0L;
			}
		};
	};
	
	public class Cpu {
		public long a = 0L;
		public long b = 0L;
		public long c = 0L;
		public int ip = 0;
		public byte[] program;
		List<Byte> stdout = new ArrayList<>();
		
		static String[] opcodeNames =       { "adv",   "bxl", "bst",   "jnz",   "bxc", "out", "bdv", "cdv" };
		static OperandType[] operandTypes = { COMBO, LITERAL, COMBO, LITERAL, LITERAL, COMBO, COMBO, COMBO };
		static Opcode[] opcodes = {
			// adv
			(cpu, op) -> {
				long numerator = cpu.a;
				long denominator = (long) Math.pow(2, op);
				cpu.a = numerator / denominator;
				cpu.ip += 2;
			},
			
			// bxl:  b = b xor literal-operand
			(cpu, op) -> {
				cpu.b = cpu.b ^ op;
				cpu.ip += 2;
			},
			
			// bst:  b = combo-operand modulo 8
			(cpu, op) -> {
				cpu.b = op % 8;
				cpu.ip += 2;
			},
			
			// jnz:  jump if A is nonzero
			(cpu, op) -> {
				if (cpu.a != 0) {
					cpu.ip = (int) op;
				} else {
					cpu.ip += 2;
				}
			},
			
			// bxc
			(cpu, op) -> {
				cpu.b = cpu.b ^ cpu.c; //Ignores op
				cpu.ip += 2;
			},
			
			// out
			(cpu, op) -> {
				cpu.stdout.add((byte) (op % 8));
				cpu.ip += 2;
			},
			
			// bdv
			(cpu, op) -> {
				long numerator = cpu.a;
				long denominator = (long) Math.pow(2, op);
				cpu.b = numerator / denominator;
				cpu.ip += 2;
			},
			
			// cdv
			(cpu, op) -> {
				//long numerator = cpu.a;
				//long denominator = (long) Math.pow(2, op);
				//cpu.c = numerator / denominator;
				cpu.c = cpu.a >> op;
				cpu.ip += 2;
			},
		};
		
		public Cpu clone() {
			Cpu result = new Cpu();
			result.a = a;
			result.b = b;
			result.c = c;
			result.ip = ip;
			result.program = program; //Program memory is effectively immutable
			// Note: Clone does not copy over stdout
			
			return result;
		}
		
		public String state() {
			return "A: "+a+", B: "+b+", C: "+c+", IP: "+ip;
		}
		
		public String decompileAt(int ofs) {
			if (ofs+1 >= program.length) return "EOF";
			
			int opcodeNum = program[ofs] & 0x7;
			byte literal = program[ofs+1];
			String operandName = (operandTypes[opcodeNum] == LITERAL) ? "" + (long) literal : getComboOperandName(literal);
			
			return opcodeNames[opcodeNum] + " " + operandName;
		}
		
		public static String decompile(byte[] program) {
			StringBuilder result = new StringBuilder();
			
			for(int i=0; i<program.length; i+=2) {
				int opcodeNum = program[i] & 0x7;
				//Opcode opcode = opcodes[opcodeNum];
				String opcodeName = opcodeNames[opcodeNum];
				String operandName = (operandTypes[opcodeNum] == LITERAL) ? "" + (long) program[i+1] : getComboOperandName(program[i+1]);
				result.append(opcodeName);
				result.append(' ');
				result.append(operandName);
				result.append('\n');
			}
			
			return result.toString();
		}
		
		public boolean run() {
			while(ip < program.length) {
				int inst = program[ip] & 0x7;
				//System.out.println("Evaluating: "+decompileAt(ip));
				if (inst >= opcodes.length) {
					System.out.println("Program halted abnormally: Invalid opcode '"+opcodeNames[inst]+"' ("+inst+")");
					return false;
				}
				
				long operand = operandTypes[inst].get(this, program[ip + 1]);
				opcodes[inst].apply(this, operand);
			}
			
			return true;
		}
	}
	
	public Cpu compile(String input) {
		Cpu cpu = new Cpu();
		for(String line : input.lines().toList()) {
			if (line.startsWith("Register A: ")) {
				line = Strings.removePrefix(line, "Register A: ");
				cpu.a = Long.parseLong(line);
			} else if (line.startsWith("Register B: ")) {
				line = Strings.removePrefix(line, "Register B: ");
				cpu.b = Long.parseLong(line);
			} else if (line.startsWith("Register C: ")) {
				line = Strings.removePrefix(line, "Register C: ");
				cpu.c = Long.parseLong(line);
			} else if (line.startsWith("Program: ")) {
				line = Strings.removePrefix(line, "Program: ");
				String[] bits = line.trim().split(",");
				cpu.program = new byte[bits.length];
				for(int i=0; i<bits.length; i++) {
					cpu.program[i] = Byte.parseByte(bits[i], 8);
				}
			}
		}
		return cpu;
	}

	@Override
	public void a(String input) {
		Cpu cpu = compile(input);
		
		System.out.println("Decompilation:");
		System.out.println(Cpu.decompile(cpu.program));
		
		cpu.run();
		System.out.println("Output: "+cpu.stdout);
		System.out.println("Final State: "+cpu.state());
	}

	@Override
	public void b(String input) {
		Cpu template = compile(input);
		
		byte[] result = new byte[0];
		
		long lastTick = System.nanoTime();
		System.out.println(Cpu.decompile(template.program));
		
		long lockPick = 0L;
		for(int i=0; i<template.program.length; i++) {
			long keyPosition = i * 3;
			pickLock:
			for(int j = 0; j < 8; j++) {
				// Cur is our guess for this position
				long cur = lockPick | (long) (j << keyPosition);
				
				Cpu branch = template.clone();
				branch.a = cur;
				branch.run();
				if (branch.stdout.size() <= i) continue;
				if (branch.stdout.get(i).byteValue() == (byte) template.program[i]) {
					System.out.println("Found digit "+i+" (stdout "+branch.stdout+")");
					lockPick = cur;
					break pickLock;
				}
				
			}
			
			
		}
		
		
		Cpu branch = template.clone();
		branch.a = lockPick;
		branch.run();
		System.out.println("Stdout: "+branch.stdout);
		
		/*
		while(Arrays.compare(result, template.program) != 0) {
			aValue++;
			
			Cpu branch = template.clone();
			branch.a = aValue;
			branch.run();
			result = new byte[branch.stdout.size()];
			for(int i=0; i<branch.stdout.size(); i++) result[i] = branch.stdout.get(i).byteValue();
			
			long currentTick = System.nanoTime();
			long elapsed = currentTick - lastTick;
			if (elapsed > 1_000_000_000L) {
				lastTick = currentTick;
				System.out.println("  "+aValue);
			}
		}
		*/
		System.out.println("Substitute A Value: "+lockPick);
	}
	
}
