在日常生活中，排队几乎随处可见，上地铁要排队，买火车票要排队，就连出门吃个大餐，也要排队。。。之前研究的`ArrayList`就像是一个缺乏管理的排队系统。大家虽然会排队，但一会走个人，大家向前挪一挪，一会插个人，大家向后挪一挪，碰到这样的队伍，一定让人痛苦万分吧？

今天要介绍的`Queue`就不同了，它是一个严格的排队系统。就像许多火车站排队窗口在两侧加了护栏一样，大家只能从队尾进来，从队首离开，我们称之为`FIFO(first in first out)`，也就是先进来的人先离开。`Queue`就严格遵循了这个原则，使插队和提早离开变得不可能。

当然`Queue`也有很多变种，`FIFO`并不是其可以遵循的唯一规则。比如`Stack`（栈），就遵循`LIFO(last in first out)`，这就好比我们叠碗一样，后来者居上。还有我们之后要分析的`Deque`，其允许元素从两端插入或删除，比如排队进站时总有人说，“我能不能插个队，我赶时间？”。

# 超级接口Queue

队列在软件开发中担任着重要的职责，java函数的调用用到了栈的技术，在处理并发问题时，`BlockingQueue`很好的解决了数据传输的问题。接下来我们看看Java是如何定义队列的吧。

首先，`Queue`也继承自`Collection`，说明它是集合家族的一员。`Queue`接口主要提供了以下方法：

```
//将元素插入队列
boolean add(E e);

//将元素插入队列，与add相比，在容量受限时应该使用这个
boolean offer(E e);

//将队首的元素删除，队列为空则抛出异常
E remove();

//将队首的元素删除，队列为空则返回null
E poll();

//获取队首元素，但不移除，队列为空则抛出异常
E element();

//获取队首元素，但不移除，队列为空则返回null
E peek();
```

# 超级实现类AbstractQueue

`Queue`的定义很简单，所以其实现类也很简单，用简单的代码做复杂的事情，值得我们学习。

`AbstractQueue`仅实现了`add`、`remove`和`element`三个方法，并且分别调用了另外一个仅细微区别的方法，我们这里只看其一

```
//这里我们就明白，对于有容量限制的，直接调用offer肯定会更快
public boolean add(E e) {
    if (offer(e))
        return true;
    else
        throw new IllegalStateException("Queue full");
}
```

此外，它还实现了`clear`与`addAll`方法，重写这些方法可以使其更符合当前场景。

```
public void clear() {
    while (poll() != null)
        ;
}

public boolean addAll(Collection<? extends E> c) {
    if (c == null)
        throw new NullPointerException();
    if (c == this)
        throw new IllegalArgumentException();
    boolean modified = false;
    for (E e : c)
        if (add(e))
            modified = true;
    return modified;
}
```

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。