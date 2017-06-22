package test;

import assembler.AllInOne;
import emulator.Emulator;

/**
 * Created by po917265 on 6/5/17.
 */
public class Test {
    public static void main(String[] args) {
        AllInOne.Main(new String[]{"samples/program.txt", "256"});
        AllInOne.Main(new String[]{"samples/jaltestasm.txt", "256"});
        AllInOne.Main(new String[]{"samples/memtestasm.txt", "256"});
        AllInOne.Main(new String[]{"samples/stringtestasm.txt", "256"});
        AllInOne.Main(new String[]{"samples/errtestasm.txt", "256"});
        AllInOne.Main(new String[]{"samples/includetestasm.txt", "256"});
        AllInOne.Main(new String[]{"samples/include_1.ftnt", "256"});
        Emulator.main(new String[]{"samples/fibonacci.txt", "256"});
        AllInOne.Main(new String[]{"samples/memtestbadasm.txt", "256"});
    }
}
