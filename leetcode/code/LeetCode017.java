import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode017
 * 
 * Problem: Letter Combinations of a Phone Number
 * 
 * 题目：电话号码的字母组合
 * 
 * Description: Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent.
 * A mapping of digit to letters (just like on the telephone buttons) is given below. Note that 1 does not map to any letters.
 * 
 *      1[]     2[abc] 3[def]
 *      4[ghi]  5[jkl] 6[mno]
 *      7[pqrs] 8[tuv] 9[wxyz]
 *      *[]     0[]    #[]
 * 
 * 描述：给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 *      
 *      1[]     2[abc] 3[def]
 *      4[ghi]  5[jkl] 6[mno]
 *      7[pqrs] 8[tuv] 9[wxyz]
 *      *[]     0[]    #[]
 *     
 * Example: 
 *      Input: "23"
 *      Output: ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 *      Note: Although the above answer is in lexicographical order, your answer could be in any order you want.
 * 
 * 示例：
 *      输入："23"
 *      输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 *      说明：尽管上面的答案是按字典序排列的，但是你可以任意选择答案输出的顺序。
 */
public class LeetCode017 {
    public static void main(String[] args) {
        String digits = "232";
        List<String> result = letterCombinations(digits);
        System.out.println(result);
    }   

    public static List<String> letterCombinations1(String digits) {
        if(digits.length()==0)return new ArrayList<>();
        String[] letters = {"abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
        return letterCombinations1(digits,letters,digits.length()-1);
    }

    private static List<String> letterCombinations1(String digits,String[] letters, int end) {
        List<String> result = new ArrayList<>();
        if (end==0) {
            int index = digits.charAt(end)-'2';
            for (int i = 0; i < letters[index].length(); i++) {
                result.add(String.valueOf(letters[index].charAt(i)));
            }
            return result;
        }else{
            int index = digits.charAt(end)-'2';
            for (int i = 0; i < letters[index].length(); i++) {
                List<String> temp = new ArrayList<>(letterCombinations1(digits, letters, end-1));
                for (int j = 0; j < temp.size(); j++) {
                    temp.set(j, temp.get(j)+letters[index].charAt(i));
                }
                result.addAll(temp);
            }
        }
        return result;
    }

    public static List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if(digits.length()==0)return result;
        String[] letters = {"abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
        StringBuilder sb = new StringBuilder();
        letterCombinations(digits, 0, letters, sb, result);
        return result;
    }

    private static void letterCombinations(String digits, int start,String[] letters, StringBuilder sb, List<String> result){
        if(start==digits.length()){
            if (sb.length()>0) {
                result.add(sb.toString());
            }
            return;
        }
        if (digits.charAt(start)>='2'&&digits.charAt(start)<='9'){
            for (int i = 0; i < letters[digits.charAt(start)-'2'].length(); i++) {
                sb.append(letters[digits.charAt(start)-'2'].charAt(i));
                System.out.println("start = "+start+", before: "+sb.toString());
                letterCombinations(digits, start+1, letters, sb, result);
                System.out.println("start = "+start+", after: "+sb.toString());
                sb.deleteCharAt(sb.length()-1);
            }
        }else{
            letterCombinations(digits, start+1, letters, sb, result);
        }

    }
}