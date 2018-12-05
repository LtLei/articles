`List`是`Collection`三大直接子接口之一，其中的数据可以通过位置检索，用户可以在指定位置插入数据。`List`的数据可以为空，可以重复。以下是其文档注释，只看前两段：
> An ordered collection (also known as a ***sequence***).  The user of this interface has precise control over where in the list each element is inserted.  The user can access elements by their integer index (position in the list), and search for elements in the list.

> Unlike sets, lists typically allow duplicate elements.  More formally, lists typically allow pairs of elements <tt>e1</tt> and **e2** such that **e1.equals(e2)**, and they typically allow multiple null elements if they allow null elements at all.  It is not inconceivable that someone might wish to implement a list that prohibits duplicates, by throwing runtime exceptions when the user attempts to insert them, but we expect this usage to be rare.

# List特有方法

我们关注其不同于Collection的方法，主要有以下这些：

```
//在指定位置，将指定的集合插入到当前的集合中
boolean addAll(int index, Collection<? extends E> c);

//这是一个默认实现的方法，会通过Iterator的方式对每个元素进行指定的操作
default void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    final ListIterator<E> li = this.listIterator();
    while (li.hasNext()) {
        li.set(operator.apply(li.next()));
    }
}

//排序，依据指定的规则对当前集合进行排序，可以看到，排序是通过Arrays这个工具类完成的。
default void sort(Comparator<? super E> c) {
    Object[] a = this.toArray();
    Arrays.sort(a, (Comparator) c);
    ListIterator<E> i = this.listIterator();
    for (Object e : a) {
        i.next();
        i.set((E) e);
    }
}

//获取指定位置的元素
E get(int index);

//修改指定位置元素的值
E set(int index, E element);

//将指定元素添加到指定的位置
void add(int index, E element);

//将指定位置的元素移除
E remove(int index);

//返回一个元素在集合中首次出现的位置
int indexOf(Object o);

//返回一个元素在集合中最后一次出现的位置
int lastIndexOf(Object o);

//ListIterator继承于Iterator，主要增加了向前遍历的功能
ListIterator<E> listIterator();

//从指定位置开始，返回一个ListIterator
ListIterator<E> listIterator(int index);

//返回一个子集合[fromIndex, toIndex)，非结构性的修改返回值会反映到原表，反之亦然。
//如果原表进行了结构修改，则返回的子列表可能发生不可预料的事情
List<E> subList(int fromIndex, int toIndex);
```

通过以上对接口的分析可以发现，`Collection`主要提供一些通用的方法，而`List`则针对线性表的结构，提供了对位置以及子表的操作。

# 超级实现类：AbstractList

有了分析`AbstractCollection`的经验，我们分析`AbstractList`就更容易了。首先也看下其文档中强调的部分：

> To implement an unmodifiable list, the programmer needs only to extend this class and provide implementations for the ***get(int)*** and ***size()*** methods.
 
>To implement a modifiable list, the programmer must additionally override the ***set(int, E)*** method (which otherwise throws an **UnsupportedOperationException**).  If the list is variable-size the programmer must additionally override the ***add(int, E)*** and ***remove(int)*** methods.

大致意思是说，要实现一个不可修改的集合，只需要复写`get`和`size`就可以了。要实现一个可以修改的集合，还需要复写`set`方法，如果要动态调整大小，就必须再实现`add`和`remove`方法。

然后看下其源码实现了哪些功能吧：

```
//在AbstractCollection中，add方法默认会抛出异常，
//而在这里是调用了add(int index, E e)方法，但这个方法也是没有实现的。
//这里默认会把元素添加到末尾。
public boolean add(E e) {
    add(size(), e);
    return true;
}

//同上，这个只需要进行一次遍历即可
public boolean addAll(int index, Collection<? extends E> c) {
    //...   
}
```

接下来，还有几个方法和`Iterator`与`ListIterator`息息相关，在`AbstractList`中有具体的实现，我们先看看它是如何把集合转变成`Iterator`对象并支持`foreach`循环的吧。

我们追踪源码发现，在`iterator()`方法中直接返回了一个`Itr`对象

```
public Iterator<E> iterator() {
    return new Itr();
}
```

这样我们就明白了，它是实现了一个内部类，这个内部类实现了`Iterator`接口，合理的处理`hasNext`、`next`、`remove`方法。这个源码就不粘贴啦，其中仅仅在`remove`时考虑了一下多线程问题，有兴趣的可以自己去看看。

另外一个就是`ListIterator`，

```
public ListIterator<E> listIterator() {
    return listIterator(0);
}
```

可以看到，`listIterator`方法依赖于`listIterator(int index)`方法。有了上边的经验，我们可以推测，它也是通过一个内部类完成的。

```
public ListIterator<E> listIterator(final int index) {
    rangeCheckForAdd(index);

    return new ListItr(index);
}
```

事实证明，和我们想的一样，`AbstractList`内部还定义了一个`ListItr`，实现了`ListIterator`接口，其实现也很简单，就不粘贴源码啦。

接下来我们看看，利用这两个实现类，`AbstractList`都做了哪些事情。

```
//寻找一个元素首次出现的位置，只需要从前往后遍历，找到那个元素并返回其位置即可。
public int indexOf(Object o) {
    ListIterator<E> it = listIterator();
    if (o==null) {
        while (it.hasNext())
            if (it.next()==null)
                return it.previousIndex();
    } else {
        while (it.hasNext())
            if (o.equals(it.next()))
                return it.previousIndex();
    }
    return -1;
}

//同理，寻找一个元素最后一次出现的位置，只需要从列表最后一位向前遍历即可。
//看到listIterator(int index)方法是可以传递参数的，这个我想我们都可以照着写出来了。
public int lastIndexOf(Object o) {
    //...
}

//这个方法是把从fromIndex到toIndex之间的元素从集合中删除。
//clear()方法也是调用这个实现的（我认为clear实现意义并不大，因为在其上级AbstractCollection中已经有了具体实现）。
protected void removeRange(int fromIndex, int toIndex) {
    ListIterator<E> it = listIterator(fromIndex);
    for (int i=0, n=toIndex-fromIndex; i<n; i++) {
        it.next();
        it.remove();
    }
}
```

接下来还有两块内容比较重要，一个是关于`SubList`的，一个是关于`equals`和`hashcode`的。

我们先看看`SubList`相关的内容。`SubList`并不是新建了一个集合，只是持有了当前集合的引用，然后控制一下用户可以操作的范围，所以在接口定义时就说明了其更改会直接反应到原集合中。`SubList`定义在`AbstractList`内部，并且是`AbstractList`的子类。在`AbstractList`的基础上增加了对可选范围的控制。

`equals`和`hashcode`的实现，也关乎我们的使用。在`AbstractList`中，这两个方法不仅与其实例有关，也和其内部包含的元素有关，所以在定义数据元素时，也应该复写这两个方法，以保证程序的正确运行。这里看下其源码加深一下印象吧。

```
public boolean equals(Object o) {
    if (o == this)
        return true;
    if (!(o instanceof List))
        return false;

    ListIterator<E> e1 = listIterator();
    ListIterator<?> e2 = ((List<?>) o).listIterator();
    while (e1.hasNext() && e2.hasNext()) {
        E o1 = e1.next();
        Object o2 = e2.next();
        //这里用到了数据元素的equals方法
        if (!(o1==null ? o2==null : o1.equals(o2)))
            return false;
    }
    return !(e1.hasNext() || e2.hasNext());
}
```

```
public int hashCode() {
    int hashCode = 1;
    for (E e : this)
        //这里用到了数据元素的hashCode方法
        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
    return hashCode;
}
```

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。