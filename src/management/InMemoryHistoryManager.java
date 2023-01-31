package management;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public HashMap<Integer, Node<Task>> tasksViewingHistory = new HashMap<>();
    public CustomLinkedList customLinkedList = new CustomLinkedList();

    //Оформляем внутренний класс CustomLinkedList и складываем в него все методы, относящиеся к нему
    static class CustomLinkedList {
        public Node<Task> head = null;
        public Node<Task> tail = null;
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
            //Удаление из CustomLinkedList
            //Проверяем, не null ли нам передали, не пуст ли список
            if (node == null || head == null) {
                System.out.println("При удалении передан null, либо список пуст, не можем удалить");
            }

            else {
                //Проверяем, не head ли надо удалить
                if (node == head) {
                    //Если да, то делаем head-ом следующего
                    head = node.next;
                }
                //Проверяем, не tail ли надо удалить
                if (node == tail) {
                    //Если да, то делаем head-ом следующего
                    tail = node.previous;
                }
                //Если next у удаляемого - не null, изменим его
                if (node.next != null){
                    node.next.previous = node.previous;
                }
                //Если previous у удаляемого - не null, изменим его
                if(node.previous != null){
                    node.previous.next = node.next;
                }
            }

        }
    }




    @Override
    public void add(Task task) {
        //Проверяем, есть ли задача в списке
        if(tasksViewingHistory.containsKey(task.getId())){
            //Если есть - нужно удалить.
            remove(task.getId());
        }
        //Добавляем задачу в конец нашего CustomLinkedList, формируя из неё узел tail
        customLinkedList.linkLast(task);
        //Добавляем узел tail в нашу мапу с историей просмотров
        tasksViewingHistory.put(task.getId(),customLinkedList.tail);
    }

    @Override
    public void remove(int id){
        //Сначала временно сохраняем Node чтоб после удаления Nod-а из мапы с историей удалить его из Custom LinkedList
        Node<Task> nodeToBeDeleted = tasksViewingHistory.get(id);
        //Удаляем из мапы
        tasksViewingHistory.remove(id);
        //Удаляем из CustomLinkedList
        customLinkedList.removeNode(nodeToBeDeleted);
    }
    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

}