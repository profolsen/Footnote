package assembler;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by po917265 on 6/3/17.
 */
public class Assembler {

    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(new File("program.txt"));
        PrintStream out = new PrintStream("output.txt");
        HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
        ArrayList<String> program = new ArrayList<String>();
        ArrayList<Integer> pc = new ArrayList<Integer>();
        int index = 0; //the index for variable addresses.
        int variableCount = 0;
        pc.add(0);
        boolean declare = false;
        while(scan.hasNextLine()) {
            String line = scan.nextLine();
            line = killComments(line).trim();
            if(line.equals("")) continue;
            if(line.equals(".declare")) {
                declare = true;
            } else if(line.equals(".begin")) {
                declare = false;
            } else if(declare) {
                String[] parts = line.split("\\s+");
                if(parts.length == 1) {
                    symbolTable.put(line, --index);
                    variableCount++;
                } else if(parts.length == 2) {
                    symbolTable.put(parts[0], parse(parts[1]));
                } else {
                    System.out.println("Illegal constant or variable declaration: " + line);
                }
            } else if(line.startsWith(":")) {
                symbolTable.put(line, pc.get(0));
            } else {
                String[] parts = line.split("\\s+");
                syntaxCheck(parts);
                assemble(parts, symbolTable, program, pc);
            }
        }
        for(int i = 0; i < program.size(); i++) {
            if(program.get(i).startsWith(":")) {
                Integer address = symbolTable.get(program.get(i));
                if(address == null) {
                    System.out.println("Undefined branch label: " + program.get(i));
                }
                program.set(i, "" + address);
            }
        }
        for(int i = 0; i < variableCount; i++) {
            program.add("0");
        }
        //System.out.println(program);
        for(String s : program) {
            out.println(s);
        }
    }

    private static void syntaxCheck(String[] parts) {
        if(parts[0].equals("jmp")) {
            checkThat(parts, true, true, false);
        } else if(parts[0].equals("beq")) {
            checkThat(parts, true, true, false);
        } else if(parts[0].equals("ld")) {
            checkThat(parts, true, true, true);
        } else if(parts[0].equals("print") || parts[0].equals("println") || parts[0].equals("printch")) {
            checkThat(parts, false, false, false);
        } else if(parts[0].equals("add") || parts[0].equals("sub") || parts[0].equals("mul") || parts[0].equals("div")) {
            checkThat(parts, false, false, false);
        } else if(parts[0].equals("zero") || parts[0].equals("one")) {
            checkThat(parts, false, false, false);
        } else if(parts[0].equals("dup") || parts[0].equals("dup2") || parts[0].equals("dupn")) {
            checkThat(parts, false, false, false);
        } else if(parts[0].equals("ldi")) {
            checkThat(parts, true, true, true);
        } else if(parts[0].equals("st")) {
            checkThat(parts, true, true, true);
        } else if(parts[0].equals("hlt")) {
            checkThat(parts, false, false, false);
        } else {
            System.out.println("Undefined Instruction: " + parts[0]);
        }
    }

    private static void checkThat(String[] parts, boolean takesArguments, boolean takesLabels, boolean takesAddresses) {
        if(parts.length !=  (takesArguments ? 2 : 1)) {
            System.out.println("Incorrect argument count: " + parts[0]);
            return;
        }
        if(!takesArguments) return;
        boolean label = parts[1].startsWith(":");
        boolean number = parses(parts[1]);
        if(!((takesLabels & label) || takesAddresses & number)) {
            System.out.println("Incorrect argument type: " + parts[1]);
        }
    }

    private static boolean parses(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private static void assemble(String[] parts, HashMap<String, Integer> symbolTable, ArrayList<String> program, ArrayList<Integer> pc) {
        if(parts[0].equals("dup")) {
            program.add("" + 0xA);
            inc(pc);
        } else if(parts[0].equals("dup2")) {
            program.add("" + 0xB);
            inc(pc);
        } else if(parts[0].equals("dupn")) {
            program.add("" + 0xC);
            inc(pc);
        } else if(parts[0].equals("add")) {
            program.add("" + 0x4);
            inc(pc);
        } else if(parts[0].equals("sub")) {
            program.add("" + 0x5);
            inc(pc);

        } else if(parts[0].equals("mul")) {
            program.add("" + 0x6);
            inc(pc);

        } else if(parts[0].equals("div")) {
            program.add("" + 0x7);
            inc(pc);

        } else if(parts[0].equals("ld")) {
            program.add("" + 0x2);
            inc(pc);
            Integer address = symbolTable.get(parts[1]);
//            System.out.println(symbolTable);
//            System.out.println(parts[1] + " :: " + symbolTable.get(parts[1]));
            if(address == null)
            {
                System.out.println("Undefined variable: " + parts[1]);
            }
            inc(pc);
            program.add("" + address);

        } else if(parts[0].equals("st")) {
            program.add("" + 0xE);
            inc(pc);
            Integer address = symbolTable.get(parts[1]);
            if(address == null)
            {
                System.out.println("Undefined variable: " + parts[1]);
            }
            inc(pc);
            program.add("" + address);

        } else if(parts[0].equals("ldi")) {
            program.add("" + 0xD);
            inc(pc);
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    System.out.println("Undefined constant: " + parts[1]);
                }
                program.add("" + value);
            } else {
                program.add(parts[1]);
            }
            inc(pc);

        } else if(parts[0].equals("beq")) {
            Integer address = symbolTable.get(parts[1]);
            program.add("" + 0xD);
            inc(pc);
            if(address == null)
            {
                program.add(parts[1]);
            } else {
                program.add("" + address);
            }
            inc(pc);
            program.add("" + 0x1);
            inc(pc);

        } else if(parts[0].equals("jmp")) {
            Integer address = symbolTable.get(parts[1]);
            program.add("" + 0xD);
            inc(pc);
            if(address == null)
            {
                program.add(parts[1]);
            } else {
                program.add("" + address);
            }
            inc(pc);
            program.add("" + 0x0);
            inc(pc);

        } else if(parts[0].equals("hlt")) {
            program.add("" + 0xF);
            inc(pc);

        } else if(parts[0].equals("print")) {
            program.add("" + 0x3);
            inc(pc);
            program.add("" + 0x1);
            inc(pc);

        } else if(parts[0].equals("println")) {
            program.add("" + 0x3);
            inc(pc);
            program.add("" + 0x3);
            inc(pc);

        } else if(parts[0].equals("printch")) {
            program.add("" + 0x3);
            inc(pc);
            program.add("" + 0x2);
            inc(pc);

        } else if(parts[0].equals("one")) {
            program.add("" + 0x8);
            inc(pc);

        } else if(parts[0].equals("zero")) {
            program.add("" + 0x9);
            inc(pc);

        }
    }

    private static void inc(ArrayList<Integer> pc) {
        pc.set(0, pc.get(0) + 1);
    }

    private static String killComments(String line) {
        if(line.startsWith(";")) return "";
        if(! line.contains(";")) {
            return line;
        }
        return line.substring(0, line.indexOf(';') - 1);
    }

    public static int parse(String number) {
        try {
            return Integer.parseInt(number);
        } catch(Exception e) {
            System.out.println("Expected a number but instead saw: " + number);
            return Integer.MIN_VALUE;
        }
    }
}
