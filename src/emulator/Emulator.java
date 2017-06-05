package emulator;

import java.io.File;
import java.util.Scanner;

public class Emulator {

    public static void main(String[] args) throws Exception{

        StackMachine sm = new StackMachine(256);
        Scanner scan = new Scanner(new File("memtest.txt"));
        int i = 0;
        while(scan.hasNextInt()) {
            sm.load(scan.nextInt());
        }
        sm.run();
    }
}
