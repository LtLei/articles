# 正则表达式匹配

> 题目：正则表达式匹配

> 描述：给定一个字符串 (s) 和一个字符模式 (p)。实现支持 '.' 和 '\*' 的正则表达式匹配。

* '.' 匹配任意单个字符。
* '\*' 匹配零个或多个前面的元素。

> 匹配应该覆盖整个字符串 (s) ，而不是部分字符串。

> 说明:

* s 可能为空，且只包含从 a-z 的小写字母。
* p 可能为空，且只包含从 a-z 的小写字母，以及字符 . 和 \*。

> 示例 1:

* 输入: s = "aa", p = "a"
* 输出: false
* 解释: "a" 无法匹配 "aa" 整个字符串。

> 示例 2:

* 输入: s = "aa", p = "a\*"
* 输出: true
* 解释: '\*' 代表可匹配零个或多个前面的元素, 即可以匹配 'a' 。因此, 重复 'a' 一次, 字符串可变为 "aa"。

> 示例 3:

* 输入: s = "ab", p = ".\*"
* 输出: true
* 解释: ".\*" 表示可匹配零个或多个('\*')任意字符('.')。

> 示例 4:

* 输入: s = "aab", p = "c\*a\*b"
* 输出: true
* 解释: 'c' 可以不被重复, 'a' 可以被重复一次。因此可以匹配字符串 "aab"。

> 示例 5:

* 输入: s = "mississippi", p = "mis\*is\*p\*."
* 输出: false

# 解析

这应该是遇到的第一个困难类型的题目了。如果只有 '.' 这个字符，匹配是十分简单的，每匹配一个字符，问题就转化为在剩余的字符串中做相同的操作，例如对于字符串 s="aa", p=".a" 而言，因为第一个字符 'a' 和 '.' 相匹配，所以问题就转化成 s="a", p= "a" 是否匹配。如果我们用函数 f(x, y) 表示字符串 s 的 前 x 位与字符串 p 的前 y 位是否匹配，那么它具有以下的表达式：

```
如果 s.charAt(x)可以匹配p.charAt(y)，则 f(x, y) = f(x+1, y+1)
否则，f(x, y) = false
```

然而，因为 '\*' 定义为可以匹配零个或多个前面的元素，这使得上述规则不再适用，所以我们要对它进行适当的修正。首先，'\*' 必须在某个小写字母或者 '.' 之后，这就意味着我们必须校验一个字符后边是否有 '\*' 标记，如果有，字符串 s 可以与这部分不匹配，例如 s="aa", p="b\*aa"，虽然第一个字符 'a' 不能匹配 'b'，但由于 '\*' 的原因，可以忽略这部分不同。所以我们有了如下策略：

```
记 canMatchFirst表示s.charAt(x)是否可以匹配p.charAt(y)
如果p.charAt(y+1)=='\*'，f(x, y) = f(x, y+2) || (canMatchFirst && f(x+1, y))
否则和只有 '.' 时一样，f(x, y) = canMatchFirst && f(x+1, y+1)
```

有了思路，我们就可以写代码了，参考如下：

```java
public boolean isMatch(String s, String p) {
    return isMatch(s, p, s.length(), p.length(), 0, 0);
}
private boolean isMatch(String s, String p, int lenOfS, int lenOfP, int startS, int startP) {
    // 当前参与递归运算的长度
    int currLenOfS = lenOfS - startS;
    int currLenOfP = lenOfP - startP;

    if (currLenOfP == 0) {
        return currLenOfS == 0;
    }

    char pc = p.charAt(startP);
    // 第一个字符是否匹配
    boolean canMatchFirst = currLenOfS != 0 && (pc == '.' || s.charAt(startS) == pc);

    // 第二个字符是 * 的情况
    if (currLenOfP > 1 && p.charAt(startP + 1) == '*') {
        return isMatch(s, p, lenOfS, lenOfP, startS, startP + 2)
                || (canMatchFirst) && isMatch(s, p, lenOfS, lenOfP, startS + 1, startP);
    } else {
        return (canMatchFirst) && isMatch(s, p, lenOfS, lenOfP, startS + 1, startP + 1);
    }
}
```

# 总结

虽然我们经常使用正则表达式，但是它的实现确实是十分复杂的，以上只能当做一个引子，展示了正则表达式最简单的几个情况，但是它却为我们了解正则表达式的实现提供了思路。以上的分析思路也十分重要，在之后的很多问题中，我们都需要这样思考。好了，接下来让我们看一个有趣的题目吧。

# 下题预告

> 题目：盛最多水的容器

> 描述：给定 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0)。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。

>说明：你不能倾斜容器，且 n 的值至少为 2。

<div align="center"><img src ="./image/img_4_1.png" /><br/>图中垂直线代表输入数组 [1,8,6,2,5,4,8,3,7]。在此情况下，容器能够容纳水（表示为蓝色部分）的最大值为 49。</div>

> 示例:

    * 输入: [1,8,6,2,5,4,8,3,7]
    * 输出: 49

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。