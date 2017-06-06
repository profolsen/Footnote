package emulator;

import virtualmachine.StackMachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Emulator {

    public static void main(String[] args){
        if(args.length != 2) {
            System.out.println("Usage: Emulator infile memoryCapacity");
            return;
        }
        StackMachine sm = null;
        try {
            sm = new StackMachine(Integer.parseInt(args[1]));
        } catch(NumberFormatException nfe) {
            System.out.println("Unable to interpret memoryCapacity: " + args[1]);
        }
        Scanner scan = null;

        try {
            scan = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + args[0]);
        }

        int i = 0;
        while(scan.hasNextInt()) {
            sm.load(scan.nextInt());
        }
        sm.run();
    }
}
