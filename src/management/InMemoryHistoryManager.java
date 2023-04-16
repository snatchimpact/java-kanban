package management;

import tasks.Task;
import utilities.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public HashMap<Integer, Node<Task>> tasksViewingHistory = new HashMap<>();
    public CustomLinkedList customLinkedList = new CustomLinkedList();

    static class CustomLinkedList {
        private Node<Task> head = null;
        private Node<Task> tail = null;
        public void linkLast(Task task){
            Node<Task> newNode = new Node<>(task);
            if(head == null){
                head = tail = newNode;
                head.previous = null;
            } else {
                tail.next = newNode;
                newNode.previous = tail;
                tail = newNode;
            }
            tail.next = null;
        }

        public ArrayList<Task> getTasks(){
            if (head == null){
                System.out.println("История задач - пуста");
                return null;
            } else{
                ArrayList<Task> viewedTasksArrayList = new ArrayList<>();
                Node<Task> current = head;
                while (current != null){
                    viewedTasksArrayList.add(current.task);
                    current = current.next;
                }
                return viewedTasksArrayList;
            }
        }


        public void removeNode(Node<Task> node){
            if (node == null || head == null) {
                System.out.println("При удалении передан null, либо список пуст, не можем удалить");
            }

            else {
                if (node == head) {
                    head = node.next;
                }
                if (node == tail) {
                    tail = node.previous;
                }
                if (node.next != null){
                    node.next.previous = node.previous;
                }
                if(node.previous != null){
                    node.previous.next = node.next;
                }
            }

        }
    }



    @Override
    public void add(Task task) {
        if(tasksViewingHistory.containsKey(task.getId())){
            remove(task.getId());
        }
        customLinkedList.linkLast(task);
        tasksViewingHistory.put(task.getId(),customLinkedList.tail);
    }

    @Override
    public void remove(int id){
        Node<Task> nodeToBeDeleted = tasksViewingHistory.get(id);
        tasksViewingHistory.remove(id);
        customLinkedList.removeNode(nodeToBeDeleted);
    }
    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public String toString() {
        List<Task> taskList = getHistory();
        if(taskList == null){
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < taskList.size(); i++) {
                builder.append(taskList.get(i).getId());
                if (i < taskList.size() - 1) {
                    builder.append(",");
                }
            }
            return builder.toString();
        }
    }
}