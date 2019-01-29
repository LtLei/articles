# 题目介绍

> **题目**：电话号码的字母组合
> **描述**：给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
> 
> 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
>
> ![数字字母映射](https://upload-images.jianshu.io/upload_images/1696815-9979c53de963eb3a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>
> **示例**:
> * 输入："23"
> 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
>
> **说明**: 尽管上面的答案是按字典序排列的，但是你可以任意选择答案输出的顺序。

# 解析

如果还记得高中学过的排列组合问题，就可以发现以上问题是一个最简单的组合问题。假设输入了 n 个数字，每个数字所包含的字母个数分别是 m<sub>1</sub>, m<sub>2</sub>, ..., m<sub>n</sub>，那么解的个数则为：**C<sup>1</sup><sub>m<sub>1</sub></sub>C<sup>1</sup><sub>m<sub>2</sub></sub>...C<sup>1</sup><sub>m<sub>n</sub></sub>** ，也就是m<sub>1</sub>\*m<sub>2</sub>\*...\*m<sub>n</sub>。虽然这和我们的题目关系不大，但是它限定了时间复杂度的下限，我们在设计算法时可以参考此值。

首先，我们考虑最简单的情况，假如只输入一个数字 2，那么答案的解集是{"a", "b", "c"}，现在，我们输入第二个数字 3，那就是把 "d"、"e"、"f" 分别和 "a" 组合，然后和 "b" 组合，最后再与 "c" 组合。也就是说，我们得到前面的结果，复制多份，每一个结果后都增加一个当前的字符，就可以得到最终结果，由此可以得出第一条思路：递归。参考代码如下：

```java
public List<String> letterCombinations(String digits) {
    if(digits.length()==0)return new ArrayList<>();
    String[] letters = {"abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
    return letterCombinations(digits,letters,digits.length()-1);
}

private List<String> letterCombinations(String digits,String[] letters, int end) {
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
            // 复制前一结果
            List<String> temp = new ArrayList<>(letterCombinations(digits, letters, end-1));
            for (int j = 0; j < temp.size(); j++) {
                // 每一条结果后边都加上当前的一个字符
                temp.set(j, temp.get(j)+letters[index].charAt(i));
            }
            result.addAll(temp);
        }
    }
    return result;
}
```

但是以上的代码并不完美，为了复制递归需要的数据，额外的执行了一段for循环语句，大大增加了时间复杂度，而且由于无法使用StringBuilder等类来操作字符串的拼接，额外创建了许多String变量，又浪费了大量的时间和空间。所以我们需要寻找一种能够使用StringBuilder，又不需要多一层for循环的方法，这个方法就是：递归+回溯。

现在依然假设输入了数字 2，因为还要输入更多的数字，所以这时还得不到最终结果，我们从数字 2 代表的结果中选择一个字符存入到StringBuilder里，例如选择了字符 'a'，现在StringBuilder里的结果就是 'a'。接下来输入数字 3 之后，'d'、'e'、'f' 每个字符都要和 'a' 组合，那么我们可以把 'a' 固定在StringBuilder中，加入 'd' 组成结果 "ad"，然后移除 'd' 再加入 'e'，组成结果 'ae'，...。假如输入了更多数字，就依法炮制，再固定 "ad"，然后加入 'g' 组成 "adg"，移除 'g' 再加入 'h' 组成 "adh"，...。这个过程就是回溯，之所以还有递归是因为每次运算只处理了一个输入的数字。参考代码如下：

```java
public List<String> letterCombinations(String digits) {
    List<String> result = new ArrayList<>();
    if(digits.length()==0)return result;
    String[] letters = {"abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
    StringBuilder sb = new StringBuilder();
    letterCombinations(digits, 0, letters, sb, result);
    return result;
}

private void letterCombinations(String digits, int start,String[] letters, StringBuilder sb, List<String> result){
    if(start==digits.length()){
        if (sb.length()>0) {
            result.add(sb.toString());
        }
        return;
    }
    if (digits.charAt(start)>='2'&&digits.charAt(start)<='9'){
        for (int i = 0; i < letters[digits.charAt(start)-'2'].length(); i++) {
            // 加入一个字符
            sb.append(letters[digits.charAt(start)-'2'].charAt(i));
            // 进入下一级运算
            letterCombinations(digits, start+1, letters, sb, result);
            // 删除最后加入的字符
            sb.deleteCharAt(sb.length()-1);
        }
    }else{
        letterCombinations(digits, start+1, letters, sb, result);
    }

}
```

可以看到递归+回溯算法的实现十分简洁，但是也较为抽象。接下来我们就以示例为例，看看输入字符串为 "23" 时，代码是如何执行的。

首先，获取到字符 '2' 时，函数进到第一轮，代码走到for循环中时把字符 'a' 添加到了 sb（StringBuilder实例）对象中，然后就进入了函数的第二轮，for循环将等待此递归函数执行完成后再继续运行。这时获取到字符 '3'，又把字符 'd' 添加到了sb中，进入了下一轮的递归，也就是函数的第三轮。

第三轮已经得到了完整的结果，便return了，这时第二轮的函数继续执行，它把 sb 中的最后一个字符删除，于是 sb 中又只剩下 'a'，for循环重复这个过程之后，就得到了三个结果，分别是 "ad"，"ae"，"af"。

现在，第二轮的函数结束了，sb 中依然只剩下字符 'a'，回到第一轮时继续执行删除语句，便把 'a' 也删除了，然后继续处理 'b'、处理 'c'，便得到了全部结果。

# 总结

其实早在研究KMP算法时，我们就见到过回溯的身影，之所以有KMP算法就是为了解决暴力算法的回溯问题，当然不是任何情况下都能避免回溯的。

递归+回溯能够解决很多实际的问题，之后的许多题目都可以用这个思路来解决，但是这个算法较为抽象，需要我们多多练习，才能深刻理解。

# 下题预告

> **题目**：四数之和
> **描述**：给定一个包含 n 个整数的数组 nums 和一个目标值 target，判断 nums 中是否存在四个元素 a，b，c 和 d ，使得 a + b + c + d 的值与 target 相等？找出所有满足条件且不重复的四元组。
> **注意**：答案中不可以包含重复的四元组。
> **示例**:
> 给定数组 nums = [1, 0, -1, 0, -2, 2]，和 target = 0。
> 满足要求的四元组集合为：
> ```   
> [
>     [-1,  0, 0, 1],
>     [-2, -1, 1, 2],
>     [-2,  0, 0, 2]
> ] 
> ```

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。