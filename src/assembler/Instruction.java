package assembler;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author Paul Olsen
 *
 */
public enum Instruction {

    //instructions, and their syntax.
    jal(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[5];
            ans[0] = ("" + 0xD);
            //we need to do incrementing before the linking, so we link back to the correct location.
            pc+= 5;
            ans[1] = ("" + pc); //should return to this location...
            Integer address = symbolTable.get(parts[1]);
            ans[2] = ("" + 0xD);
            if(address == null)
            {
                ans[3] = (parts[1]);
            } else {
                ans[3] = ("" + address);
            }
            ans[4] = ("" + 0x0);
            return ans;
        }
    },
    jmp(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[3];
            Integer address = symbolTable.get(parts[1]);
            ans[0] = "" + 0xD;
            pc++;
            if(address == null)
            {
                ans[1] = parts[1];
            } else {
                ans[1] = "" + address;
            }
            pc++;
            ans[2] = "" + 0x0;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    lda(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[7];
            String base = "!"+parts[1];  //base has to come this way.
            //we have to assume that index is on the stack already.
            ans[0] = "" + 0xD;   //load the base address onto stack.
            pc++;
            ans[1] = "" + base;       //the base address....
            pc++;
            ans[2] = "" + 0x4;   //add the base address to whatever was already on the stack...
            pc++;
            ans[3] = "" + 0xE;   //store the calculated address as a target to load.
            pc += 3;
            ans[4] = "" + pc; //where we are storing the value.
            ans[5] = "" + 0x2;    //the load instruction.
            ans[6] = "" + 0x0;  //this value should be overriden by the store instruction above.
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    sda(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[7];
            String base = "!"+parts[1];  //base has to come this way.
            //we have to assume that index is on the stack already.
            ans[0] = "" + 0xD;   //load the base address onto stack.
            pc++;
            ans[1] = "" + base;       //the base address....
            pc++;
            ans[2] = "" + 0x4;   //add the base address to whatever was already on the stack...
            pc++;
            ans[3] = "" + 0xE;   //store the calculated address as a target to load.
            pc += 3;
            ans[4] = "" + pc; //where we are storing the value.
            ans[5] = "" + 0xE;    //the store instruction.
            ans[6] = "" + 0x0;  //this value should be overriden by the store instruction above.
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    beq(true, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[3];
            Integer address = symbolTable.get(parts[1]);
            ans[0] = "" + 0xD;
            pc++;
            if(address == null)
            {
                ans[1] = parts[1];
            } else {
                ans[1] = "" + address;
            }
            pc++;
            ans[2] = ("" + 0x1);
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    ld(true, true) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x2);
            pc++;
            Integer address = symbolTable.get(parts[1]);
//            System.out.println(symbolTable);
//            System.out.println(parts[1] + " :: " + symbolTable.get(parts[1]));
            if(address == null)
            {
                error.println("Undefined variable @" + lineno);
            }
            pc++;
            ans[1] = ("" + address);
            //ans[0] = "" + pc;
            return ans;
        }
    },
    print(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x3);
            pc++;
            ans[1] = ("" + 0x1);
            //ans[0] = "" + pc;
            return ans;

        }
    },
    println(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x3);
            pc++;
            ans[1] = ("" + 0x3);
            //ans[0] = "" + pc;
            return ans;
        }
    },
    printch(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x3);
            pc++;
            ans[1] = ("" + 0x2);
            //ans[0] = "" + pc;
            return ans;
        }
    },
    read(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x3);
            pc++;
            ans[1] = ("" + 0x4);
            //ans[0] = "" + pc;
            return ans;
        }
    },
    diagnostics(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0x3);
            pc++;
            ans[1] = ("" + 0x0);
            //ans[0] = "" + pc;
            return ans;
        }
    },
    add(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x4;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    sub(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x5;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    mul(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x6;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    div(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x7;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    zero(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x8;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    one(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x9;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    dup(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0xA;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    cmp(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0xC;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    ldi(true, true) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0xD);
            pc++;
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    safePrint(error, "Undefined constant @" + lineno);
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
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0xB);
            pc++;
            if(parts[1].startsWith(":")) {
                Integer value = symbolTable.get(parts[1]);
                if(value == null) {
                    safePrint(error, "Undefined constant @" + lineno);
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
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[2];
            ans[0] = ("" + 0xE);
            pc++;
            Integer address = symbolTable.get(parts[1]);
            if(address == null)
            {
                System.out.println("Undefined variable: @" + lineno);
            }
            pc++;
            ans[1] = ("" + address);
            return ans;
        }
    },
    ret(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0x0;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    },
    hlt(false, false) {
        @Override
        public String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno) {
            String[] ans = new String[1];
            ans[0] = "" + 0xF;
            pc++;
            //ans[0] = "" + pc;
            return ans;
        }
    };



    private boolean takesLabel;  //true if takes a label (some string starting with ':').
    private boolean takesValue;  //true if takes a value (a decimal integer).

    Instruction(boolean takesLabel, boolean takesValue) {
        this.takesValue = takesValue;
        this.takesLabel = takesLabel;
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
    public void checkSyntax(String[] parts, PrintStream error, int lineno) {
        if(!(takesLabel || takesValue) && parts.length > 1) {
            safePrint(error, "Incorrect Argument Count for " + name() + " @" + lineno);
        } else if(takesLabel && !takesValue && !parts[1].startsWith(":")) {
            safePrint(error, "Expected label but found " + parts[1] + " for " + name() + " @" + lineno);
        } else if(!takesLabel && takesValue && !integer(parts[1])) {
            safePrint(error, "Expected value but found " + parts[1] + " for " + name() + " @" + lineno);
        } else if((takesLabel || takesValue) && !(integer(parts[1]) || parts[1].startsWith(":"))) {
            safePrint(error, "Expected label or value but found " + parts[1] + " for " + name() + " @" +
                                lineno);
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
     * @return the assembled instruction and updated pc.
     * position 0 is the updated pc, the rest is the assembled instruction.
     * symbols that cannot be resolved are put in the appropriate place in the assembled instruction
     * as symbols.
     */
    public abstract String[] assemble(String[] parts, HashMap<String, Integer> symbolTable, int pc, PrintStream error, int lineno);

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
