/**
 * LeetCode014
 * 
 * Problem: Longest Common Prefix
 * 
 * 题目：最长公共前缀
 * 
 * Description: Write a function to find the longest common prefix string amongst an array of strings.
 * If there is no common prefix, return an empty string "".
 * 
 * Note: All given inputs are in lowercase letters a-z.
 * 
 * 描述：编写一个函数来查找字符串数组中的最长公共前缀。如果不存在公共前缀，返回空字符串 ""。
 * 
 * 说明: 所有输入只包含小写字母 a-z。
 * 
 * Example 1: 
 *      Input: ["flower","flow","flight"]
 *      Output: "fl"
 * Example 2: 
 *      Input: ["dog","racecar","car"]
 *      Output: ""
 *      Explanation: There is no common prefix among the input strings.
 * 
 * 示例 1：
 *      输入: ["flower","flow","flight"]
 *      输出: "fl"
 * 示例 2：
 *      输入: ["dog","racecar","car"]
 *      输出: ""
 *      原因: 输入不存在公共前缀。
 */
public class LeetCode014 {
    public static void main(String[] args) {
        String[] strs = {"flower","flow","flight"};
        strs = new String[]{"dog","racecar","car"};
        strs = new String[]{"aa","a"};
        String result = longestCommonPrefix(strs);
        System.out.println(result);
    }   
    public static String longestCommonPrefix(String[] strs) {
        if (strs.length == 0 || strs[0].length() == 0) {
            return "";
        }
        int index = 0;

        while (index<strs[0].length()) {
            char c = strs[0].charAt(index);

            for (int i = 1, len = strs.length; i < len; i++) {
                if (index>=strs[i].length() || c!=strs[i].charAt(index)) {
                    return index==0?"":strs[0].substring(0, index);  
                }
            }
            index++;
        }
        return index==0?"":strs[0].substring(0, index);  
    }
}