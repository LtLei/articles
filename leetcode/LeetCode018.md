# 题目介绍

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

# 解析

四数之和实际上是三数之和的延伸，所以如果我们还记得三数之和是如何求解的，就能很轻易的写出这道题的解法。三数之和是求三个数 a, b, c，它们的和是 0，只要把 0 换成 -d，就能求出此题的解。参考代码如下：

```java
public List<List<Integer>> fourSum(int[] nums, int target) {
    List<List<Integer>> result = new ArrayList<>(); 
    Arrays.sort(nums);

    for (int i = 0; i < nums.length; i++) {
        if (i>0 && nums[i] == nums[i-1]) {
            continue;
        }
        
        for (int j = i+1; j < nums.length; j++) {
            if (j>i+1&&nums[j] == nums[j-1]) {
                continue;
            }
            int sum = target- nums[i] - nums[j];
            int left = j+1;
            int right = nums.length - 1;
            while (left<right) {
                if (nums[left]+nums[right] == sum) {
                    result.add(Arrays.asList(nums[i], nums[j],nums[left],nums[right]));
                    while (left<right && nums[left]==nums[left+1]) {
                        left++;
                    }
                    while (left<right && nums[right]==nums[right-1]) {
                        right--;
                    }
                    left++;
                    right--;
                }else if (nums[left]+nums[right] < sum) {
                    left++;
                }else{
                    right--;
                }
            }
        }
    }
    
    return result;
}
```

# 总结

同样的思路，可以解决很多类似的问题，四数之和本身并不复杂，但是如果没有掌握好三数之和的话，这个题目还是很难解的，对此不熟的小伙伴可以回顾下三数之和的解法。

# 下题预告

> **题目**：删除链表的倒数第N个节点
> **描述**：给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。
> **示例**:
> 给定一个链表: 1->2->3->4->5, 和 n = 2.
> 当删除了倒数第二个节点后，链表变为 1->2->3->5.
> **说明**：给定的 n 保证是有效的。
> **进阶**：你能尝试使用一趟扫描实现吗？

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。