/**
 * LeetCode009
 * 
 * Problem: Palindrome Number
 * 
 * 题目：回文数
 * 
 * Description: Determine whether an integer is a palindrome. An integer is a palindrome when it reads the same backward as forward.
 * 
 * Coud you solve it without converting the integer to a string?
 * 
 * 描述：判断一个整数是否是回文数。回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
 * 
 * 你能不将整数转为字符串来解决这个问题吗？
 * 
 * Example 1: 
 *      Input: 121
 *      Output: true
 * Example 2: 
 *      Input: -121
 *      Output: false
 *      Explanation: 
 *          From left to right, it reads -121. From right to left, it becomes 121-. Therefore it is not a palindrome.
 * Example 3: 
 *      Input: 10
 *      Output: false
 *      Explanation: 
 *          Reads 01 from right to left. Therefore it is not a palindrome. 
 * 
 * 示例 1：
 *      输入: 121
 *      输出: true
 * 示例 2：
 *      输入: -121
 *      输出: false
 *      解释：
 *          从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
 * 示例 3：
 *      输入: 10
 *      输出: false
 *      解释：
 *          从右向左读, 为 01 。因此它不是一个回文数。
 */
public class LeetCode009 {
    public static void main(String[] args) {
       int x = 1221;
       boolean result = isPalindrome(x);
       System.out.println(result);
    }   
    
    public static boolean isPalindrome(int x) {
        if (x<0) {
            return false;
        }
        if(x<10){
            return true;
        }
        if(x % 10 == 0) {
            return false;
        }

        int temp = 0;
        while (x>temp) {
            temp = temp*10 + x%10;
            x /= 10;
        }
        return temp == x || temp/10 == x;
    }
}