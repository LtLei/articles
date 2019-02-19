import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode018
 * 
 * Problem: 4Sum
 * 
 * 题目：四数之和
 * 
 * Description: Given an array nums of n integers and an integer target, are there elements a, b, c, and d in nums such that a + b + c + d = target? 
 * Find all unique quadruplets in the array which gives the sum of target.
 * 
 * Note: The solution set must not contain duplicate quadruplets.
 * 
 * 描述：给定一个包含 n 个整数的数组 nums 和一个目标值 target，判断 nums 中是否存在四个元素 a，b，c 和 d ，
 * 使得 a + b + c + d 的值与 target 相等？找出所有满足条件且不重复的四元组。
 * 
 * 注意：答案中不可以包含重复的四元组。
 *      
 * Example: 
 *      Given array nums = [1, 0, -1, 0, -2, 2], and target = 0.
 *      A solution set is:
 *          [
 *              [-1,  0, 0, 1],
 *              [-2, -1, 1, 2],
 *              [-2,  0, 0, 2]
 *          ]
 * 
 * 示例：
 *      给定数组 nums = [1, 0, -1, 0, -2, 2]，和 target = 0。
 *      满足要求的四元组集合为：
 *          [
 *              [-1,  0, 0, 1],
 *              [-2, -1, 1, 2],
 *              [-2,  0, 0, 2]
 *          ]
 */
public class LeetCode018 {
    public static void main(String[] args) {
        int[] nums = {1, 0, -1, 0, -2, 2};
        nums = new int[]{0,0,0,0};
        int target = 1;
        List<List<Integer>> result = fourSum(nums, target);
        System.out.println(result);
    }   

    public static List<List<Integer>> fourSum(int[] nums, int target) {
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

}