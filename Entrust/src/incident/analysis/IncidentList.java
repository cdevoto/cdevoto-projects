package incident.analysis;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import util.AppUtil;
import anomaly.inferencing.Incident;

public class IncidentList implements Iterable<Incident> {
    private String target;
    private List<Incident> incidents = new LinkedList<Incident>();
    private Incident currentIncident;

    public IncidentList(String target) {
        this.target = target;
    }

    public void startIncident(String objectType, String id, String startTime) {
        this.currentIncident = new Incident(objectType, id, startTime);
        this.incidents.add(currentIncident);
    }

    public void endIncident(String endTime, String resolutionType) {
        if (currentIncident == null) {
            System.out
                    .println("WARNING - Attempted to close an incident that was never created. { \"target\":\""
                            + target
                            + "\", \"endTime\":\""
                            + endTime
                            + "\", \"resolutionType\":\""
                            + resolutionType
                            + "\"");
        } else {
            Date startTimeDate = AppUtil.parseDate(currentIncident
                    .getStartTime());
            Date endTimeDate = AppUtil.parseDate(endTime);
            String duration = AppUtil.formatElapsedTime(endTimeDate.getTime()
                    - startTimeDate.getTime());
            currentIncident.setEndTime(endTime);
            currentIncident.setResolutionType(resolutionType);
            currentIncident.setDuration(duration);
            currentIncident = null;
        }

    }

    public boolean hasCurrentIncident() {
        return currentIncident != null;
    }

    public int size() {
        return incidents.size();
    }

    @Override
    public Iterator<Incident> iterator() {
        return incidents.iterator();
    }

    @Override
    public String toString() {
        return "[IncidentList: currentIncident=" + hasCurrentIncident()
                + ", incidents=" + incidents + "]";
    }
}
