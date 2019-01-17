/**
 * LeetCode010
 * 
 * Problem: Regular Expression Matching
 * 
 * 题目：正则表达式匹配
 * 
 * Description: Given an input string (s) and a pattern (p), implement regular expression matching with support for '.' and '*'.
 * 
 *  '.' Matches any single character.
 *  '*' Matches zero or more of the preceding element.
 * 
 * The matching should cover the entire input string (not partial).
 * 
 * Note:
 *      s could be empty and contains only lowercase letters a-z.
 *      p could be empty and contains only lowercase letters a-z, and characters like . or *.
 * 
 * 描述：给定一个字符串 (s) 和一个字符模式 (p)。实现支持 '.' 和 '*' 的正则表达式匹配。
 * 
 *  '.' 匹配任意单个字符。
 *  '*' 匹配零个或多个前面的元素。
 * 
 * 匹配应该覆盖整个字符串 (s) ，而不是部分字符串。
 * 
 * 说明:
 *      s 可能为空，且只包含从 a-z 的小写字母。
 *      p 可能为空，且只包含从 a-z 的小写字母，以及字符 . 和 *。
 * 
 * Example 1: 
 *      Input: s = "aa" p = "a"
 *      Output: false
 *      Explanation: "a" does not match the entire string "aa".
 * Example 2: 
 *      Input: s = "aa" p = "a*"
 *      Output: true
 *      Explanation: '*' means zero or more of the precedeng element, 'a'. Therefore, by repeating 'a' once, it becomes "aa".
 * Example 3: 
 *      Input: s = "ab" p = ".*"
 *      Output: true
 *      Explanation: ".*" means "zero or more (*) of any character (.)".
 * Example 4: 
 *      Input: s = "aab" p = "c*a*b"
 *      Output: true
 *      Explanation: c can be repeated 0 times, a can be repeated 1 time. Therefore it matches "aab".
 * Example 1: 
 *      s = "mississippi" p = "mis*is*p*."
 *      Output: false
 * 
 * 示例 1：
 *      输入：s = "aa" p = "a"
 *      输出：false
 *      原因："a" 无法匹配 "aa" 整个字符串。
 * 示例 2：
 *      输入：s = "aa" p = "a*"
 *      输出：true
 *      原因：'*' 代表可匹配零个或多个前面的元素, 即可以匹配 'a' 。因此, 重复 'a' 一次, 字符串可变为 "aa"。
 * 示例 3：
 *      输入：s = "ab" p = ".*"
 *      输出: true
 *      原因：".*" 表示可匹配零个或多个('*')任意字符('.')。
 * 示例 4：
 *      输入：s = "aab" p = "c*a*b"
 *      输出：true
 *      原因：'c' 可以不被重复, 'a' 可以被重复一次。因此可以匹配字符串 "aab"。
 * 示例 5：
 *      输入：s = "mississippi" p = "mis*is*p*."
 *      输出：false
 */
public class LeetCode010 {
    public static void main(String[] args) {
        boolean result = false;

        String s = "aaa";
        String p = "a*b";
        result = isMatch(s, p);
        System.out.println(result);

        s = "aa";
        p = "a";
        result = isMatch(s, p);
        System.out.println(result);

        s = "aa";
        p = "a*";
        result = isMatch(s, p);
        System.out.println(result);

        s = "ab";
        p = ".*";
        result = isMatch(s, p);
        System.out.println(result);

        s = "aab";
        p = "c*a*b";
        result = isMatch(s, p);
        System.out.println(result);

        s = "mississippi";
        p = "mis*is*p*.";
        result = isMatch(s, p);
        System.out.println(result);

        s = "mississippi";
        p ="mis*is*ip*.";
        result = isMatch(s, p);
        System.out.println(result);

        s = "a";
        p ="ab*";
        result = isMatch(s, p);
        System.out.println(result);

        s = "ab";
        p = ".*..";
        result = isMatch(s, p);
        System.out.println(result);
    }   
    
    public static boolean isMatch(String s, String p) {
        return isMatch(s, p, s.length(), p.length(), 0, 0);
    }

    /**
     * 递归
     * 
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 
     * @param s
     * @param p
     * @param lenOfS
     * @param lenOfP
     * @param startS
     * @param startP
     * @return
     */
    private static boolean isMatch(String s, String p, int lenOfS, int lenOfP, int startS, int startP) {
        int currLenOfS = lenOfS - startS;
        int currLenOfP = lenOfP - startP;

        if (currLenOfP == 0) {
            return currLenOfS == 0;
        }

        char pc = p.charAt(startP);
        boolean canMatchFirst = currLenOfS != 0 && (pc == '.' || s.charAt(startS) == pc);

        if (currLenOfP > 1 && p.charAt(startP + 1) == '*') {
            return isMatch(s, p, lenOfS, lenOfP, startS, startP + 2)
                    || (canMatchFirst) && isMatch(s, p, lenOfS, lenOfP, startS + 1, startP);
        } else {
            return (canMatchFirst) && isMatch(s, p, lenOfS, lenOfP, startS + 1, startP + 1);
        }
    }
}