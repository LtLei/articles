import java.util.Stack;

/**
 * LeetCode020
 * 
 * Problem: Valid Parentheses
 * 
 * 题目：有效的括号
 * 
 * Description: Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
 * 
 * An input string is valid if:
 *      Open brackets must be closed by the same type of brackets.
 *      Open brackets must be closed in the correct order.
 * 
 * Note that an empty string is also considered valid.
 * 
 * 描述：给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 * 
 * 有效字符串需满足：
 *      左括号必须用相同类型的右括号闭合。
 *      左括号必须以正确的顺序闭合。
 * 
 * 注意空字符串可被认为是有效字符串。
 *      
 * Example 1: 
 *      Input: "()"
 *      Output: true
 * Example 2: 
 *      Input: "()[]{}"
 *      Output: true
 * Example 3: 
 *      Input: "(]"
 *      Output: false
 * Example 4: 
 *      Input: "([)]"
 *      Output: false
 * Example 5: 
 *      Input: "{[]}"
 *      Output: true
 * 
 * 示例 1：
 *      输入: "()"
 *      输出: true
 * 示例 2：
 *      输入: "()[]{}"
 *      输出: true
 * 示例 3：
 *      输入: "(]"
 *      输出: false
 * 示例 4：
 *      输入: "([)]"
 *      输出: false
 * 示例 5：
 *      输入: "{[]}"
 *      输出: true
 */
public class LeetCode020 {
    public static void main(String[] args) {
        String s = "{}";
        boolean result = isValid(s);
        System.out.println(result);
    }   

    /**
     * 借助栈来实现
     * 
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * @param s
     * @return
     */
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        int i = 0;
        int len = s.length();
        while (i<len) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            }else{
                if (stack.isEmpty()) {
                    return false;
                }
                char top = stack.peek();
                if (top == '(' && c==')' || top=='[' && c==']' || top=='{' && c=='}') {
                    stack.pop();
                }else{
                    return false;
                }
            }            
            i++;
        }

        return stack.isEmpty();
    }
}