import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode001
 * 
 * Problem: Two Sum
 * 
 * 题目：两数之和
 * 
 * Description: Given an array of integers, return indices of the two numbers such that they add up to a specific target.
 * You may assume that each input would have exactly one solution, and you may not use the same element twice.
 * 
 * 描述：给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。
 * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
 * 
 * Example: 
 *      Given nums = [2, 7, 11, 15]， target = 9
 *      Because nums[0] + nums[1] = 2 + 7 = 9
 *      return [0, 1].
 * 
 * 示例：
 *      给定 nums =[2, 7, 11, 15], target = 9
 *      因为 nums[0] + nums[1] = 2 + 7 = 9
 *      所以返回 [0, 1]
 */
public class LeetCode001 {
    public static void main(String[] args) {
        int[] nums = new int[] { 2, 7, 11, 15 };
        int target = 9;

        // int[] result = twoSum(nums, target);
        int[] result = twoSumOptimize(nums, target);
        if (result == null) {
            System.err.println("no result.");
        }
        System.out.println("the result is [" + result[0] + ", " + result[1] + "]");
    }

    /**
     * 方法1：暴力查找
     * 
     * 时间复杂度：O(n^2)
     * 
     * 空间复杂度：O(1)
     * 
     * @param nums   数组
     * @param target 目标值
     * @return 符合条件的值的下标
     */
    public static int[] twoSum(int[] nums, int target) {
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

    /**
     * 方法2：哈希表
     * 
     * 时间复杂度：O(n)
     * 
     * 空间复杂度：O(n)
     * 
     * @param nums   数组
     * @param target 目标值
     * @return 符合条件的值的下标
     */
    public static int[] twoSumOptimize(int[] nums, int target) {
        int len = nums.length;
        Map<Integer, Integer> map = new HashMap(len);

        /* for (int i = 0; i < len; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < len; i++) {
            int temp = target - nums[i];
            if (map.containsKey(temp) && map.get(temp) != i) {
                return new int[] { i, map.get(temp) };
            }
        } */

        for (int i = 0; i < len; i++) {
            int temp = target - nums[i];
            if (map.containsKey(temp) && map.get(temp) != i) {
                return new int[] { map.get(temp), i };
            }
            map.put(nums[i], i);
        }
        return null;
    }
}