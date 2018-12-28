# 两数之和

2018年就要过去了，我也开始了LeetCode刷题的历程。LeetCode的第一题可以说是开胃菜，它的难度还不至于吓退一个诚心要刷题的人，也能给新来的一点信心。就从它开始LeetCode之旅吧。

> 题目：两数之和

> 描述：给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。

> 示例：
* 给定 nums =[2, 7, 11, 15], target = 9
* 因为 nums[0] + nums[1] = 2 + 7 = 9
* 所以返回 [0, 1]

# 解析

这个题目看起来并不复杂，我们只需要在数组中找到两个数相加之后等于target的值即可。可能我们许多人第一个想法就是双重for循环，也就是下面的解法一：暴力查找的方式。

# 解法一：暴力查找

用外层循环选定一个值，用内层循环判断它们的和是否等于target。示例代码如下：

```java
public int[] twoSum(int[] nums, int target) {
    int len = nums.length;
    for (int i = 0; i < len; i++) {
        for (int j = i + 1; j < len & j != i; j++) {
            if (nums[i] + nums[j] == target) {
                return new int[] { i, j };
            }
        }
    }
    return null;
}
```

这一解法很简单，也很符合我们的思维方式，但是它对于计算机来说就不那么友好了。这个算法的时间复杂度为**O(n<sup>2</sup>)**，虽然用它可以通过测试，但是在面对很大数组时就不再适用，我们应该考虑更高效的方式。

# 解法二：借力哈希表

可以发现，促使我们适用双重for循环的主要原因在于我们知道每个数字的下标，但是不知道它们的值，数组本身就是根据下标获取值容易，而查找较难。如果我们能把下标和值对应起来，就可以解决数组的这一问题，所以考虑使用哈希表。哈希表的特性恰恰可以解决数组查询慢的问题，于是有了以下代码：

```java
public int[] twoSumOptimize(int[] nums, int target) {
    int len = nums.length;
    Map<Integer, Integer> map = new HashMap(len);

    for (int i = 0; i < len; i++) {
        map.put(nums[i], i);
    }
    for (int i = 0; i < len; i++) {
        int temp = target - nums[i];
        if (map.containsKey(temp) && map.get(temp) != i) {
            return new int[] { i, map.get(temp) };
        }
    }

    return null;
}
```

因为要把数组转存成哈希表，所以空间复杂度增加到了**O(n)**，但也因为哈希表的查询优势，时间复杂度降低到了**O(n)**。因为时间复杂度降低的幅度很大，所以这是值得的。

如果追求简练的代码，上述两次for循环还可以合并成一个，代码如下所示：

```java
public int[] twoSumOptimize(int[] nums, int target) {
    int len = nums.length;
    Map<Integer, Integer> map = new HashMap(len);

    for (int i = 0; i < len; i++) {
        int temp = target - nums[i];
        if (map.containsKey(temp) && map.get(temp) != i) {
            return new int[] { map.get(temp), i };
        }
        map.put(nums[i], i);
    }
    return null;
}
```

# 总结

这个简单的题目给我们启示：符合我们思维的算法最容易想到，但对计算机不一定高效。而通过各种题目的锻炼可以增强我们的计算机思维，让计算机能更高效的为我们服务。

# 下题预告

> 题目：两数相加

> 描述：给出两个 **非空** 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 **逆序** 的方式存储的，并且它们的每个节点只能存储 **一位** 数字。如果我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。您可以假设除了数字 0 之外，这两个数都不会以 0 开头。

> 示例：
* 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
* 输出：7 -> 0 -> 8
* 原因：342 + 465 = 807

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。