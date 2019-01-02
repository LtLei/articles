import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode003
 * 
 * Problem: Longest Substring Without Repeating Characters
 * 
 * 题目：无重复字符的最长子串
 * 
 * Description: Given a string, find the length of the longest substring without repeating characters.
 * 
 * 描述：给定一个字符串，请你找出其中不含有重复字符的最长子串的长度。
 * 
 * Example: 
 *      Input: "abcabcbb"
 *      Output: 3 
 *      Explanation: The answer is "abc", with the length of 3. 
 * 
 * 示例：
 *      输入: "abcabcbb"
 *      输出: 3 
 *      解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 */
public class LeetCode003 {
    public static void main(String[] args) {
        // String str = "pwwkew";
        // String str = "abcabcbb";
        // String str = "bbbbb";
        String str = "abcacbd";
        System.out.println(lengthOfLongestSubstring(str));
    }   
    
    /**
     * 使用HashMap
     * 
     * 思路：使用Map来保存当前所有非重复字符，使用一个startIndex标记子串的起点，详情见对应文章解释。
     * 
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 
     * @param s
     */
    public static int lengthOfLongestSubstring(String s){
        Map<Character,Integer> map = new HashMap<>();
        int maxLen = 0;
        int startIndex = 0;
        int tempMaxLen = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            if (map.containsKey(s.charAt(i)) && map.get(s.charAt(i)) >= startIndex) {
                startIndex = map.get(s.charAt(i)) + 1;
            }

            map.put(s.charAt(i), i);
            tempMaxLen = i - startIndex + 1;
            maxLen = maxLen < tempMaxLen ? tempMaxLen : maxLen;
        }
        return maxLen;
    }
}