package test;

import assembler.AllInOne;
import emulator.Emulator;

/**
 * Created by po917265 on 6/5/17.
 */
public class Test {
    public static void main(String[] args) {
        AllInOne.Main(new String[]{"samples/program.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/jaltestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/memtestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/stringtestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/errtestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/includetestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/include_1.ftnt", "256"});
        Emulator.main(new String[]{"samples/fibonacci.i", "256"});
        AllInOne.Main(new String[]{"samples/memtestbadasm.ftnt", "256"});
    }
}
