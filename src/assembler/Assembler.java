package assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by po917265 on 6/3/17.
 */
public class Assembler {

    static int lineNo = 1;

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: Assembler infile outfile");
            return;
        }
        Scanner scan = null;
        try {
            scan = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open " + args[0]);
            return;
        }
        PrintStream out = null;
        try {
            out = new PrintStream(args[1]);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open " + args[1]);
        }
        ArrayList<String> program = assemble(scan);
        //System.out.println(program);
        for(String s : program) {
            out.print(s + " ");
        }
        scan.close();
        out.close();
    }

    public static ArrayList<String> assemble(Scanner in) {
        lineNo = 1;
        HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
        ArrayList<String> program = new ArrayList<String>();
        ArrayList<Integer> pc = new ArrayList<Integer>();
        HashMap<Integer, String> stringLocationMap = new HashMap<Integer, String>();
        TreeMap<Integer, Integer> pc2line = new TreeMap<Integer, Integer>();
        int index = 0; //the index for variable addresses.
        int variableCount = 0;
        pc.add(0);
        boolean declare = false;
        while(in.hasNextLine()) {
            String line = in.nextLine();
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
                } else if(parts.length == 3 && parts[1].equals("length")) {
                    try {
                        int length = Integer.parseInt(parts[2]);
                        variableCount += length;
                        index -= length;
                        symbolTable.put(parts[0], index);
                    } catch(NumberFormatException nfe) {
                        System.out.println("Illegal array length constant @" + lineNo);
                    }
                } else if(parts.length >= 3 && parts[1].equals("is")) {
                    String[] pieces = line.split("\\s+", 3);
                    pieces[2] = pieces[2].substring(1, pieces[2].length() - 1); //get rid of single quotes.
                    int length = pieces[2].length() + 1;
                    variableCount += length;
                    index -= length;
                    symbolTable.put(pieces[0], index);
                    stringLocationMap.put(index, pieces[2]);
                } else {
                    System.out.println("Illegal constant or variable declaration @" + lineNo);
                }
            } else if(line.startsWith(":")) {
                symbolTable.put(line, pc.get(0));
            } else {
                String[] parts = line.split("\\s+");
                syntaxCheck(parts);
                pc2line.put(pc.get(0), lineNo);
                assemble(parts, symbolTable, program, pc);
            }
            lineNo++;
        }
        for(int i = 0; i < variableCount; i++) {
            program.add("0");
        }
        for(int i : stringLocationMap.keySet()) {
            String s = stringLocationMap.get(i);
            for(char c : s.toCharArray()) {
                program.set(program.size() + i, "" + (int)c);
                i++;
            }
            program.set(program.size() + i, "" + 0);
        }
        for(int i = 0; i < program.size(); i++) {
            if(program.get(i).startsWith(":")) {
                Integer address = symbolTable.get(program.get(i));
                if(address == null) {
                    System.out.println("Undefined branch label @" + pc2line.get(pc2line.floorKey(i)));
                }
                program.set(i, "" + address);
            } else if(program.get(i).startsWith("!")) {
                program.set(i, program.get(i).substring(1));
                Integer address = symbolTable.get(program.get(i));
                if(address == null) {
                    System.out.println("Undefined array name: @" + pc2line.get(pc2line.floorKey(i)));
                } else {
                    program.set(i, "" + (program.size() + address));
                }
            }
        }
        in.close();
        return program;
    }

    private static void syntaxCheck(String[] parts) {
        try { //try to syntax check the instruction.
            Instruction is = Instruction.valueOf(parts[0]);
            is.checkSyntax(parts, System.out, lineNo);
        } catch(IllegalArgumentException iae) {  //invalid instruction name.  Report this.
            System.out.println("Undefined Instruction " + parts[0] + " @" + lineNo);
        }
    }

    private static void checkThat(String[] parts, boolean takesArguments, boolean takesLabels, boolean takesAddresses) {
        if(parts.length !=  (takesArguments ? 2 : 1)) {
            System.out.println("Incorrect argument count @" + lineNo);
            return;
        }
        if(!takesArguments) return;
        boolean label = parts[1].startsWith(":");
        boolean number = parses(parts[1]);
        if(!((takesLabels & label) || takesAddresses & number)) {
            System.out.println("Incorrect argument type: @" + lineNo);
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
        } else if(parts[0].equals("down")) {//retiring dup2... if(parts[0].equals("dup2")) {
            program.add("" + 0xB);
            inc(pc);
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    System.out.println("Undefined constant @" + lineNo);
                }
                program.add("" + value);
            } else {
                program.add(parts[1]);
            }
            inc(pc);
        } else if(parts[0].equals("cmp")) {//retiring dupn... if(parts[0].equals("dupn")) {
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
                System.out.println("Undefined variable @" + lineNo);
            }
            inc(pc);
            program.add("" + address);

        } else if(parts[0].equals("st")) {
            program.add("" + 0xE);
            inc(pc);
            Integer address = symbolTable.get(parts[1]);
            if(address == null)
            {
                System.out.println("Undefined variable: @" + lineNo);
            }
            inc(pc);
            program.add("" + address);

        } else if(parts[0].equals("ldi")) {
            program.add("" + 0xD);
            inc(pc);
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    System.out.println("Undefined constant @" + lineNo);
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
        }  else if(parts[0].equals("diagnostics")) {
            program.add("" + 0x3);
            inc(pc);
            program.add("" + 0x0);
            inc(pc);
        } else if(parts[0].equals("read")) {
            program.add("" + 0x3);
            program.add("" + 0x4);
        }else if(parts[0].equals("one")) {
            program.add("" + 0x9);
            inc(pc);

        } else if(parts[0].equals("zero")) {
            program.add("" + 0x8);
            inc(pc);
        } else if(parts[0].equals("jal")) {
            program.add("" + 0xD);
            inc(pc);
            inc(pc);
            inc(pc);
            inc(pc);
            inc(pc);  //we need to do incrementing before the linking, so we link back to the correct location.
            program.add("" + pc.get(0)); //should return to this location...
            Integer address = symbolTable.get(parts[1]);
            program.add("" + 0xD);
            if(address == null)
            {
                program.add(parts[1]);
            } else {
                program.add("" + address);
            }
            program.add("" + 0x0);
        } else if(parts[0].equals("lda")) {
            String base = "!"+parts[1];  //base has to come this way.
            //we have to assume that index is on the stack already.
            program.add("" + 0xD);   //load the base address onto stack.
            inc(pc);
            program.add(base);       //the base address....
            inc(pc);
            program.add("" + 0x4);   //add the base address to whatever was already on the stack...
            inc(pc);
            program.add("" + 0xE);   //store the calculated address as a target to load.
            inc(pc);
            inc(pc);
            inc(pc);
            program.add("" + pc.get(0)); //where we are storing the value.
            program.add("" + 0x2);    //the load instruction.
            program.add("" + 0x0);  //this value should be overriden by the store instruction above.
            inc(pc);

        } else if(parts[0].equals("sda")) {
            String base = "!"+parts[1];  //base has to come this way.
            //we have to assume that index is on the stack already.
            program.add("" + 0xD);   //load the base address onto stack.
            inc(pc);
            program.add(base);       //the base address....
            inc(pc);
            program.add("" + 0x4);   //add the base address to whatever was already on the stack...
            inc(pc);
            program.add("" + 0xE);   //store the calculated address as a target to load.
            inc(pc);
            inc(pc);
            inc(pc);
            program.add("" + pc.get(0)); //where we are storing the value.
            program.add("" + 0xE);    //the store instruction.
            program.add("" + 0x0);  //this value should be overriden by the store instruction above.
            inc(pc);

        } else if(parts[0].equals("ret")) {
            program.add("" + 0x0);
            inc(pc);
        }
    }

    private static String lookUp(HashMap<String, Integer> symbolTable, String name) {
        Integer value = symbolTable.get(name);
        if(value == null)
        {
            return name;
        } else {
            return "" + value;
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
            System.out.println("Invalid integer value @" + lineNo);
            return Integer.MIN_VALUE;
        }
    }
}
