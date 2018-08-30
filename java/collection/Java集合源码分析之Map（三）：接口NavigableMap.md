`SortedMap`提供了获取最大值与最小值的方法，但对于一个已经排序的数据集，除了最大值与最小值之外，我们可以对任何一个元素，找到比它小的值和比它大的值，还可以按照按照原有的顺序倒序排序等。`NavigableMap`就为我们提供了这些功能。

`NavigableMap`主要有以下方法：

```
// 找到第一个比指定的key小的值
Map.Entry<K,V> lowerEntry(K key);

// 找到第一个比指定的key小的key
K lowerKey(K key);

// 找到第一个小于或等于指定key的值
Map.Entry<K,V> floorEntry(K key);

// 找到第一个小于或等于指定key的key
K floorKey(K key);

//  找到第一个大于或等于指定key的值
Map.Entry<K,V> ceilingEntry(K key);

K ceilingKey(K key);

// 找到第一个大于指定key的值
Map.Entry<K,V> higherEntry(K key);

K higherKey(K key);

// 获取最小值
Map.Entry<K,V> firstEntry();

// 获取最大值
Map.Entry<K,V> lastEntry();

// 删除最小的元素
Map.Entry<K,V> pollFirstEntry();

// 删除最大的元素
Map.Entry<K,V> pollLastEntry();

//返回一个倒序的Map
NavigableMap<K,V> descendingMap();

// 返回一个Navigable的key的集合，NavigableSet和NavigableMap类似
NavigableSet<K> navigableKeySet();

// 对上述集合倒序
NavigableSet<K> descendingKeySet();
```

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<img src ="https://github.com/LtLei/articles/blob/master/qrcode.jpg" />

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。