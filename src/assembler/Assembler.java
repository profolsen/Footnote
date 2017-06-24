package assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by po917265 on 6/3/17.
 */
public class Assembler {

    private int lineNo = 1;
    private HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
    private ArrayList<String> program = new ArrayList<String>();
    private int pc = 0;
    private HashMap<Integer, String> stringLocationMap = new HashMap<Integer, String>();
    private TreeMap<Integer, String> pc2line = new TreeMap<Integer, String>();
    private Scanner in;
    private int variableCount = 0;
    private int index = 0;
    private ArrayList<String> includedFiles = new ArrayList<String>();
    private Assembler root = null; //if null, then this is a top level assembler.
    private HashSet<String> branchLabels = new HashSet<String>(); //need to keep track of branch labels
                //for includes, because branch labels are always incorrect when loaded.  They need to be fixed.
                //by an offset.
    private String filename;

    public Assembler(File source) throws FileNotFoundException {
        in = new Scanner(source);
        root = this;
        filename = source.getName();
        includedFiles.add(filename.substring(0, filename.lastIndexOf('.') + 1));
    }

    public Assembler(File source, Assembler root) throws FileNotFoundException {
        this(source);
        this.root = root;
    }

    public void assembleFirstPassOnly() {
        firstPass(false);
        finish();
        in.close();
    }

    //a two pass assembler.
    public void assemble() {
        //ArrayList<Integer> pc = new ArrayList<Integer>();
        firstPass(true);  //the first pass.
        secondPass();  //the second pass.
        finish(); //closing the scanner.
        in.close();
    }

    private void finish() {
        in.close();
    }

