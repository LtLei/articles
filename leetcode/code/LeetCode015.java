import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LeetCode015
 * 
 * Problem: 3Sum
 * 
 * 题目：三数之和
 * 
 * Description: Given an array nums of n integers, are there elements a, b, c in nums such that a + b + c = 0? 
 * Find all unique triplets in the array which gives the sum of zero.
 * 
 * Note: The solution set must not contain duplicate triplets.
 * 
 * 描述：给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？
 * 找出所有满足条件且不重复的三元组。
 * 
 * 说明: 答案中不可以包含重复的三元组。
 * 
 * Example: 
 *      Given array nums = [-1, 0, 1, 2, -1, -4],
 *      A solution set is:
 *          [
 *              [-1, 0, 1],
 *              [-1, -1, 2]
 *          ]
 * 
 * 示例：
 *      给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 *      满足要求的三元组集合为：
 *          [
 *              [-1, 0, 1],
 *              [-1, -1, 2]
 *          ]
 */
public class LeetCode015 {
    public static void main(String[] args) {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        // nums = new int[]{3,0,-2,-1,1,2};
        List<List<Integer>> result = threeSum(nums);
        System.out.println(result);
    }   
    
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int len = nums.length;
        // 从小到大排序
        Arrays.sort(nums);
        for (int i = 0; i < len && nums[i]<=0; i++) {
            if (i>0 && nums[i] == nums[i-1]) {
                continue;
            }
            int left = i+1;
            int right = len - 1;
            int target = -1*nums[i];
            while (left<right) {
                if (nums[left]+nums[right]==target) {
                    result.add(Arrays.asList(nums[i],nums[left],nums[right]));
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
}