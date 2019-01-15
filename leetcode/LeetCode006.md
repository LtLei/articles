# Z字形变换

> 题目：Z 字形变换

> 描述：将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。

> 比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：

```
    L   C   I   R
    E T O E S I I G
    E   D   H   N
```

> 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。

> 请你实现这个将字符串进行指定行数变换的函数：`string convert(string s, int numRows);`

> 示例 1：
* 输入: s = "LEETCODEISHIRING", numRows = 3
* 输出: "LCIRETOESIIGEDHN"

> 示例 2：
* 输入: 输入: s = "LEETCODEISHIRING", numRows = 4
* 输出: "LDREOEIIECIHNTSG"
* 解释：

```
    L     D     R
    E   O E   I I
    E C   I H   N
    T     S     G
```

# 解析

这个题目只需要找好规律，就可以很快解决了。如果记 m=numRows-1，从纵向来看，第一列对应原串的下标就是 0, 1, 2,..., m，例如示例2的LEET四个字符对应的下标就是0, 1, 2, 3。之后的斜线上字符的下标为 m+1, m+2,..., 2m，例如示例2的COD下标就是4, 5, 6。从横向来看，第一行包含0, 2m, 4m,...等元素，最后一行包含m, 3m, 5m,...等元素，而其余行例如第二行则还包含一个2m-1, 4m-1,...的值。

掌握了以上规律，我们就可以快速解决此问题，参考代码如下：

```java
public String convert(String s, int numRows) {
    int len = s.length();
    if (len == 0 || numRows < 2)
        return s;
    int m = numRows - 1;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i <= m; i++) {
        for (int j = 0; j < len + m; j += 2 * m) {
            if (i != 0 && i != m) {
                if (j >= i && j - i < len) {
                    sb.append(s.charAt(j - i));
                }
            }
            if (j + i < len) {
                sb.append(s.charAt(j + i));
            }
        }
    }

    return sb.toString();
}
```

# 总结

LeetCode上还有类似于此题的简单题目，接下来的几个题也是对细致的考察，这里就略过了，感兴趣的话可以到我的github上查看代码。接下来的题目较为有挑战性，我们一起来试试吧。

# 下题预告

> 题目：正则表达式匹配

> 描述：给定一个字符串 (s) 和一个字符模式 (p)。实现支持 '.' 和 '*' 的正则表达式匹配。

* '.' 匹配任意单个字符。
* '*' 匹配零个或多个前面的元素。

> 匹配应该覆盖整个字符串 (s) ，而不是部分字符串。

> 说明:

* s 可能为空，且只包含从 a-z 的小写字母。
* p 可能为空，且只包含从 a-z 的小写字母，以及字符 . 和 *。

> 示例 1:

* 输入: s = "aa", p = "a"
* 输出: false
* 解释: "a" 无法匹配 "aa" 整个字符串。

> 示例 2:

* 输入: s = "aa", p = "a*"
* 输出: true
* 解释: '*' 代表可匹配零个或多个前面的元素, 即可以匹配 'a' 。因此, 重复 'a' 一次, 字符串可变为 "aa"。

> 示例 3:

* 输入: s = "ab", p = ".*"
* 输出: true
* 解释: ".*" 表示可匹配零个或多个('*')任意字符('.')。

> 示例 4:

* 输入: s = "aab", p = "c*a*b"
* 输出: true
* 解释: 'c' 可以不被重复, 'a' 可以被重复一次。因此可以匹配字符串 "aab"。

> 示例 5:

* 输入: s = "mississippi", p = "mis*is*p*."
* 输出: false

**相关源码请在code目录查看。**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。