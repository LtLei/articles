import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode013
 * 
 * Problem: Roman to Integer
 * 
 * 题目：罗马数字转整数
 * 
 * Description: the same as LeetCode012
 * 
 * 描述：和LeetCode012一致
 * 
 * Example: 
 *      the same as LeetCode012, just opposite.
 * 
 * 示例：
 *      和LeetCode012一致，只是方向相反
 */
public class LeetCode013 {
    public static void main(String[] args) {
        String s = "MCMXCIV";
        s = "LVIII";
        int result = romanToInt(s);
        System.out.println(result);
    }   
    public static int romanToInt(String s) {
        if (s.isEmpty()) {
            return 0;
        }
        
        // 此处可以直接写map，省去构建数组占用的空间
        String[] romanStr = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int[] romanInt =    {1000, 900, 500,  400,  100,  90,  50,  40,   10,   9,   5,    4,   1};

        Map<String,Integer> map = new HashMap();
        for (int i = 0, len = romanStr.length; i < len; i++) {
            map.put(romanStr[i], romanInt[i]);
        }

        int result = 0;
        int i = 0;
        while (i<s.length()) {
            String tag = s.substring(i, Math.min(i+2, s.length()));
            System.out.println(i+"----"+tag);
            if (map.containsKey(tag)) {
                result+=map.get(tag);
                i+=2;
            }else{
                result += map.get(String.valueOf(s.charAt(i)));
                i++;
            }

        }

        return result;
    }
}