import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode022
 * 
 * Problem: Generate Parentheses
 * 
 * 题目：括号生成
 * 
 * Description: Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.
 * 
 * 描述：给出 n 代表生成括号的对数，请你写出一个函数，使其能够生成所有可能的并且有效的括号组合。
 *      
 * Example: 
 *      given n = 3
 *      a solution set is:
 *          [
 *              "((()))",
 *              "(()())",
 *              "(())()",
 *              "()(())",
 *              "()()()"
 *          ]
 * 
 * 示例：
 *      给出 n = 3
 *      生成结果为:
 *          [
 *              "((()))",
 *              "(()())",
 *              "(())()",
 *              "()(())",
 *              "()()()"
 *          ]
 */
public class LeetCode022 {
    public static void main(String[] args) {
        int n = 3;
        List<String> result = generateParenthesis1(n);
        System.out.println(result);
    }   

    public static List<String> generateParenthesis1(int n) {
        List<String> result = new ArrayList<>();
        generateParenthesis(result, "", 0, 0, n);
        return result;
    }

    private static void generateParenthesis(List<String> result, String curr, int left, int right, int n){
        if (curr.length() == n*2) {
            System.out.println(curr);
            result.add(curr);
            return;
        }
        if (left<n) {
            generateParenthesis(result, curr+'(', left+1, right, n);
        }
        if (right<left) {
            generateParenthesis(result, curr+')', left, right+1, n);
        }
    }

    public static List<String> generateParenthesis2(int n) {
        List<String> result = new ArrayList<>();
        if (n==0) {
            result.add("") ;
        }

        for (int i = 0; i < n; i++) {
            for (String varOut : generateParenthesis2(i)) {
                for (String varIn : generateParenthesis2(n-1-i)) {
                    result.add("("+varOut+")"+varIn);
                }
            }
        }
        return result;
    }
}