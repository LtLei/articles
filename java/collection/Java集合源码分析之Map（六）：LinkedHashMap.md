`LinkedHashMap`是`HashMap`的子类，所以也具备`HashMap`的诸多特性。不同的是，`LinkedHashMap`还维护了一个双向链表，以保证通过`Iterator`遍历时顺序与插入顺序一致。除此之外，它还支持**Access Order**，即按照元素被访问的顺序来排序，我们熟知的`LRUCache`底层就依赖于此。以下是文档中需要我们注意的点：

> Hash table and linked list implementation of the **Map** interface, with predictable iteration order.  This implementation differs from ***HashMap*** in that it maintains a doubly-linked list running through all of its entries.  This linked list defines the iteration ordering, which is normally the order in which keys were inserted into the map (**insertion-order**).  Note that insertion order is not affected if a key is **re-inserted** into the map.

> A special ***LinkedHashMap(int,float,boolean)*** **constructor** is provided to create a linked hash map whose order of iteration is the order in which its entries were last accessed, from least-recently accessed to most-recently (**access-order**).  This kind of map is well-suited to building LRU caches.

> The ***removeEldestEntry(Map.Entry)*** method may be overridden to impose a policy for removing stale mappings automatically when new mappings are added to the map.

> Note, however, that the penalty for choosing an excessively high value for initial capacity is less severe for this class than for **HashMap**, as iteration times for this class are unaffected by capacity.

下面我们就从构造函数和成员变量开始分析其具体实现。

# 构造函数与成员变量

## 成员变量

在分析成员变量前，我们先看下其存储元素的结构。

```
static class Entry<K,V> extends HashMap.Node<K,V> {
    Entry<K,V> before, after;
    Entry(int hash, K key, V value, Node<K,V> next) {
        super(hash, key, value, next);
    }
}
```

这个`Entry`在`HashMap`中被引用过，主要是为了能让`LinkedHashMap`也支持树化。在这里则是用来存储元素。

```
// 双向链表的头，用作AccessOrder时也是最老的元素
transient LinkedHashMap.Entry<K,V> head;

// 双向链表的尾，用作AccessOrder时也是最新的元素
transient LinkedHashMap.Entry<K,V> tail;

// true则为访问顺序，false则为插入顺序
final boolean accessOrder;
```

## 构造函数

关于`LinkedHashMap`的构造函数我们只关注一个，其他的都和`HashMap`类似，只是把`accessOrder`设置为了false。在上边的文档说过，initialCapacity并没有在`HashMap`中那般重要，因为链表不需要像数组那样必须先声明足够的空间。下面这个构造函数是支持访问顺序的。

```
public LinkedHashMap(int initialCapacity,
                    float loadFactor,
                    boolean accessOrder) {
    super(initialCapacity, loadFactor);
    this.accessOrder = accessOrder;
}
```

## 重要方法

`LinkedHashMap`并没有再实现一整套增删改查的方法，而是通过复写`HashMap`在此过程中定义的几个方法来实现的。对此不熟悉的可以查看文末关于`HashMap`分析的文章，或者对照`HashMap`的源码来看。

## 插入一个元素

`HashMap`在插入时，调用了`newNode`来新建一个节点，或者是通过`replacementNode`来替换值。在树化时也有两个对应的方法，分别是`newTreeNode`和`replacementTreeNode`。完成之后，还调用了`afterNodeInsertion`方法，这个方法允许我们在插入完成后做些事情，默认是空实现。

为了方便分析，我们会对比`HashMap`中的实现与`LinkedHashMap`的实现，来摸清它是如何做的。

```
// HashMap中的实现
Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
    return new Node<>(hash, key, value, next);
}

// LinkedHashMap中的实现
Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
    LinkedHashMap.Entry<K,V> p =
        new LinkedHashMap.Entry<K,V>(hash, key, value, e);
    linkNodeLast(p);
    return p;
}

// HashMap中的实现
Node<K, V> replacementNode(Node<K, V> p, Node<K, V> next) {
    return new Node<>(p.hash, p.key, p.value, next);
}

// LinkedHashMap中的实现
Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
    LinkedHashMap.Entry<K,V> q = (LinkedHashMap.Entry<K,V>)p;
    LinkedHashMap.Entry<K,V> t =
        new LinkedHashMap.Entry<K,V>(q.hash, q.key, q.value, next);
    transferLinks(q, t);
    return t;
}

// newTreeNode和replacementTreeNode和此类似
```

通过以上对比，可以发现，`LinkedHashMap`在新增时，调用了`linkNodeLast`，再替换时调用了`transferLinks`。以下是这两个方法的实现。

```
// 就是将元素挂在链尾
private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
    LinkedHashMap.Entry<K,V> last = tail;
    tail = p;
    if (last == null)
        head = p;
    else {
        p.before = last;
        last.after = p;
    }
}

// 用dst替换src
private void transferLinks(LinkedHashMap.Entry<K,V> src,
                            LinkedHashMap.Entry<K,V> dst) {  
    LinkedHashMap.Entry<K,V> b = dst.before = src.before;
    LinkedHashMap.Entry<K,V> a = dst.after = src.after;
    if (b == null)
        head = dst;
    else
        b.after = dst;
    if (a == null)
        tail = dst;
    else
        a.before = dst;
}
```

最后我们看下`afterNodeInsertion`做了哪些事情吧：

```
// evict在HashMap中说过，为false表示是创建阶段
void afterNodeInsertion(boolean evict) { // possibly remove eldest
    LinkedHashMap.Entry<K,V> first;
    // 不是创建阶段
    if (evict && (first = head) != null && removeEldestEntry(first)) {
        K key = first.key;
        // 自动删除最老的元素，也就是head元素
        removeNode(hash(key), key, null, false, true);
    }
}
```

`removeEldestEntry`是当想要在插入元素时自动删除最老的元素时需要复写的方法。其默认实现如下：

```
protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
    return false;
}
```

## 查询

因为要支持访问顺序，所以获取元素的方法和`HashMap`也有所不同。下面我们看下其实现：

```
public V get(Object key) {
    Node<K,V> e;
    if ((e = getNode(hash(key), key)) == null)
        return null;
    if (accessOrder)
        // 数据被访问，需要将其移动到末尾
        afterNodeAccess(e);
    return e.value;
}
```

`getNode`方法是在`HashMap`中实现的，所以这是包装了一下`HashMap`的方法，并添加了一个`afterNodeAccess`，其实现如下：

```
void afterNodeAccess(Node<K,V> e) { // move node to last
    LinkedHashMap.Entry<K,V> last;
    // e元素不在末尾
    if (accessOrder && (last = tail) != e) {
        // p是e，b是前一个元素，a是后一个元素
        LinkedHashMap.Entry<K,V> p =
            (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        // e要放在末尾，所以没有after
        p.after = null;

        // 把e去掉，把b和a接起来
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a != null)
            a.before = b;
        else
            last = b;

        //把e接在末尾
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
        tail = p;
        ++modCount;
    }
}
```

关于`LinkedHashMap`的分析就到这里了，其他关于`Iterator`的内容都和`Collection`是大同小异的，感兴趣的可以去查看相关源码。

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="/image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。