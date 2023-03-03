package management;
//Класс, описывающий узел истории просмотра задач
public class Node<Task>{
    public Task task;
    public Node<Task> next;
    public Node<Task> previous;

    public Node(Task task) {
        this.task = task;
        this.next = null;
        this.previous = null;
    }

}
