package tasks;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status = Status.NEW;
    protected TaskType taskType = TaskType.TASK;


    public Task(String title, String description, int id, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
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

    //Сохраним старый toString на всякий случай
//    @Override
//    public String toString() {
//        return "Task{" +
//                "title='" + title + '\'' +
//                ", description='" + description + '\'' +
//                ", id=" + id +
//                ", Status='" + status + '\'' +
//                '}';
//    }

    @Override
    public String toString() {
        return id + "," + taskType + "," + title + "," + status + "," + description + ",\n";
    }
//    public Task fromString(String value){
//        String[] taskFields = value.split(",");
//        try{
//            int importedTasksID = Integer.parseInt(taskFields[0]);
//            Status importedTasksStatus = Status.valueOf(taskFields[3]);
//            return new Task(taskFields[2], taskFields[4], importedTasksID, importedTasksStatus);
//        }
//        catch (NumberFormatException ex){
//            ex.printStackTrace();
//        }
//        System.out.println("Не удалось импортировать задачу!");
//        return null;
//    }

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
