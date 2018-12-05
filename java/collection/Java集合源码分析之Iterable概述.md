# 前言

当我们想要遍历集合时，Java为我们提供了多种选择，通常有以下三种写法：

* 写法1：for循环

```java
for (int i = 0, len = strings.size(); i < len; i++) {
    System.out.println(strings.get(i));
}
```

* 写法2：foreach循环

```java
for (String var : strings) {
    System.out.println(var);
}
```

* 写法3：Iterator

```java
Iterator iterator = strings.iterator();
while (iterator.hasNext()) {
    System.out.println(iterator.next());
}
```

那么以上三种遍历方式有何区别呢？for循环我们很熟悉了，就是根据下标来获取元素，这个特性与数组十分吻合，不熟悉的朋友可以阅读前面讲解数组的文章。foreach则主要对类似链表的结构提供遍历支持，链表没有下标，所以使用for循环遍历会大大降低性能。Iterator就是我们今天要讲述的主角，**它实际上就是foreach**。

那么，为什么集合可以进行foreach遍历，而我们自己定义的Java对象却不可以呢？有没有办法让任何对象都支持这种遍历方式？下面的内容会告诉我们答案。

# Iterable

**Iterable**是迭代器的意思，作用是为集合类提供**for-each**循环的支持。由于使用**for**循环需要通过位置获取元素，而这种获取方式仅有数组支持，其他许多数据结构，比如链表，只能通过查询获取数据，这会大大的降低效率。**Iterable**就可以让不同的集合类自己提供遍历的最佳方式。

**Iterable**的文档声明仅有一句：

> Implementing this interface allows an object to be the target of the "for-each loop" statement.

它的作用就是为Java对象提供foreach循环，其主要方法是返回一个`Iterator`对象：

```java
Iterator<T> iterator();
```

也就是说，如果想让一个Java对象支持foreach，只要实现**Iterable**接口，然后就可以像集合那样，通过`Iterator iterator = strings.iterator()`方式，或者使用foreach，进行遍历了。

# Iterator

Iterator是foreach遍历的主体，它的代码实现如下：

```java
// 判断一个对象集合是否还有下一个元素
boolean hasNext();

// 获取下一个元素
E next();

// 删除最后一个元素。默认是不支持的，因为在很多情况下其结果不可预测，比如数据集合在此时被修改
default void remove(){...}

// 主要将每个元素作为参数发给action来执行特定操作
default void forEachRemaining(Consumer<? super E> action){...}
```

`Iterator`还有一个子接口，是为需要双向遍历数据时准备的，在后续分析`ArrayList`和`LinkedList`时都会看到它。它主要增加了以下几个方法：

```java
// 是否有前一个元素
boolean hasPrevious();

// 获取前一个元素
E previous();

// 获取下一个元素的位置
int nextIndex();

// 获取前一个元素的位置
int previousIndex();

// 添加一个元素
void add(E e);

// 替换当前元素值
void set(E e);
```

# 总结

在Java中有许多特性都是通过接口来实现的，foreach循环也是。foreach主要是解决for循环依赖下标的问题，为高效遍历更多的数据结构提供了支持。如果你清楚数组和链表的区别，应该就可以回答以下问题了：

**for与foreach有何区别，哪个更高效？**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="/image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。