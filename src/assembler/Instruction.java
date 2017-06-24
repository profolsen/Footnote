package assembler;

import java.io.PrintStream;
import java.util.HashMap;

/*


MIT License

Copyright (c) 2017 Paul Olsen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
public enum Instruction {

    //instructions, and their syntax.
    jal(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[5];
            ans[0] = ("" + 0xD);
            //we need to do incrementing before the linking, so we link back to the correct location.
            pc+= 5;
            //ans[1] = ("" + pc); //should return to this location...
            ans[1] = "+4";
            Integer address = symbolTable.get(parts[1]);
            ans[2] = ("" + 0xD);
            ans[3] = parts[1];
            ans[4] = ("" + 0x0);
            return ans;
        }
    },
    jmp(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[3];
            Integer address = symbolTable.get(parts[1]);
            ans[0] = "" + 0xD;
            pc++;
            ans[1] = parts[1];
            pc++;
            ans[2] = "" + 0x0;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    lda(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[8];
            String base = "!"+parts[1];  //base has to come this way.
            //we have to assume that index is on the stack already.
            ans[0] = "" + 0xD;   //load the base address onto stack.
            pc++;
            ans[1] = "" + base;       //the base address....
            pc++;
            ans[2] = "" + 0x4;   //add the base address to whatever was already on the stack...
            pc++;
            ans[3] = "" + 0x1;
            pc++;
            ans[4] = "" + 0xE;   //store the calculated address as a target to load.
            pc += 3;
            //ans[5] = "" + pc; //where we are storing the value.
            ans[5] = "+2";
            ans[6] = "" + 0x2;    //the load instruction.
            ans[7] = "" + 0x0;  //this value should be overriden by the store instruction above.
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    sda(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[8];
            String base = "!"+parts[1];  //base has to come this way.
            //we have to assume that index is on the stack already.
            ans[0] = "" + 0xD;   //load the base address onto stack.
            pc++;
            ans[1] = "" + base;       //the base address....
            pc++;
            ans[2] = "" + 0x4;   //add the base address to whatever was already on the stack...
            pc++;
            ans[3] = "" + 0x1;
            pc++;
            ans[4] = "" + 0xE;   //store the calculated address as a target to load.
            //pc += 3;
            //ans[5] = "" + pc; //where we are storing the value.
            ans[5] = "+2";
            ans[6] = "" + 0xE;    //the store instruction.
            ans[7] = "" + 0x0;  //this value should be overriden by the store instruction above.
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    beq(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[3];
            Integer address = symbolTable.get(parts[1]);
            ans[0] = "" + 0xD;
            pc++;
            ans[1] = parts[1];
            pc++;
            ans[2] = ("" + 0x1);
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    ld(true, true) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x2);
            pc++;
            Integer address = symbolTable.get(parts[1]);
//            System.out.println(symbolTable);
//            System.out.println(parts[1] + " :: " + symbolTable.get(parts[1]));
            if(address == null)
            {
                error.println("Undefined variable @" + filename + ":" + lineno);
            }
            pc++;
            ans[1] = ("" + address);
            //ans[0] = "" + pc;
            return ans;
        }
    },
    print(new int[] {0x3, 0x1}),
    println(new int[] {0x3, 0x3}),
    printch(new int[] {0x3, 0x2}),
    read(new int[] {0x3, 0x4}),
    diagnostics(new int[] {0x3, 0x0}),
    add(new int[] {0x4, 0x1}),
    sub(new int[] {0x4, 0x2}),
    mul(new int[] {0x4, 0x3}),
    div(new int[] {0x4, 0x4}),
    zero(new int[] {0x8}),
    one(new int[] {0x9}),
    dup(new int[] {0xA}),
    cmp(new int[] {0x4, 0x5}),
    ldi(true, true) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[2];
            ans[0] = ("" + 0xD);
            pc++;
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    safePrint(error, "Undefined constant @" + filename + ":" + lineno);
                }
                ans[1] = ("" + value);
            } else {
                ans[1] = (parts[1]);
            }
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    down(true, true) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[2];
            ans[0] = ("" + 0xB);
            pc++;
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    safePrint(error, "Undefined constant @" + filename + ":" + lineno);
                }
                ans[1] = ("" + value);
            } else {
                ans[1] = (parts[1]);
            }
            pc++;
            return ans;
        }
    },
    st(true, true) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
            String[] ans = new String[2];
            ans[0] = ("" + 0xE);
            pc++;
            Integer address = symbolTable.get(parts[1]);
            if(address == null)
            {
                System.out.println("Undefined variable: @" + filename + ":" + lineno);
            }
            pc++;
            ans[1] = ("" + address);
            return ans;
        }
    },
    ret(new int[] {0x0}),
    hlt(new int[] {0xF});



    private boolean takesLabel;  //true if takes a label (some string starting with ':').
    private boolean takesValue;  //true if takes a value (a decimal integer).
    private int[] code = null; //the code this instruction will assemble into.
            //this field is only used by instructions that are relatively straightforward
            //when they assemble (e.g., add).

    Instruction(boolean takesLabel, boolean takesValue) {
        this.takesValue = takesValue;
        this.takesLabel = takesLabel;
    }

    Instruction(int[] code) {  //simple no argument instructions.
        this.code = code;
        takesLabel = false;
        takesValue = false;
    }

    @Override
    public String toString() {
        String argPart = "";  //what argument the instruction takes.
        if(takesValue && takesLabel) {
            argPart = " label | value";
        } else if(takesValue) {
            argPart = " value";
        } else if(takesLabel) {
            argPart = " label";
        }
        return this.name() + argPart;  //return what kind of syntax the instruction expects.
    }

    /**
     * Performs a syntax check.
     * @param parts the original line of code split on whitespace.
     * @param error where to print errors.  If null, no errors will be printed.
     * @param lineno the line number in the file where we got this line from.
     */
    public void checkSyntax(String[] parts, PrintStream error, int lineno, String filename) {
        if(!(takesLabel || takesValue) && parts.length > 1) {
            safePrint(error, "Incorrect Argument Count for " + name() + " @" + filename + ":" + lineno);
        } else if(takesLabel && !takesValue && !parts[1].startsWith(":")) {
            safePrint(error, "Expected label but found " + parts[1] + " for " + name() + " @" + filename + ":" + lineno);
        } else if(!takesLabel && takesValue && !integer(parts[1])) {
            safePrint(error, "Expected value but found " + parts[1] + " for " + name() + " @" + filename + ":" + lineno);
        } else if((takesLabel || takesValue) && !(integer(parts[1]) || parts[1].startsWith(":"))) {
            safePrint(error, "Expected label or value but found " + parts[1] + " for " + name() + " @" +
                    filename + ":" + lineno);
        }
    }

    /*
    tests if a string is a number or not.  Probably does this in the worst way possible, but it is very easy to code.
     */
    private boolean integer(String soyouthinkyouareanumber) {
        try {
            Integer.parseInt(soyouthinkyouareanumber);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Only prints s if error is not null.
     * @param error
     * @param s
     */
    private static void safePrint(PrintStream error, String s) {
        if(error != null) {
            error.println(s);
        }
    }

    /**
     *
     * @param parts the instruction and arguments.
     * @param symbolTable the symbol table.
     * @param pc the program counter index of current instruction in assembled code.
     * @param error where to print a syntax error if it should occur.
     * @param lineno the line number the instruction being assembled came from.
     * @param filename
     * @return the assembled instruction and updated pc.
     * position 0 is the updated pc, the rest is the assembled instruction.
     * symbols that cannot be resolved are put in the appropriate place in the assembled instruction
     * as symbols.
     */
    public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno, String filename) {
        if(code != null) {
            String[] ans = new String[code.length];
            for(int i = 0; i < ans.length; i++) {
                ans[i] = "" + code[i];
            }
            return ans;
        } else return new String[] {};  //no information so assemble into empty array (not null!).
    }

    /**
     * This method is to get rid of unused field warnings for the members of this enum.
     * It has no other purpose and should never be used.
     */
    private static void getRidOfUnusedFieldWarnings() {
        Instruction s = jal; s = jmp; s = beq; s = add; s = sub; s = mul; s = div; s = cmp;
        s = ret; s = ldi; s = lda; s = sda; s = st; s = dup; s = down; s = print; s = printch;
        s = read; s = diagnostics; s = zero; s = one; s = ld; s = hlt; s = println;
        getRidOfUnusedFieldWarnings_helper();

    }

    /**
     * This method is used to get rid of unused warning on getRidOfUnusedFieldWarnings.
     * This method has no other purpose and should never be used.
     * It is also infinitely recursive ...so yeah.
     */
    private static void getRidOfUnusedFieldWarnings_helper() {
        getRidOfUnusedFieldWarnings();
    }
}
