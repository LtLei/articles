import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 演示单链表的使用
 */
public class SingleLinkSample{
    public static void main(String[] args) {
        Node head = new Node(null,null);
        Node first = new Node(2,null);
        Node second = new Node(5,null);
        first.next = second;
        head.next = first;

        // 注意顺序，①把first也链接在insert后，②再把insert链接在head后
        // 因为通常情况下我们只持有head实例的引用
        Node insert = new Node(7,null);
        insert.next = head.next;
        head.next = insert;

        // 删除数据2
        Node delete = head.next;
        Node prev = delete;
        while(delete!=null){
            if(delete.data == 2){
                // 也要注意顺序，先把后边的数据，挂载在前一数据后边
                prev.next = delete.next;
                // 再删除数据2
                delete.next = null;
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
    }
}

class Node{
    Integer data;
    Node next;

    public Node(Integer data, Node next){
        this.data = data;
        this.next = next;
    }
}