/**
 * LeetCode005
 * 
 * Problem: Longest Palindromic Substring
 * 
 * 题目：最长回文子串
 * 
 * Description: Given a string s, find the longest palindromic substring in s. You may assume that the maximum length of s is 1000.
 * 
 * 描述：给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。
 * 
 * Example: 
 *      Input: "babad"
 *      Output: "bab"
 *      Note: "aba" is also a valid answer.
 * 
 * 示例：
 *      输入: "babad"
 *      输出: "bab"
 *      注意: "aba" 也是一个有效答案。
 */
public class LeetCode005 {
    public static void main(String[] args) {
        // String s = "babad";
        // String s = "cbbd";
        String s = "b";
        // String result = longestPalindrome(s);
        String result = longestPalindromeOptimize(s);
        System.out.println(result);
    }   
    
    /**
     * 寻找包含某一个字符的最长回文子序列，且此字符为中心
     * 存在 "b" 和 "bb" 两种情况，所以求解也需要分别考虑
     * 
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 
     * @param s
     * @return
     */
    public static String longestPalindrome(String s) {
        int len = s.length();
        if(len==0)return "";

        int i = 0;
        int[] ends = new int[2];
        
        while(i<len){
            getEnds(s, len, i-1, i+1, ends);
            getEnds(s, len, i, i+1, ends);
            i++;
        }

        return s.substring(ends[0], ends[1]);
    }

    private static void getEnds(String s, int len, int left, int right, int[] ends){
        while(left >= 0 && right < len && s.charAt(left)==s.charAt(right)){
            left--;
            right++;
        }
        if((ends[1]-ends[0])<(right-left-1)){
            ends[0] = left+1;
            ends[1] = right;
        }
    }

    /**
     * Manacher算法
     * 
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 
     * @param s
     * @return
     */
    public static String longestPalindromeOptimize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        char[] charArr = manacherStr(s);
        // 构造回文半径数组
        int[] radius = new int[charArr.length];
        // 当前已经计算过的最右侧下标
        int mx = -1;
        // i 表示 mx 最大时，对应的回文子串的中心
        int i = -1;
        // 最长回文子串的长度
        int max = 0;
        // 最长回文子串的起点下标
        int maxIndex = -1;

        for (int j = 0; j < radius.length; j++) {
            // 2*i-j 是 j 相对于 i 的对称点，也就是文章中的 k。
            // 当 j<mx 时，由于以 k 为中心的子串可能被 i 完全覆盖，也可能超出 i 的范围
            // 所以，计算 我们只能计算出 radius[j] 在 mx-j这个范围内的值
            // 而当 j>mx 时，就需要完全从头开始计算了
            radius[j] = j < mx ? Math.min(radius[2 * i - j], mx - j + 1) : 1;

            // 计算 j<mx 时超出 mx-j 范围内的部分，或者 j 本就大于 mx时
            while (j + radius[j] < charArr.length && j - radius[j] >= 0
                    && charArr[j - radius[j]] == charArr[j + radius[j]]) {
                radius[j]++;
            }

            // 更新 mx 的值和 i 的值
            if (j+radius[j]>mx) {
                mx = j+radius[j]-1;
                i = j;
            }

            // 更新max的值
            if (max<radius[j]) {
                max = radius[j];
                // j 的起点坐标，相对于Manacher字符数组而言是 j-radius[j]+1
                // 而相对于原数组而言，这个值是由原数组的位置乘以2再加一得到的，所以直接除以2即可
                maxIndex = (j-radius[j]+1)/2;
            }
        }

        return s.substring(maxIndex,maxIndex+max-1);
    }

    private static char[] manacherStr(String s){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append('#');
            sb.append(s.charAt(i));
        }
        sb.append('#');
        return sb.toString().toCharArray();
    }
}