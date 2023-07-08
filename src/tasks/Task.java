package tasks;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected Status status = Status.NEW;
    protected Type type = Type.TASK;
    protected Duration duration;
    protected LocalTime startTime;

    public Task(String title, String description, int id, Status status, Duration duration, LocalTime startTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Duration duration){
        this.duration = duration;
    }
    public void setStartTime(LocalTime startTime){
        this.startTime = startTime;
    }
    public LocalTime getStartTime(){
        return startTime;
    }
    public  Duration getDuration(){
        return duration;
    }

    public LocalTime getEndTime(){
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return id + ","
                + type + ","
                + title + ","
                + status + ","
                + description + ","
                + duration + ","
                + getStartTime() + ","
                + getEndTime() + ",\n";
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if(title != null){
            hash = hash + title.hashCode();
        }
        hash = hash * 31;

        if (description != null){
            hash = hash + description.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(title, otherTask.title) &&
                Objects.equals(description, otherTask.description) &&
                (id == otherTask.id) &&
                Objects.equals(status, otherTask.status);
    }
}
