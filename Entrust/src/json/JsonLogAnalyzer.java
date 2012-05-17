package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JsonLogAnalyzer {
    
    protected List<File> logFiles = new LinkedList<File>();
    
    public JsonLogAnalyzer () {
    }
    
    public void configureLogFiles (String ... paths) {
        File [] files = new File[paths.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(paths[i]);
        }
        configureLogFiles(files);
    }

    public void configureLogFiles (File ... files) {
        for (File file : files) {
            this.logFiles.add(file);
        }
    }
    
    public void processJsonLogs (JsonLogVisitor visitor) throws IOException {
        if (logFiles.isEmpty()) {
            throw new IllegalStateException("No log files configured for processing.");
        }
        for (File file : logFiles) {
            JsonLogReader in = null;
            try {
                in = new JsonLogReader(new BufferedReader(new FileReader(file)));
                for (String jsonString = in.readJsonObject(); jsonString != null; jsonString = in.readJsonObject()) {
                    visitor.onJsonObject(jsonString);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        
    }

} 
