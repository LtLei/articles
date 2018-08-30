`HashMap`可能是我们使用最多的键值对型的集合类了，它的底层基于哈希表，采用数组存储数据，使用链表来解决哈希碰撞。在JDK1.8中还引入了红黑树来解决链表长度过长导致的查询速度下降问题。以下是文档对它的介绍中我们重点关注的部分：

> Hash table based implementation of the **Map** interface.  This implementation provides all of the optional map operations, and permits ***null*** values and the ***null*** key.  (The **HashMap** class is roughly equivalent to **Hashtable**, except that it is unsynchronized and permits nulls.)  This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time.

> An instance of **HashMap** has two parameters that affect its performance: ***initial capacity*** and ***load factor***.  The ***capacity*** is the number of buckets in the hash table, and the initial capacity is simply the capacity at the time the hash table is created.  The ***load factor*** is a measure of how full the hash table is allowed to get before its capacity is automatically increased.

> As a general rule, the default load factor (.75) offers a good tradeoff between time and space costs.

> Because TreeNodes are about twice the size of regular nodes, we use them only when bins contain enough nodes to warrant use. And when they become too small (due to removal or resizing) they are converted back to plain bins.  In usages with well-distributed user hashCodes, tree bins are rarely used.

`HashMap`的结构如下所示：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_1.png"/>

# 构造函数与成员变量

在看构造函数和成员变量前，我们要先看下其数据单元，因为`HashMap`有普通的元素，还有红黑树的元素，所以其数据单元定义有两个：

```
// 普通节点
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
    // ...
}

// 树节点，继承自LinkedHashMap.Entry
// 这是因为LinkedHashMap是HashMap的子类，也需要支持树化
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;
    // ...
}

// LinkedHashMap.Entry的实现
static class Entry<K,V> extends HashMap.Node<K,V> {
    Entry<K,V> before, after;
    Entry(int hash, K key, V value, Node<K,V> next) {
        super(hash, key, value, next);
    }
}
```

`TreeNode`定义了一些相关操作的方法，我们会在使用时进行分析。

## 成员变量

```
// capacity初始值，为16，必须为2的次幂
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

// capacity的最大值，为2^30
static final int MAXIMUM_CAPACITY = 1 << 30;

// load factor，是指当容量被占满0.75时就需要rehash扩容
static final float DEFAULT_LOAD_FACTOR = 0.75f;

// 链表长度到8，就转为红黑树
static final int TREEIFY_THRESHOLD = 8;

// 树大小为6，就转回链表
static final int UNTREEIFY_THRESHOLD = 6;

// 至少容量到64后，才可以转为树
static final int MIN_TREEIFY_CAPACITY = 64;

// 保存所有元素的table表
transient Node<K,V>[] table;

// 通过entrySet变量，提供遍历的功能
transient Set<Map.Entry<K,V>> entrySet;

// 下一次扩容值
int threshold;

// load factor
final float loadFactor;
```

## 构造函数

`HashMap`有多个构造函数，主要支持配置容量capacity和load factor，以及从其他Map集合获取初始化数据。

```
 public HashMap(int initialCapacity, float loadFactor) {
    // ... 参数校验    
    this.loadFactor = loadFactor;
    this.threshold = tableSizeFor(initialCapacity);
}

public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}

public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
}

public HashMap(Map<? extends K, ? extends V> m) {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    putMapEntries(m, false);
}
```

这些构造函数都很简单，`putMapEntries`也是依次插入元素的，我们后续分析`put`方法时就能理解其操作了，这里我们还要看下`tableSizeFor`这个方法：

```
static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```

如果你是跟随我文章的顺序读到这里，有没有感觉十分熟悉？这就是找到距离`cap`参数最近的2的次幂呀。没有读过也没有关系，这里奉上链接，里面有非常详细的解析。

