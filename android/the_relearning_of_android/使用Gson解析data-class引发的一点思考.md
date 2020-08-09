# 使用Gson解析data class引发的一点思考

Gson是Android解析Json的老牌子了，它的使用和原理也被大家研究的极其透彻了，可以说这是一个相当成熟的库。但是伴随kotlin的普及，有一个问题也越发明显地暴露了出来。

kotlin里有一个 data class 的概念，倒不是什么“黑科技”的东西，但是确实相当好用，它会自动生成hashcode、equals以及toString等方法，都是对于一个bean来说很重要的方法。但是这么好用的东西在和gson一起使用时就出现了一点意外。让我们看下边的例子：

```kotlin
// 定义
data class TestBean(
    val name: String,
    val age: Int
)

// 数据
val json = """
            {"name":null,"age":null}
        """.trimIndent()

// 解析
val bean = gson.fromJson(json, TestBean::class.java)

// 输出
TestBean(name=null, age=0)
```

把json换成 `{}` 或 `{"name":null}` 或 `{"age":null}`，甚至 `{"age":0}` 都不会影响输出结果。也就是说，当gson解析data class时，kotlin的null-safe失效了。

其实这个问题不是data class造成的，问题主要在null-safe，只是data class和gson打交道最多而已。当然也不能怪gson，谁让gson火起来的时候kotlin还没多少知名度呢。

# 追溯问题产生原因

遇到问题自然要追踪源码了，想必很多人这样试过，最终都会定位到 **ReflectiveTypeAdapterFactory.java** 这个类中。为了节约大家的时间，这里把相关的部分贴出来：

```java
public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
  // ...

  @Override public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
    // ...
    ObjectConstructor<T> constructor = constructorConstructor.get(type);
    return new Adapter<T>(constructor, getBoundFields(gson, type, raw));
  }

  private ReflectiveTypeAdapterFactory.BoundField createBoundField(
      final Gson context, final Field field, final String name,
      final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
    // ...

    return new ReflectiveTypeAdapterFactory.BoundField(name, serialize, deserialize) {
      // ...
      @Override void read(JsonReader reader, Object value)
          throws IOException, IllegalAccessException {
        Object fieldValue = typeAdapter.read(reader);
        if (fieldValue != null || !isPrimitive) {
          field.set(value, fieldValue);
        }
      }
    };
  }

  // ...

  public static final class Adapter<T> extends TypeAdapter<T> {
    // ...

    @Override public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }

      T instance = constructor.construct();

      try {
        in.beginObject();
        while (in.hasNext()) {
          String name = in.nextName();
          BoundField field = boundFields.get(name);
          if (field == null || !field.deserialized) {
            in.skipValue();
          } else {
            field.read(in, instance);
          }
        }
      } 
      // ...
      return instance;
    }

  }
}
```

这里有两处需要我们关注，第一处就是 `T instance = constructor.construct();` 这个 constructor 是一个 ObjectConstructor 对象，在 ConstructorConstructor 类里可以找到它的实现：

```java
public final class ConstructorConstructor {
  // ...

  public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
    final Type type = typeToken.getType();
    final Class<? super T> rawType = typeToken.getRawType();

    // first try an instance creator

    @SuppressWarnings("unchecked") // types must agree
    final InstanceCreator<T> typeCreator = (InstanceCreator<T>) instanceCreators.get(type);
    if (typeCreator != null) {
      return new ObjectConstructor<T>() {
        @Override public T construct() {
          return typeCreator.createInstance(type);
        }
      };
    }

    // ...

    ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
    if (defaultConstructor != null) {
      return defaultConstructor;
    }

    ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
    if (defaultImplementation != null) {
      return defaultImplementation;
    }

    // finally try unsafe
    return newUnsafeAllocator(type, rawType);
  }
```

Gson 实例化对象分为四种情况：
1. 使用我们自定义的 InstanceCreator，可以在初始化时加入它；
2. 使用默认构造器，也就是无参构造函数；
3. 如果是 Collection 或 Map，则返回对应的对象；
4. 使用 UnSafe。

自定义 InstanceCreator 不现实，在这个问题上有多少 data class，就得准备多少 InstanceCreator。Collection 或 Map 也排除了，我们要处理的是对象。也就是说只有方式 2 和 4 可用，我们没有提供默认构造器，所以 Gson 使用了 UnSafe 这种手段。我们这里不追究 UnSafe 是什么，只要确认使用了 UnSafe，就会产生上述结果就好了，不过有一句必须注意，**它不会走我们的构造器**。

第二处需要注意的就是为什么 String 被赋值为 null，但 Int 没有问题？这个玄机就在 createBoundField 方法里，我们再贴一遍：

