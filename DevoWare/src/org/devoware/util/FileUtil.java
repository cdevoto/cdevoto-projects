package org.devoware.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtil {
    
    private FileUtil () {}
    
    public static void readFile (String file, LineVisitor visitor) throws IOException {
        readFile(new File(file), visitor);
    }
    
    public static void readFile (File file, LineVisitor visitor) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                visitor.processLine(line);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    public static void writeFile (String file, FileOutputGenerator generator) throws IOException {
        writeFile(new File(file), generator);
    }

        
    public static void writeFile (File file, FileOutputGenerator generator) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(file));
            generator.generateOutput(out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
