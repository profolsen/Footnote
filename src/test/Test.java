package test;

import assembler.AllInOne;

/**
 * Created by po917265 on 6/5/17.
 */
public class Test {
    public static void main(String[] args) {
        AllInOne.Main(new String[]{"program.txt", "256"});
        AllInOne.Main(new String[]{"jaltestasm.txt", "256"});
        AllInOne.Main(new String[]{"memtestasm.txt", "256"});
        AllInOne.Main(new String[]{"stringtestasm.txt", "256"});
    }
}
