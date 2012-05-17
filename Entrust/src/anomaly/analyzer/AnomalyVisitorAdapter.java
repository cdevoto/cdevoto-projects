package anomaly.analyzer;

import java.io.File;
import java.io.IOException;

public class AnomalyVisitorAdapter implements EventVisitor, IpEventVisitor,
        IpUrlEventVisitor, UrlEventVisitor {

    @Override
    public void onUrlEvent(String url, String line) throws IOException {
    }

    @Override
    public void onBeforeUrlEventsProcessed() throws IOException {
    }

    @Override
    public void onBeforeUrlFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterUrlFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterUrlEventsProcessed() throws IOException {
    }

    @Override
    public void onIpUrlEvent(String ip, String url, String line)
            throws IOException {
    }

    @Override
    public void onBeforeIpUrlEventsProcessed() throws IOException {
    }

    @Override
    public void onBeforeIpUrlFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterIpUrlFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterIpUrlEventsProcessed() throws IOException {

    }

    @Override
    public void onIpEvent(String ip, String line) throws IOException {
    }

    @Override
    public void onBeforeIpEventsProcessed() throws IOException {
    }

    @Override
    public void onBeforeIpFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterIpFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterIpEventsProcessed() throws IOException {
    }

    @Override
    public void onEvent(String line) throws IOException {

    }

    @Override
    public void onBeforeEventsProcessed() throws IOException {
    }

    @Override
    public void onBeforeFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterFileProcessed(File file) throws IOException {
    }

    @Override
    public void onAfterEventsProcessed() throws IOException {
    }

}
