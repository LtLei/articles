/**
 * LeetCode007
 * 
 * Problem: Reverse Integer
 * 
 * 题目：整数反转
 * 
 * Description: Given a 32-bit signed integer, reverse digits of an integer.
 * Assume we are dealing with an environment which could only store integers within the 32-bit signed integer range: [−2^31,  2^31−1]. 
 * For the purpose of this problem, assume that your function returns 0 when the reversed integer  overflows.
 * 
 * 描述：给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
 * 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−2^31, 2^31−1]。
 * 请根据这个假设，如果反转后整数溢出那么就返回 0。
 * 
 * Example 1: 
 *      Input: 123
 *      Output: 321
 * Example 2: 
 *      Input: -123
 *      Output: -321
 * Example 3: 
 *      Input: 120
 *      Output: 21
 * 
 * 示例 1：
 *      输入: 123
 *      输出: 321
 * 示例 2：
 *      输入: -123
 *      输出: -321
 * 示例 3：
 *      输入: 120
 *      输出: 21
 */
public class LeetCode007 {
    public static void main(String[] args) {
        int input = 123;
        input = 1534236469;
        int result = reverse(input);
        System.out.println("the reverse int is : " + result);
    }   
    
    /**
     * 注意边界
     * 
     * 时间复杂度：O(logn)
     * 空间复杂度：O(1)
     * 
     * @param x
     * @return
     */
    public static int reverse(int x) {
        int result = 0;
        while (x!=0) {
            if(result<0 && result<Integer.MIN_VALUE/10 ) return 0;
            if(result>0 && result>Integer.MAX_VALUE/10 ) return 0;
            result = result*10 + x%10;
            x /= 10;
        }

        return result>Integer.MAX_VALUE?0:result;
    }
}