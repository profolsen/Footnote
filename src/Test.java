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
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ie) {

        }
    }
}
