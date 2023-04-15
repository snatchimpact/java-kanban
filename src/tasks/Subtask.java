package tasks;

public class Subtask extends Task {
    protected int epicID;

    public Subtask(Epic epic, String title, String description, int id, Status status) {
        super(title, description, id, status);
        epicID = epic.getId();
        type = Type.SUBTASK;
    }

    public Subtask(int epicID, String title, String description, int id, Status status){
        super(title, description, id, status);
        this.epicID = epicID;
        type = Type.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString(){
        return id + "," + type + "," + title + "," + status + "," + description + "," + epicID + "\n";
    }
//    public Subtask fromString(String value){
//        String[] subtaskFields = value.split(",");
//        try{
//            int importedSubtasksID = Integer.parseInt(subtaskFields[0]);
//            return new Subtask(subtaskFields[2], subtaskFields[4], importedSubtasksID);
//        }
//        catch (NumberFormatException ex){
//            ex.printStackTrace();
//        }
//        System.out.println("Не удалось импортировать задачу!");
//        return null;
//    }
}

