import assembler.Assembler;
import virtualmachine.StackMachine;

import java.io.*;
import java.util.Scanner;

/**
 * Created by po917265 on 6/23/17.
 */
public class Footnote {

    private static final int DEFAULT_MEMORY_SIZE = 256;

    static class Options {
        boolean sym;
        boolean lines;
        int memory;
        int message;
        Scanner in;
        PrintStream out;
        boolean assemble = true;
        boolean alsoRun = true;
    }

    private static final int VERSION_REQUEST = 1;
    private static final int NO_MESSAGE = 0;
    private static final int BAD_ARGUMENT = 2;
    private static final String currentVersion = "Footnote version 0.1";

    public static void main(String[] args) {
        Options options = getOptions(args);
        if(options.message == BAD_ARGUMENT) {
            printHelp();
            return;
        } else if(options.message == VERSION_REQUEST) {
            System.out.println(currentVersion);
        } else {//no message
            if(options.assemble) {
                Assembler assembler = new Assembler(options.in);
                assembler.assemble();
                for(String i : assembler.program()) {
                    options.out.println(i);
                }
            } else if(!options.assemble || options.alsoRun) {
                StackMachine m = new StackMachine(options.memory);
                m.load(options.in);
                m.run();
            }
        }
    }

    private static void printHelp() {
        System.out.println("Usage: Footnote [options] infile [outfile]");
        System.out.println("Options: ");
        System.out.println("\t-version .... prints a version message and quits.\n\t  All other arguments are ignored.");
        System.out.println("\t-sym .... saves symbol table information to a file symbols.txt.");
        System.out.println("\t-lines .... saves a map from instructions in the assembled file to lines of code in the unassembled file.\n" +
                "\t This information to a file symbols.txt.");
        System.out.println("\t-memory amount .... sets the amount of memory.  amount is the number of 32-bit integers available to the virtual machine.\n" +
                "\tIf this option is not given, the amount of memory is 256.");
    }

    private static Options getOptions(String[] args) {
        String infilename = null;
        Options answer = new Options();
        answer.message = NO_MESSAGE;
        answer.memory = DEFAULT_MEMORY_SIZE;
        boolean infile_set = false;
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-version")) {
                answer.message = VERSION_REQUEST;
                return answer;
            } else if(args[i].equals("-sym")) {
                answer.sym = true;
            } else if(args[i].equals("-lines")) {
                answer.lines = true;
            } else if(args[i].equals("-memory")) {
                if(i+1 == args.length) {
                    System.out.println("-memory: no amount specified.");
                    answer.message = BAD_ARGUMENT;
                    return answer;
                } else {
                    try {
                        answer.memory = Integer.parseInt(args[++i]);
                        if(answer.memory < 0) {
                            System.out.println("Memory amount must be positive.");
                            answer.message = BAD_ARGUMENT;
                            return answer;
                        }
                    } catch(NumberFormatException nfe) {
                        System.out.println("-memory: cannot construct virtual machine with memory limit of " + args[i]);
                        answer.message = BAD_ARGUMENT;
                        return answer;
                    }
                }
            } else if(!infile_set) {
                File[] files = getPossibleInputs(args[i], new File(System.getProperty("user.dir")).listFiles());
                setAll(answer, files);
                infilename = args[i];
            } else {  //it has to be the outfile.
                try {
                    answer.out = new PrintStream(new FileOutputStream(new File(args[i])));
                } catch (FileNotFoundException e) {
                    System.out.println("Could not open file" + args[i]);
                    answer.message = BAD_ARGUMENT;
                    return answer;
                }
            }
        }
        if((answer.sym || answer.lines) && !answer.assemble) {
            System.out.println("-sym and -lines can only be used during assembly.");
        }
        if(answer.in == null) {
            System.out.println("No input file specified.");
            answer.message = BAD_ARGUMENT;
            return answer;
        }
        if(answer.out == null) {
            try {
                answer.out = new PrintStream(new FileOutputStream(new File(infilename + ".i")));
            } catch (FileNotFoundException e) {
                System.out.println("Could not open " + infilename + ".i");
                answer.message = BAD_ARGUMENT;
            }
        }
        return answer;
    }

    private static void setAll(Options answer, File[] files) {
        if(files[0] != null) {
            try {
                answer.in = new Scanner(files[0]);
            } catch (FileNotFoundException e) {
                System.out.println("Could not open file: " + files[0]);
                answer.message = BAD_ARGUMENT;
                return;
            }
            if (files[1] != null) {
                answer.alsoRun = true;
                try {
                    answer.out = new PrintStream(new FileOutputStream(files[1]));
                } catch (FileNotFoundException e) {
                    System.out.println("Could not open file: " + files[1]);
                    answer.message = BAD_ARGUMENT;
                }
            }
        } else if(files[1] != null) {
            answer.assemble = false;  //this is the running a program case.
            try {
                answer.in = new Scanner(files[1]);
            } catch (FileNotFoundException e) {
                System.out.println("Could not open file: " + files[1]);
                answer.message = BAD_ARGUMENT;
                return;
            }
        }
    }

    private static File[] getPossibleInputs(String arg, File[] files) {
        File[] answer;
        File footnote = null;
        File i = null;
        for(File f : files) {
            if(f.getName().equals(arg + ".ftnt")) {
                footnote = f;
            } else if(f.getName().equals(arg + ".i")) {
                i = f;
            }
        }
        answer = new File[2];
        if(footnote == null) {
            answer[1] = i;
        } else if(i == null) {
            answer[0] = footnote;
        } else {
            answer[0] = footnote;
            answer[1] = i;
        }
        return answer;
    }
}
