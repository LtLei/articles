# 两数相加

> 题目：两数相加

> 描述：描述：给出两个 **非空** 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 **逆序** 的方式存储的，并且它们的每个节点只能存储 **一位** 数字。如果我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。您可以假设除了数字 0 之外，这两个数都不会以 0 开头。

> 示例：
* 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
* 输出：7 -> 0 -> 8
* 原因：342 + 465 = 807

# 解析

这个题目相对基础很多，就是两个单链表的处理，我们只要注意进位即可。不过我们要处理好几种特殊情况：

1. 当两个链表为 (4 -> 5) 和 (4 -> 5) 时，因为进位原因，所以结果为 (8 -> 0 -> 1)

2. 当两个链表为 (4 -> 4) 和 (4 -> 5 -> 1) 时，因为两个链表长度不同，所以结果为 (8 -> 9 -> 1)

只要注意处理好以上问题，其它都是常规操作，一起看下参考代码吧：

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    // 用来标记返回结果的头结点，增加一个无效结点可以使代码简单
    ListNode result = new ListNode(-1);

    // 记录当前位置的值
    int cur = 0;
    // 进位标志
    int identify = 0;
    // 用另一个结点来构建链表，这样就不会丢失头结点指针了
    ListNode resultHandle = result;

    // 避免长度不一致
    while (l1!=null || l2!=null){
        int a = l1!=null?l1.val:0;
        int b = l2!=null?l2.val:0;
        // 计算当前值
        cur = a + b + identify;
        // 更新进位标志
        identify = cur/10;
        // 更新当前值
        cur %= 10;
        
        // 当前值存入链表
        resultHandle.next = new ListNode(cur);
        if(l1!=null){
            l1 = l1.next;
        }
        if(l2!=null){
            l2 = l2.next;
        }
        resultHandle = resultHandle.next;
    }
    
    // 注意进位
    if(identify!=0){
        resultHandle.next = new ListNode(identify);
    }

    // 去除无效头结点
    return result.next;
}
```

# 总结

好了，本次题目就是这么简单，主要在于细致，处理好各种边界问题就好。以后我会对题目进行适当的筛选，尽量分享一些能够给我们启发的题目。不过全部的题目都会上传到我的github。

# 下题预告

> 题目：无重复字符的最长子串

> 描述：给定一个字符串，请你找出其中不含有重复字符的最长子串的长度。

> 示例：
* 输入: "abcabcbb"
* 输出: 3 
* 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。