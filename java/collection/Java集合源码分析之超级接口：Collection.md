# Collection

**Collection**是**List**、**Queue**和**Set**的超集，它直接继承于`Iterable`，也就是所有的**Collection**集合类都支持**for-each**循环。除此之外，**Collection**也是面向接口编程的典范，通过它可以在多种实现类间转换，这也是面向对象编程的魅力之一。

## 方法定义

在阅读源码前，我们可以先自行想象一下，如果我们想封装下数组或链表以方便操作，我们需要封装哪些功能呢？比如：统计大小、插入或删除数据、清空、是否包含某条数据，等等。而Collection就是对这些常用操作进行提取，只是其很全面、很通用。下面我们看看它都提供了哪些方法。

```
//返回集合的长度，如果长度大于Integer.MAX_VALUE，返回Integer.MAX_VALUE
int size();

//如果集合元素总数为0，返回true
boolean isEmpty();

//判断集合中是否包含指定的元素，其依据是equals()方法
boolean contains(Object o);

//返回一个包含集合中所有元素的数组
Object[] toArray();

//与上个类似，只是增加了类型的转换
<T> T[] toArray(T[] a);

//向集合中加入一个元素，如果成功加入则返回true，如果加入失败，或者因集合本身已经包含同个元素而不再加入时，返回false
boolean add(E e);

//从集合中删除指定元素的单个实例
boolean remove(Object o);

//如果集合包含指定集合中的所有元素，返回true
boolean containsAll(Collection<?> c);

//把指定集合中的所有元素添加到集合中，但在此期间，如果指定的集合发生了改变，可能出现意想不到的事情
boolean addAll(Collection<? extends E> c);

//从集合中删除所有包含在指定集合中的元素
boolean removeAll(Collection<?> c);

//仅保留集合中包含在指定集合中的元素
boolean retainAll(Collection<?> c);

//清空集合
void clear();

//将此方法抽象，是保证所有子类都覆写此方法，以保证equals的正确行为
boolean equals(Object o);

//同上
int hashCode();

//这个方法在JDK1.8中提供了默认的实现，会使用Iterator的形式删除符合条件的元素
default boolean removeIf(Predicate<? super E> filter){
    Objects.requireNonNull(filter);
    boolean removed = false;
    final Iterator<E> each = iterator();
    while (each.hasNext()) {
        if (filter.test(each.next())) {
            each.remove();
            removed = true;
        }
    }
    return removed;
}
```

# 超级实现类：AbstractCollection

在`Collection`中定义的许多方法，根据现有的定义以及继承的`Iterable`，都可以在抽象类中实现，这样可以减少实现类需要实现的方法，这个抽象类就是`AbstractCollection`。

首先我们关注下其文档，里面有两句说明可能会影响我们的继承：

> To implement an unmodifiable collection, the programmer needs only to extend this class and provide implementations for the **iterator** and **size** methods.  (The iterator returned by the **iterator** method must implement **hasNext** and **next**.)

> To implement a modifiable collection, the programmer must additionally override this class's **add** method (which otherwise throws an **UnsupportedOperationException**), and the iterator returned by the **iterator** method must additionally implement its **remove** method.

大体意思是说，如果要实现一个不可修改的集合，只需要重写`iterator`和`size`接口就可以，并且返回的`Iterator`需要实现`hasNext`和`next`。而要实现一个可以修改的集合，还必须重写`add`方法（默认会抛出异常），返回的`Iterator`还需要实现`remove`方法。

## 方法定义

```
//这个毫无疑问，是可以直接获取的
public boolean isEmpty() {
    return size() == 0;
}

//这个方法因为Iterator的存在，可以进行一致性封装，这里需要注意的是对象的比较是通过equals方法，因为调用到了it.next()与it.hasNext()，这也是为什么文档注释会写实现集合类需要重写Iterator的这两个方法。
public boolean contains(Object o) {
    Iterator<E> it = iterator();
    if (o==null) {
        while (it.hasNext())
            if (it.next()==null)
                return true;
    } else {
        while (it.hasNext())
            if (o.equals(it.next()))
                return true;
    }
    return false;
}

//和contains类似，也是通过Iterator实现的，但其会调用it.remove()方法，这也是为什么文档注释会写实现可以修改的集合类时需要重写Iterator的remove方法。
public boolean remove(Object o) {
    //...省略，这里调用了it.remove()方法
}

```

类似的方法还有`containsAll(Collection<?> c)`、`addAll(Collection<? extends E> c)`、`removeAll(Collection<?> c)`、`retainAll(Collection<?> c)`和`clear()`等，都需要利用到Iterator的特性，这里就不再一一赘述了。

另外还有一个toArray()的方法实现略微不同，可以看看其具体实现。

```
//这个实现相对复杂一些，可以看到扩容最主要的手段是Arrays.copyOf()方法，
//也就是需要将原数组通过复制到新的数组中来实现的。
//注意这里返回的顺序和Iterator顺序一致
//在这里实现是为了方便不同具体实现类互相转换，我们在后续会多次见到此方法
public Object[] toArray() {
    //先根据当前集合大小声明一个数组
    Object[] r = new Object[size()];
    Iterator<E> it = iterator();
    for (int i = 0; i < r.length; i++) {
        //集合元素没那么多，说明不需要那么大的数组
        if (! it.hasNext()) 
            return Arrays.copyOf(r, i); //仅返回赋完值的部分
        r[i] = it.next();
    }
    //元素比从size()中获取的更多，就需要进一步调整数组大小
    return it.hasNext() ? finishToArray(r, it) : r;
}

private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
    //记录当前大小
    int i = r.length;
    while (it.hasNext()) {
        int cap = r.length;
        //r的长度不够，继续分配
        if (i == cap) {
            //扩充方式为cap+cap/2+1，也就是1.5倍扩容
            int newCap = cap + (cap >> 1) + 1;
            // 超过了最大容量，MAX_ARRAY_SIZE=Integer.MAX_VALUE-8
            if (newCap - MAX_ARRAY_SIZE > 0)
                //重新设置cap的值
                newCap = hugeCapacity(cap + 1);
            
            //对r进行扩容
            r = Arrays.copyOf(r, newCap);
        }
        //赋值，进入下一轮循环
        r[i++] = (T)it.next();
    }
    // 由于之前扩容是1.5倍进行的，最后再将其设置到和r实际需要的相同
    return (i == r.length) ? r : Arrays.copyOf(r, i);
}

private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // 超过了最大正整数，也就是负数
        throw new OutOfMemoryError
            ("Required array size too large");
    return (minCapacity > MAX_ARRAY_SIZE) ?
        Integer.MAX_VALUE :
        MAX_ARRAY_SIZE;
}

//和toArray()方法类似，就不再赘述，具体可以查看源码
public <T> T[] toArray(T[] a) {
    //...
}
```

除了以上这些方法，`AbstractCollection`还实现了`toString`方法，其是通过`StringBuilder`拼接了每个元素的`toString`完成的，也并不复杂。这里可以看下其源码：
```
public String toString() {
    Iterator<E> it = iterator();
    if (! it.hasNext())
        return "[]";

    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (;;) {
        E e = it.next();
        sb.append(e == this ? "(this Collection)" : e);
        if (! it.hasNext())
            return sb.append(']').toString();
        sb.append(',').append(' ');
    }
}
```

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<img src ="https://github.com/LtLei/articles/blob/master/qrcode.jpg" />

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。