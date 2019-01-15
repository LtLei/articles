/**
 * LeetCode006
 * 
 * Problem: ZigZag Conversion
 * 
 * 题目：Z 字形变换
 * 
 * Description: The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: 
 * (you may want to display this pattern in a fixed font for better legibility)
 * 
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 * 
 * And then read line by line: "PAHNAPLSIIGYIR"
 * 
 * Write the code that will take a string and make this conversion given a number of rows:
 * string convert(string s, int numRows);
 * 
 * 描述：将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。
 * 比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：
 * 
 * P   A   H   N
 * A P L S I I G
 * Y   I   R
 * 
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。
 * 请你实现这个将字符串进行指定行数变换的函数：
 * string convert(string s, int numRows);
 * 
 * Example 1: 
 *      Input: s = "PAYPALISHIRING", numRows = 3
 *      Output: "PAHNAPLSIIGYIR"
 *    
 * Example 2: 
 *      Input: s = "PAYPALISHIRING", numRows = 4
 *      Output: "PINALSIGYAHRPI"
 *      Explanation:
 *          P     I    N
 *          A   L S  I G
 *          Y A   H R
 *          P     I
 * 
 * 示例 1：
 *      输入: s = "LEETCODEISHIRING", numRows = 3
 *      输出: "LCIRETOESIIGEDHN"
 * 
 * 示例 2：
 *      输入: s = "LEETCODEISHIRING", numRows = 4
 *      输出: "LDREOEIIECIHNTSG"
 *      解释：
 *          P     I    N
 *          A   L S  I G
 *          Y A   H R
 *          P     I
 */
public class LeetCode006 {
    public static void main(String[] args) {
        // String s = "LEETCODEISHIRING";
        String s = "ABC";
        int numRows = 3;
        String result = convert(s, numRows);
        System.out.println(result);
    }

    /**
     * 找出规律就比较简单
     * 
     * 时间复杂度：O(n) 
     * 空间复杂度：O(n)
     * 
     * @param s
     * @param numRows
     * @return
     */
    public static String convert(String s, int numRows) {
        int len = s.length();
        if (len == 0 || numRows < 2)
            return s;
        int m = numRows - 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j < len + m; j += 2 * m) {
                if (i != 0 && i != m) {
                    if (j >= i && j - i < len) {
                        sb.append(s.charAt(j - i));
                    }
                }
                if (j + i < len) {
                    sb.append(s.charAt(j + i));
                }
            }
        }

        return sb.toString();
    }
}