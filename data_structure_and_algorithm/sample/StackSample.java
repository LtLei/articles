import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 演示栈的使用
 */
public class StackSample{
    public static void main(String[] args) {
        int LOOP_SIZE = 15;

        LinearStack linearStack = new LinearStack();
        for (int i = 0; i < LOOP_SIZE; i++) {
            linearStack.push(i*i);
        }

        for (int i = 0; i < LOOP_SIZE; i++) {
            System.out.println(linearStack.pop());
        }

        LinkStack linkStack = new LinkStack();
        for (int i = 0; i < LOOP_SIZE; i++) {
            linkStack.push(i*i);
        }

        for (int i = 0; i < LOOP_SIZE; i++) {
            System.out.println(linkStack.pop());
        }

    }
}

/**
 * 数组实现栈
 */
class LinearStack{
    Integer[] stack = new Integer[10];
    // 栈顶，也就是可以操作的队尾
    int top = -1;

    public void push(Integer data){
        if(top<stack.length-1){
            stack[++top] = data;
        }
    }

    public Integer pop(){
        if(top<0) return null;

        Integer ret = stack[top];
        stack[top] = null;
        top--;
        return ret;
    }
}

class LinkStack{
    Node top = new Node(null);
    // 定义长度
    static final int SIZE = 10;
    int len = -1;

    public void push(Integer data){
        if(len<SIZE-1){
            if(top.data==null){
                top.data = data;
            }else{
                Node newNode = new Node(data);
                newNode.next = top;
                top = newNode;
            }
            len++;
        }
    }

    public Integer pop(){
        if(len<0) return null;
        Integer ret = top.data;
        Node next = top.next;
        top.next = null;
        top = next;
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