[Java集合源码分析之Queue（三）：ArrayDeque](https://www.jianshu.com/p/1c1c3f24762e)

# 重要方法

无论是`List`还是`Map`，最重要的操作都是增删改查部分，我们还从增加一个元素开始分析。

## 增加一个元素

```
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
```
这里我们先关注下`hash`函数，在HashMap中其实现如下：

```
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
这里用到的方法很简单，就是把key与其高16位异或。文档中有如下说明：
> There is a tradeoff between speed, utility, and quality of bit-spreading.

因为没有完美的哈希算法可以彻底避免碰撞，所以只能尽可能减少碰撞，在各方面权衡之后得到一个折中方案，这里我们就不再追究了。

`put`方法的具体实现在`putVal`中，我们看下其实现：

```
// 参数onlyIfAbsent表示是否替换原值
// 参数evict我们可以忽略它，它主要用来区别通过put添加还是创建时初始化数据的
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 空表，需要初始化
    if ((tab = table) == null || (n = tab.length) == 0)
        // resize()不仅用来调整大小，还用来进行初始化配置
        n = (tab = resize()).length;
    // (n - 1) & hash这种方式也熟悉了吧？都在分析ArrayDeque中有体现
    //这里就是看下在hash位置有没有元素，实际位置是hash % (length-1)
    if ((p = tab[i = (n - 1) & hash]) == null)
        // 将元素直接插进去
        tab[i] = newNode(hash, key, value, null);
    else {
        //这时就需要链表或红黑树了
        // e是用来查看是不是待插入的元素已经有了，有就替换
        Node<K,V> e; K k;
        // p是存储在当前位置的元素
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p; //要插入的元素就是p，这说明目的是修改值
        // p是一个树节点
        else if (p instanceof TreeNode)
            // 把节点添加到树中
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            // 这时候就是链表结构了，要把待插入元素挂在链尾
            for (int binCount = 0; ; ++binCount) {
                //向后循环
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 链表比较长，需要树化，
                    // 由于初始即为p.next，所以当插入第9个元素才会树化
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                // 找到了对应元素，就可以停止了
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                // 继续向后
                p = e;
            }
        }
        // e就是被替换出来的元素，这时候就是修改元素值
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            // 默认为空实现，允许我们修改完成后做一些操作
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    // size太大，达到了capacity的0.75，需要扩容
    if (++size > threshold)
        resize();
    // 默认也是空实现，允许我们插入完成后做一些操作
    afterNodeInsertion(evict);
    return null;
}
```

以上方法和我们开头看到的文档描述一致，在插入时可能会从链表变成红黑树。里面用到了`TreeNode.putTreeVal`方法向红黑树中插入元素，关于`TreeNode`的方法我们最后分析。除此之外，还有一个树化的方法是`treeifyBin`，我们现在看下其原理：

```
final void treeifyBin(Node<K,V>[] tab, int hash) {
    int n, index; Node<K,V> e;
    //如果表是空表，或者长度还不到树化的最小值，就需要重新调整表了
    // 这样做是为了防止最初就进行树化
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
        resize();
    else if ((e = tab[index = (n - 1) & hash]) != null) {
        TreeNode<K,V> hd = null, tl = null;
        // while循环的目的是把链表的每个节点转为TreeNode
        do {
            // 根据当前元素，生成一个对应的TreeNode节点
            TreeNode<K,V> p = replacementTreeNode(e, null);
            //挂在红黑树的尾部，顺序和链表一致
            if (tl == null)
                hd = p;
            else {
                p.prev = tl;
                tl.next = p;
            }
            tl = p;
        } while ((e = e.next) != null);
        if ((tab[index] = hd) != null)
            // 这里也用到了TreeNode的方法，我们在最后一起分析
            // 通过头节点调节TreeNode
            // 链表数据的顺序是不符合红黑树的，所以需要调整
            hd.treeify(tab);
    }
}
```
无论是在`put`还是`treeify`时，都依赖于`resize`，它的重要性不言而喻。它不仅可以调整大小，还能调整树化和反树化（从树变为链表）所带来的影响。我们看看它具体做了哪些工作：

```
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        // 大小超过了2^30
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        // 扩容，扩充为原来的2倍
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    // 原来的threshold设置了
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    else {               // zero initial threshold signifies using defaults
        // 全部设为默认值
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
     // 扩容完成，现在需要进行数据拷贝，从原表复制到新表
    @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                if (e.next == null)
                    // 这是只有一个值的情况
                    newTab[e.hash & (newCap - 1)] = e;
                else if (e instanceof TreeNode)
                    // 重新规划树，如果树的size很小，默认为6，就退化为链表
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                else { // preserve order
                    // 处理链表的数据
                    // loXXX指的是在原表中出现的位置
                    Node<K,V> loHead = null, loTail = null;
                    // hiXXX指的是在原表中不包含的位置
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    do {
                        next = e.next;
                        //这里把hash值与oldCap按位与。
                        //oldCap是2的次幂，所以除了最高位为1以外其他位都是0
                        // 和它按位与的结果为0，说明hash比它小，原表有这个位置
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    // 挂在原表相应位置
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    // 挂在后边
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

## 删除一个元素

```
public V remove(Object key) {
    Node<K,V> e;
    return (e = removeNode(hash(key), key, null, false, true)) == null ?
        null : e.value;
}
```
和插入一样，其实际的操作在`removeNode`方法中完成，我们看下其实现：

```
// matchValue是说只有value值相等时候才可以删除，我们是按照key删除的，所以可以忽略它。
// movable是指是否允许移动其他元素，这里是和TreeNode相关的
final Node<K,V> removeNode(int hash, Object key, Object value,
                           boolean matchValue, boolean movable) {
    Node<K,V>[] tab; Node<K,V> p; int n, index;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (p = tab[index = (n - 1) & hash]) != null) {
        Node<K,V> node = null, e; K k; V v;
        // 不同情况下获取待删除的node节点
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            node = p;
        else if ((e = p.next) != null) {
            if (p instanceof TreeNode)
                node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
            else {
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key ||
                            (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        if (node != null && (!matchValue || (v = node.value) == value ||
                                (value != null && value.equals(v)))) {
            if (node instanceof TreeNode)
                // TreeNode删除
                ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
            else if (node == p)
                // 在队首，直接删除
                tab[index] = node.next;
            else
                // 链表中删除
                p.next = node.next;
            ++modCount;
            --size;
            // 默认空实现，允许我们删除节点后做些处理
            afterNodeRemoval(node);
            return node;
        }
    }
    return null;
}
```

## 获取一个元素

除了增删之外，重要的就是查询操作了。查询的`get`方法也是通过调用`getNode`方法完成的，我们看下其实现：

```
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

这里逻辑和我们分析的增删很类似，再读起来就很简单了。

# TreeNode方法介绍

在前面分析增删时，可以发现与红黑树相关的操作都是通过`TreeNode`来实现的，下面我们就来看看`TreeNode`的具体实现：

`TreeNode`算上其继承来的成员变量，共有11个：

```
final int hash;
final K key;
V value;
Node<K,V> next;
Entry<K,V> before, after;
TreeNode<K,V> parent;  // red-black tree links
TreeNode<K,V> left;
TreeNode<K,V> right;
TreeNode<K,V> prev;    // needed to unlink next upon deletion
boolean red;
```
这么多的变量，说明其功能十分强大。这主要是因为它需要在树和链表之间来回转换。下面按照本文中出现的方法顺序对其函数进行分析。

首先是在添加元素时使用到了`TreeNode.putTreeVal`：

```
final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                int h, K k, V v) {
    Class<?> kc = null;
    boolean searched = false;
    // 获取到root节点
    TreeNode<K,V> root = (parent != null) ? root() : this;
    for (TreeNode<K,V> p = root;;) {
        // dir表示查询方向
        int dir, ph; K pk;
        // 要插入的位置在树的左侧
        // 树化会依据key的hash值
        if ((ph = p.hash) > h)
            dir = -1;
        // 要插入的位置在树的右侧
        else if (ph < h)
            dir = 1;
        else if ((pk = p.key) == k || (k != null && k.equals(pk)))
            return p; //找到了，替换即可

        // comparableClassFor是如果key实现了Comparable就返回具体类型，否则返回null
        // compareComparables是比较传入的key和当前遍历元素的key
        // 只有当前hash值与传入的hash值一致才会走到这里
        else if ((kc == null &&
                    (kc = comparableClassFor(k)) == null) ||
                    (dir = compareComparables(kc, k, pk)) == 0) {
            if (!searched) {
                TreeNode<K,V> q, ch;
                //左右都查过了
                searched = true;
                // 通过hash和Comparable都找不到，只能从根节点开始遍历
                if (((ch = p.left) != null &&
                        (q = ch.find(h, k, kc)) != null) ||
                    ((ch = p.right) != null &&
                        (q = ch.find(h, k, kc)) != null))
                    return q;
            }
            // 元素的hashCode一致，且没有实现Comparable，在树里也没有
            // tieBreakOrder则是调用System.identityHashCode(Object o)来进行比较，
            //它的意思是说不管有没有覆写hashCode，都强制使用Object类的hashCode
            // 这样做，是为了保持一致性的插入
            dir = tieBreakOrder(k, pk);
        }
        
         // 代码执行到这，说明没有找到元素，也就是需要新建并插入了
        TreeNode<K,V> xp = p;
        // 经历过上述for循环，p已经到某个叶节点了
        if ((p = (dir <= 0) ? p.left : p.right) == null) {
            Node<K,V> xpn = xp.next;
            TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
            if (dir <= 0)
                xp.left = x;
            else
                xp.right = x;
            xp.next = x;
            x.parent = x.prev = xp;
            if (xpn != null)
                ((TreeNode<K,V>)xpn).prev = x;
            
            // moveRootToFront目的很明确也是必须的。
            // 因为这个红黑树需要挂在数组的某个位置，所以其首个元素必须是root
            // balanceInsertion是因为插入元素后可能不符合红黑树定义了
            // 这部分知识在分析TreeMap中有详细介绍
            // 需要了解的话可以查看文末链接
            moveRootToFront(tab, balanceInsertion(root, x));
            return null;
        }
    }
}
```

除了`putTreeVal`之外，我们还调用过`treeify`以及`removeTreeNode`等方法。这些方法的过程都和`putTreeVal`类似，大家感兴趣可以自己去分析，这里就不再介绍了。

# 增删图示

上面这些增删的代码都很抽象，即使加了大量的注释，也很难以理解，这里做一个简单的示意图，方便我们理解为何要这么做。这里需要一些红黑树调整的知识，大家可以参考文末关于`TreeMap`的文章链接。

删除和增加类似，我们以增加为例：

起初，我们有一张table表，其中插入了一些数据。由于`HashMap`优秀的设计，想要构造出一个需要红黑树的表很难。我们假设插入的数据的key在table表相同位置的hash值都一致，且实现了`Comparable`接口。`Comparable`按照key的自然顺序比较，图中的数字都表示key值。这里数据都是不准确的甚至可能会重复，我们只要理解目的即可。

图中左侧是hash算法完成后的hash值，中间是插入的内容，有的位置还没有数据，有的位置已经插入了一些数据并变为了链表，并且我们假设capacity已经大于64（64是可以树化的阈值）。如下图所示：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_2.png"/>

为了完整的演示，现在我们向表中插入一个hash=6的值。由于6的位置现在是空的，所以元素会直接放在此处：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_3.png"/>

我们继续插入一个hash=6的值，此时，6的位置已经存在一个元素，所以新的元素会通过链表的方式链接在18的后边，如下所示：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_4.png"/>

现在，我们再插入几个hash=6的值，直到达到链表变为红黑树的阈值（默认是8个）：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_5.png"/>

此时，在6的位置上有了8个元素。这时，我们要向其中加入一个9，就需要进行树化，用红黑树代替链表以提升查询性能。

树化时，先获取第一个元素18，将其转为`TreeNode`节点，并设置为head。然后把后续节点依次转为`TreeNode`，并依次挂在head之后，他们的**prev**指向前一个元素，**next**指向后一个元素。挂完之后类似下图：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_6.png"/>

转为树节点之后，需要通过head，也就是这里的18，来进一步调整。首先，18就是root节点，颜色设置为黑色。然后比较18与20，它们的hash都一样，所以会采用`Comparable`比较。这时20应该在18的右边。然后按照`balanceInsertion`方法此时不需要调整，所以18依然是root，且依然在table表的首位，结果如下：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_7.png"/>

然后再调整31，31在18的右侧，结果如下：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_8.png"/>

这时候就破坏了红黑树了，按照在`TreeMap`中介绍的方法，需要进行调整，这里不再展示过程，而直接展示结果了：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_9.png"/>

如果仅是一棵红黑树，到此调整就完毕了，但是这棵红黑树需要在table表中，所以其根节点必须在首位。我们看到，加入31以后，根节点由18变为了20，所以就需要按照`moveRootToFront`方法将root节点提前。这一操作并不会改变树的结构，仅仅是把新的root和原来的root在table表中的位置交换了一下，如下所示：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_10.png"/>

然后按照这样的规则继续调整剩下的元素，这些步骤和上述类似，最终调整结果如下：

<img src="https://github.com/LtLei/articles/blob/master/java/collection/image/img_11_11.png"/>

# 总结

`HashMap`是目前分析的最复杂的集合类了。合理的使用它能够在增删改查等方面都有很好的表现。在使用时我们需要注意以下几点：

1. 设计的key对象一定要实现`hashCode`方法，并尽可能保证均匀少重复。

2. 由于树化过程会依次通过hash值、比较值和对象的hash值进行排序，所以key还可以实现`Comparable`，以方便树化时进行比较。

3. 如果可以预先估计数量级，可以指定initial capacity，以减少rehash的过程。

4. 虽然HashMap引入了红黑树，但它的使用是很少的，如果大量出现红黑树，说明数据本身设计的不合理，我们应该从数据源寻找优化方案。

# 相关文章



---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<img src ="https://github.com/LtLei/articles/blob/master/qrcode.jpg" />

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。