package assembler;

import virtualmachine.StackMachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by po917265 on 6/5/17.
 */
public class AllInOne {
    public static void Main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: AllInOne program memoryCapacity");
            return;
        }
        int memoryCapacity = -1;
        try {
            memoryCapacity = Integer.parseInt(args[1]);
        } catch(NumberFormatException nfe) {
            System.out.println("Memory Capacity is not an integer: " + args[1]);
            return;
        }
        try {
            Scanner scan = new Scanner(new File(args[0]));
            Assembler assembler = new Assembler(scan);
            assembler.assemble();
            ArrayList<String> program = assembler.program();
            StackMachine machine = new StackMachine(memoryCapacity);
            for(String s : program) {
                try {
                    machine.load(Integer.parseInt(s));
                } catch(NumberFormatException nfe) {
                    System.out.println("Invalid program");
                    return;
                }
            }
            machine.run();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + args[0]);
            return;
        }
    }
}
