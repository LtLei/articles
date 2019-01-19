/**
 * LeetCode011
 * 
 * Problem: Container With Most Water
 * 
 * 题目：盛最多水的容器
 * 
 * Description: Given n non-negative integers a1, a2, ..., an , where each represents a point at coordinate (i, ai). 
 * n vertical lines are drawn such that the two endpoints of line i is at (i, ai) and (i, 0). 
 * Find two lines, which together with x-axis forms a container, such that the container contains the most water.
 * 
 * Note:
 *       You may not slant the container and n is at least 2.
 * 
 * 描述：给定 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。
 * 在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0)。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。
 * 
 * 说明:
 *      你不能倾斜容器，且 n 的值至少为 2。
 * 
 * Example: 
 *      Input: [1,8,6,2,5,4,8,3,7]
 *      Output: 49
 * 
 * 示例：
 *      输入：[1,8,6,2,5,4,8,3,7]
 *      输出：49
 */
public class LeetCode011 {
    public static void main(String[] args) {
        int[] height = { 1, 8, 6, 2, 5, 4, 8, 3, 7 };
        int result = maxArea(height);
        System.out.println(result);
    }   
    
    /**
     * 左右双指针
     * 
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 
     * @param height
     * @return
     */
    public static int maxArea(int[] height) {
        int max = 0;
        int i = 0;
        int j = height.length - 1;
        while (i < j) {
            int width = j - i;
            int area = width * Math.min(height[i], height[j]);
            max = Math.max(area, max);
            if (height[i] < height[j]) {
                i++;
            } else {
                j--;
            }
        }

        return max;
    }
}