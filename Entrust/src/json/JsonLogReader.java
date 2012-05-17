package json;

import static util.AppUtil.HR1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class JsonLogReader {
    
    private static int defaultCharBufferSize = 8192;    
    
    private Reader in;
    private char [] cb;
    private int index = 0;
    private int charsInBuffer = 0;

    
    public JsonLogReader (Reader in) {
        this.in = in;
        this.cb = new char[defaultCharBufferSize];
    }
    
    public String readJsonObject () throws IOException {
        if (index == -1 || index == charsInBuffer) {
            charsInBuffer = in.read(cb);
            if (charsInBuffer == -1) {
                return null;
            }
            index = 0;
        }    
        StringBuilder buf = new StringBuilder();
        Stack<Character> braceMatcher = new Stack<Character>();
        boolean incidentFound = false;
        while (!incidentFound || !braceMatcher.isEmpty()) {
            if (index == charsInBuffer) {
                charsInBuffer = in.read(cb);
                if (charsInBuffer == -1) {
                    return null;
                }
                index = 0;
            }    
            char c = cb[index++];
            if (c == '{') {
                incidentFound = true;
                braceMatcher.push(c);
            } else if (c == '}') {
                braceMatcher.pop();
            }
            if (incidentFound) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public void close () throws IOException {
        in.close();
    }

    public static void main(String[] args) throws Exception {
        JsonLogReader in = null;
        
        try {
            in = new JsonLogReader(new BufferedReader(new FileReader(
                    "G:/dec23_anomalies/incidents.log")));
            int count = 1;
            for (String incident = in.readJsonObject(); incident != null; incident = in.readJsonObject()) {
                System.out.println(HR1);
                System.out.println("Incident #" + (count++));
                System.out.println(HR1);
                System.out.println(incident);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