    private void secondPass() {
        for(int i = 0; i < program.size(); i++) {  //some macros require relative targets for branching,
            // we need to make these absolute now.
            String x = program.get(i);
            if(x.startsWith("+")) {
                int target = i + Integer.parseInt(x.substring(1));
                program.set(i, "" + target);
            }
        }
        for(int i = 0; i < variableCount; i++) {
            program.add("0");
        }
        placeStrings();
        for(int i = 0; i < program.size(); i++) {
            if(program.get(i).equals(".begin")) {
                program.set(i, "" + symbolTable.get(program.get(i)));
            }
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
    }

    private void placeStrings() {
        for(int i : stringLocationMap.keySet()) {
            String s = stringLocationMap.get(i);
            for(char c : s.toCharArray()) {
                program.set(program.size() + i, "" + (int)c);
                i++;
            }
            program.set(program.size() + i, "" + 0);
        }
    }

    private void firstPass(boolean assembleBranchLabels) {
        boolean declare = false;
        boolean include = false;
        boolean including = false;
        boolean beginbegan = false;

        //the following: always skip to the beginning of the program.
        //because includes are always at the top.
        //but these lines only need to be done IF this is the top level program (i.e., if root == this).
        if(root == this) {
            program.add("" + 0xD);
            program.add(".begin");
            program.add("" + 0x0);
            pc += 3;
        }
        lineNo = 0;
        while(in.hasNextLine()) {
            lineNo++;
            String line = in.nextLine();
            line = killComments(line).trim();
            if(line.equals("")) continue;
            if(line.equals(".declare")) {
                declare = true;
                include = false;
            } else if(line.equals(".include")) {
                include = true;
                declare = false;
            } else if(line.equals(".begin")) {
                if(root == this) symbolTable.put(".begin", pc);
                include = false;
                declare = false;
            } else if(declare) {
                handleDeclare(line);
            } else if(include) {
                handleInclude(line);
            } else if(line.startsWith(":")) {
                symbolTable.put(line, pc);
                branchLabels.add(line);
            } else {
                handleInstruction(line, assembleBranchLabels);
            }
        }
        if(assembleBranchLabels) {

        }
    }

    private void handleInclude(String line) {
        String filename = line + ".ftnt";
        if(! root.includedFiles.contains(filename)) {
            root.includedFiles.add(filename);
            try {
                Assembler assm = new Assembler(new File(filename), root);
                assm.assembleFirstPassOnly();
                ArrayList<String> result = assm.program();
                //we don't want to include the variable stuff... but we __do__ want to include the
                //strings and arrays.
                for(String origKey : assm.symbolTable.keySet()) {
                    String key = redirectKey(origKey, line);
                    if(assm.branchLabels.contains(origKey)) branchLabels.add(key);
                    symbolTable.put(key, assm.symbolTable.get(origKey));
                }
                for(String s : branchLabels) {
                    symbolTable.put(s, symbolTable.get(s) + pc);
                }
                index += assm.index;
                stringLocationMap.putAll(assm.stringLocationMap);
                for(int i = 0; i < assm.program.size(); i++) {
                    if(assm.program.get(i).startsWith(":") || assm.program.get(i).startsWith("!")) {
                        assm.program.set(i, redirectKey(assm.program.get(i), line));
                    }
                }
                program.addAll(result);
                pc += result.size();
                pc2line.putAll(assm.pc2line);
                variableCount += assm.variableCount;
            } catch (FileNotFoundException e) {
                System.out.println("Could not link " + filename + " @" + this.filename + ":" + lineNo);
            }
        } //including a file twice is not an error and has no effect.
    }

    private String redirectKey(String key, String name) {
        if(key.contains(".")) return key;
        boolean exclaim = false;
        if(name.contains("/")) {   //get rid of paths in name.
            name = name.substring(name.lastIndexOf('/') + 1);
        }
        if(key.startsWith(":")) {
            key = key.substring(1);
        } else { // key starts with !
            key = key.substring(2);
            exclaim = true;
        }
        key = ":" + name + "." + key;
        if(exclaim) key = "!" + key;
        return key;
    }

    private void handleInstruction(String line, boolean assembleBranchLabels) {
        String[] parts = line.split("\\s+");
        syntaxCheck(parts);
        pc2line.put(pc, filename + ":" + lineNo);
        assemble(parts, assembleBranchLabels);
    }

    private void syntaxCheck(String[] parts) {
        try { //try to syntax check the instruction.
            Instruction instruction = Instruction.valueOf(parts[0]);
            instruction.checkSyntax(parts, System.out, lineNo, filename);
        } catch(IllegalArgumentException iae) {  //invalid instruction name.  Report this.
            System.out.println("Undefined Instruction " + parts[0] + " @" + filename + ":" + lineNo);
        }
    }

    private void assemble(String[] parts, boolean assembleBranchLabels) {
        try {
            Instruction instruction = Instruction.valueOf(parts[0]);
            String[] assembled = instruction.assemble(parts, symbolTable, pc, System.out, lineNo, filename);
            pc += assembled.length;
            for (String i : assembled) {
                program.add(i);
            }
        } catch (IllegalArgumentException iae) {
            //do nothing.  This error doesn't really matter.
        }
    }

    private static String killComments(String line) {
        if(line.startsWith(";")) return "";
        if(! line.contains(";")) {
            return line;
        }
        return line.substring(0, line.indexOf(';') - 1);
    }

    public int parse(String number) {
        try {
            return Integer.parseInt(number);
        } catch(Exception e) {
            System.out.println("Invalid integer value @" + filename + ":" + lineNo);
            return Integer.MIN_VALUE;
        }
    }

    public void handleDeclare(String line) {
        String[] parts = line.split("\\s+");
        if(parts.length == 1) {
            if(parts[0].contains(".")) {
                System.out.println("Illegal variable name: " + parts[0] + " @ " + filename + ":" + lineNo);
                return;
            }
            symbolTable.put(line, --index);
            variableCount++;
        } else if(parts.length == 2) {
            if(parts[0].contains(".")) {
                System.out.println("Illegal constant name: " + parts[0] + " @ " + filename + ":" + lineNo);
                return;
            }
            symbolTable.put(parts[0], parse(parts[1]));
        } else if(parts.length == 3 && parts[1].equals("length")) {
            try {
                if(parts[0].contains(".")) {
                    System.out.println("Illegal array name: " + parts[0] + " @ " + filename + ":" + lineNo);
                    return;
                }
                int length = Integer.parseInt(parts[2]);
                variableCount += length;
                index -= length;
                symbolTable.put(parts[0], index);
            } catch(NumberFormatException nfe) {
                System.out.println("Illegal array length constant @" + filename + ":" + lineNo);
            }
        } else if(parts.length >= 3 && parts[1].equals("is")) {
            if(parts[0].contains(".")) {
                System.out.println("Illegal string name: " + parts[0] + " @ " + filename + ":" + lineNo);
                return;
            }
            String[] pieces = line.split("\\s+", 3);
            pieces[2] = pieces[2].substring(1, pieces[2].length() - 1); //get rid of single quotes.
            int length = pieces[2].length() + 1;
            variableCount += length;
            index -= length;
            symbolTable.put(pieces[0], index);
            stringLocationMap.put(index, pieces[2]);
        } else {
            System.out.println("Illegal constant or variable declaration @" + filename + ":" + lineNo);
        }
    }

    public ArrayList<String> program() {
        return program;
    }

    public Map<String, Integer> symbolTable() {
        return symbolTable;
    }

    public Map<Integer, String> lineMap() {
        return pc2line;
    }
}
