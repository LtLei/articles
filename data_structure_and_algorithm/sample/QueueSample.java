import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 演示队列的使用
 */
public class QueueSample{
    public static void main(String[] args) {
        LinkQueue linkQueue = new LinkQueue();
        for (int i = 0; i < 10; i++) {
            linkQueue.enqueue(i);
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(linkQueue.dequeue() + " : " + linkQueue.len);
        }
        
    }
}

class LinkQueue{
    Node front = new Node(null);
    Node rear = new Node(null);
    int len = 0;
    
    public void enqueue(Integer data){
        // 空表时数据放入front中
        if(front.data == null){
            front.data = data;
            len++;
            return;
        }

        // 第二条数据放在rear中，其他数据向后追加
        if(rear.data==null){
            rear.data= data;
            front.next = rear;
        }else{
            Node newNode = new Node(data);
            rear.next = newNode;
            rear = newNode;
        }
        len++;
    }

    public Integer dequeue(){
        if (front.data == null) {
            return null;
        }
        Integer ret = front.data;
        front = front.next;
        len--;
        return ret;
    }
}

class Node{
    Integer data;
    Node next;

    public Node(Integer data){
        this.data = data;
    }
}