/**
 * LeetCode004
 * 
 * Problem: Median of Two Sorted Arrays
 * 
 * 题目：寻找两个有序数组的中位数
 * 
 * Description: There are two sorted arrays nums1 and nums2 of size m and n respectively. 
 * Find the median of the two sorted arrays. The overall run time complexity should be O(log (m+n)).
 * You may assume nums1 and nums2 cannot be both empty.
 * 
 * 描述：给定两个大小为 m 和 n 的有序数组 nums1 和 nums2。
 * 请你找出这两个有序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。
 * 你可以假设 nums1 和 nums2 不会同时为空。
 * 
 * Example 1: 
 *      nums1 = [1, 3]
 *      nums2 = [2]
 *      The median is 2.0
 * 
 * Example 2: 
 *      nums1 = [1, 2]
 *      nums2 = [3, 4]
 *      The median is (2 + 3)/2 = 2.5
 * 
 * 示例 1：
 *      nums1 = [1, 3]
 *      nums2 = [2]
 *      则中位数是 2.0
 * 示例 2：
 *      nums1 = [1, 2]
 *      nums2 = [3, 4]
 *      则中位数是 (2 + 3)/2 = 2.5
 */
public class LeetCode004 {
    public static void main(String[] args) {
        // int[] nums1 = {1, 2};
        // int[] nums2 = {3, 4};

        int[] nums1 = {};
        int[] nums2 = {1};

        // double result = findMedianSortedArrays(nums1, nums2);
        double result = findMedianSortedArraysOptimize(nums1, nums2);
        System.out.println(result);

    }   
    
    /**
     * 归并排序法
     * 
     * 时间复杂度：O(max(m, n))
     * 空间复杂度：O(m+n)
     * 
     * @param nums1
     * @param nums2
     * @return
     */
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if(nums1 == null || nums1.length == 0){
            return findMedianSortedArrays(nums2);
        }
        if(nums2 == null || nums2.length == 0){
            return findMedianSortedArrays(nums1);
        }

        int[] nums = new int[nums1.length+nums2.length];
        int index1 = 0;
        int index2 = 0;
        int index = 0;
        while (index1<nums1.length && index2<nums2.length) {
            if(nums1[index1]<nums2[index2]){
                nums[index++] = nums1[index1++];
            }else{
                nums[index++] = nums2[index2++];
            }
        }

        while(index1<nums1.length){
            nums[index++] = nums1[index1++];
        }

        while(index2<nums2.length){
            nums[index++] = nums2[index2++];
        }

        return findMedianSortedArrays(nums);
    }

    private static double findMedianSortedArrays(int[] nums){
        int len = nums.length;
        if(len%2==1){
            return nums[len/2];
        }else{
            return (nums[len/2-1]+nums[len/2])/2.0;
        }
    }

    /**
     * 将问题转化为获取第 k 大的值，其中 k=(m+n)/2。此思路来源于网络。
     * 
     * 时间复杂度：O(log(m+n))
     * 空间复杂度：O(1)
     * 
     * @param nums1
     * @param nums2
     * @return
     */
    public static double findMedianSortedArraysOptimize(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int k = (len1+len2)/2;
        if((len1+len2)%2 == 1){
            return findMedianSortedArraysOptimize(nums1, nums2, len1, len2, 0, 0, k+1);
        }else{
            return (findMedianSortedArraysOptimize(nums1, nums2, len1, len2, 0, 0, k) + findMedianSortedArraysOptimize(nums1, nums2, len1, len2, 0, 0, k+1))/2.0;
        }
    }

    /**
     * 获取第 k 大的值
     * 
     * @param nums1
     * @param nums2
     * @param len1 数组 1 使用的长度
     * @param len2 数组 2 使用的长度
     * @param start1 数组 1 有效的起点
     * @param start2 数组 2 有效的起点
     * @return 第 k 大的值
     */
    private static int findMedianSortedArraysOptimize(int[] nums1, int[] nums2, int len1, int len2, int start1, int start2, int k){
        if(len1 > len2){
            return findMedianSortedArraysOptimize(nums2, nums1, len2, len1, start2, start1, k);
        }
        if(len1 == 0){
            return nums2[start2 + k - 1];
        }

        if(k == 1){
            return Math.min(nums1[start1], nums2[start2]);
        }

        int ignore1 = Math.min(k/2, len1);
        int ignore2 = k - ignore1;
        if(nums1[start1 + ignore1 - 1] < nums2[start2 + ignore2 - 1]){
            // 抛弃数组 1 比较位之前的值
            return findMedianSortedArraysOptimize(nums1, nums2, len1-ignore1, len2, start1+ignore1, start2, k-ignore1);
        }else if(nums1[start1 + ignore1 - 1] > nums2[start2 + ignore2 - 1]){
            // 抛弃数组 2 比较位之前的值
            return findMedianSortedArraysOptimize(nums1, nums2, len1, len2-ignore2,  start1, start2+ignore2, k-ignore2);
        }else{
            // 寻找到了第 k 大的值
            return nums1[start1+ignore1-1];
        }
    }
    
}