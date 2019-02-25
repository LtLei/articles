/**
 * LeetCode019
 * 
 * Problem: Remove Nth Node From End of List
 * 
 * 题目：删除链表的倒数第N个节点
 * 
 * Description: Given a linked list, remove the n-th node from the end of list and return its head. 
 * 
 * Note: Given n will always be valid. Could you do this in one pass?
 * 
 * 描述：给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。
 * 
 * 注意：给定的 n 保证是有效的。你能尝试使用一趟扫描实现吗？
 *      
 * Example: 
 *      Given linked list: 1->2->3->4->5, and n = 2.
 *      After removing the second node from the end, the linked list becomes 1->2->3->5.
 * 
 * 示例：
 *      给定一个链表: 1->2->3->4->5, 和 n = 2.
 *      当删除了倒数第二个节点后，链表变为 1->2->3->5.
 */
public class LeetCode019 {
    public static void main(String[] args) {
        ListNode node = new ListNode(1);
        node.next = new ListNode(2);
        node.next.next = new ListNode(3);
        node.next.next.next = new ListNode(4);
        node.next.next.next.next = new ListNode(5);

        removeNthFromEnd(node, 2);

        while (node!=null) {
            System.out.print(node.val + "\t");
            node = node.next;
        }
    }   

    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode first = new ListNode(0);
        first.next = head;
        ListNode node1 = first;
        ListNode node2 = first;
        int i = 0;
        while (i<n) {
            node1 = node1.next;
            i++;
        }

        while (node1.next!=null) {
            node1 = node1.next;
            node2 = node2.next;
        }

        node2.next = node2.next.next;

        return first.next;
    }

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }
}