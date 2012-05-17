package org.devoware.util;

import java.io.IOException;
import java.io.PrintWriter;

public interface FileOutputGenerator {
    
    public void generateOutput (PrintWriter out) throws IOException;

}
