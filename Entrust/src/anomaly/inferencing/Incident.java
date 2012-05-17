package anomaly.inferencing;

public class Incident {
    
    private String objType;
    private String id;
    private String startTime;
    private String endTime;
    private String duration;
    private String resolutionType;
    
    public Incident(String objType, String id, String startTime) {
        super();
        this.objType = objType;
        this.id = id.replace("|", ":");
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getObjType() {
        return objType;
    }

    public String getId() {
        return id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }    

    public String getStartTime() {
        return startTime;
    };
    
    @Override
    public String toString() {
        return "{ \"type\":\"incident\", \"objType\":\"" + objType + "\", \"id\":\"" + id + "\", \"startTime\":\"" + startTime + "\", \"endTime\":\"" + endTime + "\", \"duration\":\"" + duration + "\", \"resolutionType\":\"" + resolutionType + "\" }";

    }


}
