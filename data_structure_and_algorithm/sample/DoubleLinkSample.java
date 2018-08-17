import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 演示双向链表的使用
 */
public class DoubleLinkSample{
    public static void main(String[] args) {
        Node head = new Node(null);
        Node tail = new Node(null);

        Node first = new Node(2);
        Node second = new Node(5);
        // next和prev成对设置
        second.next = tail;
        tail.prev = second;
        first.next = second;
        second.prev = first;
        head.next = first;
        first.prev = head;

        // 注意顺序，①把first也链接在insert后，②再把insert链接在head后
        // 因为通常情况下我们只持有head实例的引用
        Node insert = new Node(7);
        insert.next = head.next;
        head.next.prev = insert;
        head.next = insert;
        insert.prev = head;

        // 删除数据2
        Node delete = head.next;
        Node prev = delete;
        while(delete!=null){
            if(delete.data == 2){
                // 要注意顺序，先把后边的数据，挂载在前一数据后边
                prev.next = delete.next;
                delete.next.prev = prev;
                // 再删除数据2
                delete.next = null;
                delete.prev = null;
                break;
            }
            // 记录前一数据
            prev = delete;
            delete = delete.next;
        }
        
        Node print = head.next;
        while (print!=null) {
            System.out.println(print.data);
            print = print.next;
        }

        Node printFromTail = tail.prev;
        while(printFromTail!=null){
            System.out.println(printFromTail.data);
            printFromTail = printFromTail.prev;
        }
    }
}

class Node{
    Integer data;
    Node prev;
    Node next;

    public Node(Integer data){
        this.data = data;
    }
}