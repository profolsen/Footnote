import java.io.File;
import java.io.IOException;
import java.nio.file.*;
/*
MIT License

Copyright (c) 2017 Paul Olsen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
public class Test {
    public static void main(String[] args) {
        File tempDir = new File(System.getProperty("user.dir") + "/temp");
        tempDir.mkdir();
        File[] tests = (new File("samples/")).listFiles();
        for(File f : tests) {
            if(f.getName().contains("include_2") || f.getName().contains("include_3") || f.getName().contains("modulus") || f.getName().equals("include_err")) continue; //these are support files.
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
