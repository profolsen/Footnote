package assembler;

import java.io.PrintStream;

/**
 * @author Paul Olsen
 *
 */
public enum InstructionSyntax {

    //instructions, and their syntax.
    jal(true, false),
    jmp(true, false),
    lda(true, false),
    sda(true, false),
    beq(true, false),
    ld(true, true),
    print(false, false),
    println(false, false),
    printch(false, false),
    read(false, false),
    add(false, false),
    sub(false, false),
    mul(false, false),
    div(false, false),
    zero(false, false),
    one(false, false),
    dup(false, false),
    cmp(false, false),
    ldi(true, true),
    down(true, true),
    st(true, true),
    ret(false, false),
    hlt(false, false);



    private boolean takesLabel;  //true if takes a label (some string starting with ':').
    private boolean takesValue;  //true if takes a value (a decimal integer).

    InstructionSyntax(boolean takesLabel, boolean takesValue) {
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
    private void safePrint(PrintStream error, String s) {
        if(error != null) {
            error.println(s);
        }
    }
}