```java
private ReflectiveTypeAdapterFactory.BoundField createBoundField(
      final Gson context, final Field field, final String name,
      final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
    // ...

    return new ReflectiveTypeAdapterFactory.BoundField(name, serialize, deserialize) {
      // ...
      @Override void read(JsonReader reader, Object value)
          throws IOException, IllegalAccessException {
        Object fieldValue = typeAdapter.read(reader);
        // 如果有值，或者不是 Primitive 类型，就赋值
        if (fieldValue != null || !isPrimitive) {
          field.set(value, fieldValue);
        }
      }
    };
  }
```

`if (fieldValue != null || !isPrimitive)` 在这里起了很大作用，像 int、char、boolean 以及对应的包装类等都属于基本类型，条件不成立所以不会赋值，但字符串和普通对象不是基本类型，于是就发生了一开始我们看到的现象。

# 如何解决

现在是解决问题的时候了，一个自然的想法是避免 UnSafe，只要提供默认构造器即可。让我们试试看：

```kotlin
data class TestBean2(val name : String = "", val age : Int = 0)

@Test
fun deserializeWithDefaultConstructor() {
    val json1 = """
        {}
    """.trimIndent()
    val bean1 = gson.fromJson(json1, TestBean2::class.java)
    println(bean1)

    val json2 = """
        {"name":null}
    """.trimIndent()
    val bean2 = gson.fromJson(json2, TestBean2::class.java)
    println(bean2)

    val json3 = """
        {"age":null}
    """.trimIndent()
    val bean3 = gson.fromJson(json3, TestBean2::class.java)
    println(bean3)

    val json4 = """
        {"age":0}
    """.trimIndent()
    val bean4 = gson.fromJson(json4, TestBean2::class.java)
    println(bean4)
}
```

输出的结果是这样的：

```
TestBean2(name=, age=0)
TestBean2(name=null, age=0)
TestBean2(name=, age=0)
TestBean2(name=, age=0)
```

看起来好了很多，只有 json 返回了 name=null 才会出现问题，这说明我们解决了问题一，但没解决问题二。gson正确地拿到了对象，随后又把 null 赋值给了 name，而且是用反射强行赋值的。如何解决问题二，反而成为了关键。

使用默认构造器是比较常见的解决方式，但当json显式返回null时该问题依然存在，所以还需要进一步处理。

既然使用默认值不管用，那么声明所有字段为可空 `?` 类型就可以很简单地规避这个问题。但还需要考虑另一个问题：fail-fast，也就是快速失败。Json里的数据也许大部分是可空的，但总有几个字段是不可空的，这是由业务本身决定的，例如一个用户的uid明显不能为空。而使用可空参数就可能让一个空的uid“混”进来，在后续操作中引发一连串的错误。当然使用默认参数也有同样的问题。

在可空参数的基础上，提供一个不可空的getter可以有效地避免以上问题，例如对data class做以下处理：

```kotlin
data class TestBean3(
    @SerializedName("name")
    private val _name: String?,
    val age: Int
) {
    val name: String
        get() = _name ?: "" // 返回默认值或者抛出异常
}
```

这是一个行之有效的方案，也是我认为当代码库和gson深度耦合后较好的解决方案，虽不能在解析时就发现问题，但也比使用之后出问题强的多。只不过这样一来比较繁琐，二来每个 bean 都会比原来大一些。

除此之外，square出品的moshi还提供了两种不同的思路，一种是使用kotlin-reflection，kotlin的反射和java不太一致，你需要依赖一个至少2.5M的jar文件，而且反射的性能肯定差一些。另一种方案是在编译时为每个data class生成TypeAdapter。要参考这两种方案，可以查看moshi的源码，地址是：[moshi](https://github.com/square/moshi/tree/master/kotlin)。另外，kotlin官方也提供了自己的解析库，它更考虑了kotlin本身的全部特性，这个库是[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)。JakeWharton也为之增加了对应的retrofit converter：[retrofit2-kotlinx-serialization-converter](https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter)。

也就是说，如果可以摆脱gson，使用moshi或serialization在kotlin编程时可以获得更好的体验。若要使用gson，要么按照上述方式使用两个变量实现非空校验，要么参考moshi的做法自己写一套gson的实现。（其实是重复造轮子了，若无必要不建议这样操作==！）

# 我的思考

gson是谷歌出品的解析库，kotlin又是谷歌力推的开发语言，中间出现这样的不相容问题的确出乎所料，但作为开发者应当总有自己的应对之法。现在的项目大多使用Retrofit进行网络请求，json的转换也是通过ConverterFactory完成的，其他需要手动解析json的地方也可以作简单的封装，而不是随用随创建Gson对象，因此项目本身对gson的依赖并不强烈。如果项目和gson发生了深度耦合，就应该考虑下自己写代码时是不是太随意了一些？

另外一点是将data class全部声明为可空 `?` 类型只能算是一种临时方案，因为问题的根源在gson不兼容kotlin特性，而不是data class出现了问题。解决问题应该从根源出发，而不是破坏其余部分的结构，造成问题范围扩大，也是设计代码时应该遵守的原则之一。

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。