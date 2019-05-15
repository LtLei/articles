/**
 * LeetCode021
 * 
 * Problem: Merge Two Sorted Lists
 * 
 * 题目：合并两个有序链表
 * 
 * Description: Merge two sorted linked lists and return it as a new list. 
 * The new list should be made by splicing together the nodes of the first two lists.
 * 
 * 描述：将两个有序链表合并为一个新的有序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
 * 
 * Example: 
 *      Input: 1->2->4, 1->3->4
 *      Output: 1->1->2->3->4->4
 * 
 * 示例：
 *      输入: 1->2->4, 1->3->4
 *      输出: 1->1->2->3->4->4
 */
public class LeetCode021 {
    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        node1.next = new ListNode(2);
        node1.next.next = new ListNode(4);

        ListNode node2 = new ListNode(1);
        node2.next = new ListNode(3);
        node2.next.next = new ListNode(4);

        ListNode result = mergeTwoLists(node1, node2);
        while (result!=null) {
            System.out.print(result.val+"\t");
            result = result.next;
        }
    }   

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(0);
        ListNode helper = head;
        while (l1!=null && l2!=null) {
            if (l1.val<l2.val) {
                helper.next = l1;
                l1 = l1.next;
            }else{
                helper.next = l2;
                l2 = l2.next;
            }
            helper = helper.next;
        }
        if (l1!=null) {
            helper.next = l1;
        }else{
            helper.next = l2;
        }
        return head.next;
    }

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }
}