import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode016
 * 
 * Problem: 3Sum Closest
 * 
 * 题目：最接近的三数之和
 * 
 * Description: Given an array nums of n integers and an integer target, find three integers in nums such that the sum is closest to target. 
 * Return the sum of the three integers. You may assume that each input would have exactly one solution.
 * 
 * 描述：给定一个包括 n 个整数的数组 nums 和 一个目标值 target。找出 nums 中的三个整数，使得它们的和与 target 最接近。
 * 返回这三个数的和。假定每组输入只存在唯一答案。
 * 
 * Example: 
 *      Given array nums = [-1, 2, 1, -4], and target = 1.
 *      The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).
 * 
 * 示例：
 *      给定数组 nums = [-1，2，1，-4], 和 target = 1.
 *      与 target 最接近的三个数的和为 2. (-1 + 2 + 1 = 2).
 */
public class LeetCode016 {
    public static void main(String[] args) {
        int[] nums = {-1, 2, 1, -4};
        int target = 1;
        int result = threeSumClosest(nums, target);
        System.out.println(result);
    }   

    public static int threeSumClosest(int[] nums, int target) {
        int sum = 0;
        int sub = 0;
        int absSub = Integer.MAX_VALUE;

        int len = nums.length;
        // 从小到大排序
        Arrays.sort(nums);
        for (int i = 0; i < len; i++) {
            int left = i+1;
            int right = len - 1;
            while (left<right) {
                sub = nums[i]+nums[left]+nums[right] - target;
                if (absSub>Math.abs(sub)) {
                    absSub = Math.abs(sub);
                    sum = nums[i]+nums[left]+nums[right];
                }
                if (sub>0) {
                    right--;
                }else if(sub<0){
                    left++;
                }else{
                    sum = nums[i]+nums[left]+nums[right];
                    break;
                }
            }
        }
        
        return sum;
    }
}