/**
 * LeetCode002
 * 
 * Problem: Add Two Numbers
 * 
 * 题目：两数相加
 * 
 * Description: You are given two non-empty linked lists representing two non-negative integers. 
 * The digits are stored in reverse order and each of their nodes contain a single digit.
 * Add the two numbers and return it as a linked list.
 * You may assume the two numbers do not contain any leading zero, except the number 0 itself.
 * 
 * 描述：给出两个非空的链表用来表示两个非负的整数。其中，它们各自的位数是按照逆序的方式存储的，并且它们的每个节点只能存储一位数字。
 * 如果我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
 * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 * 
 * Example: 
 *      Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
 *      Output: 7 -> 0 -> 8
 *      Explanation: 342 + 465 = 807.
 * 
 * 示例：
 *      输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 *      输出：7 -> 0 -> 8
 *      原因：342 + 465 = 807
 */
public class LeetCode002 {
    public static void main(String[] args) {
        ListNode l1;
        l1 = new ListNode(2);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(3);

        ListNode l2;
        l2 = new ListNode(5);
        l2.next = new ListNode(6);
        l2.next.next = new ListNode(4);
        l2.next.next.next = new ListNode(5);

        ListNode result = addTwoNumbers(l1, l2);
        while(result!=null){
            System.out.println("next is "+result.val);
            result = result.next;
        }
    }   
    
    /**
     * 测试几种特殊情况：
     *      1. 输入[5]和[5]，结果是[0, 1]
     *      2. 输入[1, 2, 3]和[1, 2, 3, 4]，结果是[2, 4, 6, 4]
     * 
     * 时间复杂度：O(max(m, n)),其中 m 是 l1 的长度，n 是 l2 的长度
     * 空间复杂度：O(max(m, n)) + 1
     * 
     * @param l1
     * @param l2
     * @return
     */
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode result = new ListNode(-1);

        int cur = 0;
        // 进位标志
        int identify = 0;
        ListNode resultHandle = result;

        // 避免长度不一致
        while (l1!=null || l2!=null){
             // 进位标志
            int a = l1!=null?l1.val:0;
            int b = l2!=null?l2.val:0;
            cur = a + b + identify;
            identify = cur/10;
            // 当前值
            cur %= 10;
            
            resultHandle.next = new ListNode(cur);
            if(l1!=null){
                l1 = l1.next;
            }
            if(l2!=null){
                l2 = l2.next;
            }
            resultHandle = resultHandle.next;
        }
        
        // 注意进位
        if(identify!=0){
            resultHandle.next = new ListNode(identify);
        }

        return result.next;
    }

    public static class ListNode{
        int val;
        ListNode next;
        ListNode(int x){
            val = x;
        }
    }
}