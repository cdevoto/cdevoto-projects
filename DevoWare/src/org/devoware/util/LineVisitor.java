package org.devoware.util;

import java.io.IOException;

public interface LineVisitor {
    
    public void processLine (String line) throws IOException;

}
