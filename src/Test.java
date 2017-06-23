import assembler.AllInOne;
import emulator.Emulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Created by po917265 on 6/5/17.
 */
public class Test {
    public static void main(String[] args) {
        File tempDir = new File(System.getProperty("user.dir") + "/temp");
        tempDir.mkdir();
        File[] tests = (new File("samples/")).listFiles();
        for(File f : tests) {
            if(f.getName().contains("include_2") || f.getName().contains("include_3") || f.getName().contains("modulus")) continue; //these are support files.
            System.out.println(f);
            try {
                File toRun = new File(tempDir.getPath() + "/" + f.getName());
                Files.copy(f.toPath(), toRun.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if(toRun.getName().endsWith(".i")) {
                    Footnote.main(new String[] {toRun.getPath().substring(0, toRun.getPath().lastIndexOf('.'))});
                } else {
                    Footnote.main(new String[] {toRun.getPath().substring(0, toRun.getPath().lastIndexOf('.')), toRun.getPath().substring(0, toRun.getPath().lastIndexOf('.'))});
                    Footnote.main(new String[] {toRun.getPath().substring(0, toRun.getPath().lastIndexOf('.'))});
                }

            } catch (IOException e) {
                System.out.println(e);
                System.out.println("Error copying " + f);
            }
        }
        for(File trash : tempDir.listFiles()) {
            trash.delete();
        }
        tempDir.delete();

        /*

        AllInOne.Main(new String[]{"samples/program.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/jaltestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/memtestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/stringtestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/errtestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/includetestasm.ftnt", "256"});
        AllInOne.Main(new String[]{"samples/include_1.ftnt", "256"});
        Emulator.main(new String[]{"samples/fibonacci.i", "256"});
        AllInOne.Main(new String[]{"samples/memtestbadasm.ftnt", "256"});
        */
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ie) {

        }
    }
}
