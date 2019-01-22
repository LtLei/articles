# 题目介绍

> **题目**：三数之和
> **描述**：给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？找出所有满足条件且不重复的三元组。
> **注意**：答案中不可以包含重复的三元组。
> 例如, 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
> 满足要求的三元组集合为：
> ```
> [
>      [-1, 0, 1],
>      [-1, -1, 2]
> ]
> ```

# 解析

LeetCode的第一题就是两数之和，看到本题后多少会有些亲切，如果从数组中选择一个元素，记为target，问题就会转变为从余下的元素中找到两个值，使得它们的和为 (target * -1)，这和两数之和有些相似，所以问题的关键在于去重。

如果相同的数字相邻，去重就变得十分简单了，所以第一步我们要对原数组进行排序。排序后，如果从左往右取target，那么包含target的全部解都在它的右侧。接下来只要在右侧部分寻找两个数，它们的和是target的相反数即可。因为Map无法保存相同的key，所以两数之和的方法无法借鉴，但是因为数组是有序的，我们可以使用左右两个指针来表示这两个数，将它们与 (target * -1) 比较，通过向左或向右移动指针来进行调整。根据这个思路，可以参考以下代码：

```java
public List<List<Integer>> threeSum(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    int len = nums.length;
    // 从小到大排序
    Arrays.sort(nums);
    for (int i = 0; i < len && nums[i]<=0; i++) {
        // target的值不可以重复
        if (i>0 && nums[i] == nums[i-1]) {
            continue;
        }
        int left = i+1;
        int right = len - 1;
        int target = -1*nums[i];
        while (left<right) {
            if (nums[left]+nums[right]==target) {
                result.add(Arrays.asList(nums[i],nums[left],nums[right]));
                // 因为只有两个数字，只要一个数字重复，另一个数字也必然重复，就有可能出现重复解
                while (left<right && nums[left]==nums[left+1]) {
                    left++;
                }
                while (left<right && nums[right]==nums[right-1]) {
                    right--;
                }
                left++;
                right--;
            }else if(nums[left]+nums[right]<target){
                left++;
            }else{
                right--;
            }
        }
    }
    
    return result;
}
```

# 总结

当我们遇到需要去重问题时，要尽量把重复的元素放在一起，以方便我们的操作。同时，固定一个元素来寻找其他元素的思想，在之后的许多题目中都有体现。

# 下题预告

> **题目**：最接近的三数之和
> **描述**：给定一个包括 n 个整数的数组 nums 和 一个目标值 target。找出 nums 中的三个整数，使得它们的和与 target 最接近。返回这三个数的和。假定每组输入只存在唯一答案。
> 
> 例如，给定数组 nums = [-1，2，1，-4], 和 target = 1.
> 与 target 最接近的三个数的和为 2. (-1 + 2 + 1 = 2).

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。