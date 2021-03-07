从设计的角度谈Retrofit

---

[OkHttp](https://github.com/square/okhttp) 已经足够好用。

我想对于这句话没有多少需要争论的，OkHttp足以应对开发中遇到的大部分问题。但 [Retrofit](https://github.com/square/retrofit) 是 square 开发的另一个网络库（实际上是用于网络的库），所以让我们思考一下square为什么要重复自己，毕竟人们总说，"Don't repeat yourself!"。

# OkHttp的基本使用

了解一点OkHttp是理解Retrofit的关键。一般情况下，按照以下方式就可以进行网络请求：

```kotlin
// 忽略各种初始化参数 OkHttpClient.Builder().xxx().build()
val okHttpClient = OkHttpClient()
val request = Request.Builder().url("https://example.com/").build()
val call = okHttpClient.newCall(request)

// 同步请求
try {
    val response: okhttp3.Response = call.execute()
} catch (ex: IOException) {
}

// 异步请求
call.enqueue(object : okhttp3.Callback {
    override fun onFailure(call: okhttp3.Call, e: IOException) {
        // ...
    }

    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
        // ...
    }
})
```

很直观的感受就是：简单。OkHttp把复杂的网络请求变成了一个方法，只要处理好入参和返回值，剩下的OkHttp会自己完成。当然，也许需要了解许多配置项。

尽管在实际使用时，我们会简单地进行封装以满足需要，但这与直接使用OkHttp区别不大。至此我们依然认为OkHttp足够好用，Retrofit的立足之本，OkHttp没有告诉我们答案。

# Retrofit做了什么

同样地，先通过一段代码来直观地感受一下Retrofit：

```kotlin
interface SampleService {
    @GET("path/")
    fun request(): retrofit2.Call<okhttp3.ResponseBody>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://example.com/")
    .client(OkHttpClient())
    .build()

val service = retrofit.create(SampleService::class.java)

// 同步请求
try {
    val response: retrofit2.Response<okhttp3.ResponseBody> = service.request().execute()
} catch (ex: IOException) {
}

// 异步请求
// service.request().enqueue(callback)
```

看起来是不是和OkHttp差不多？Retrofit把OkHttp的Call、Request以及Response等对象藏了起来，并提供了一套它自己的等价物。如果仅是这样，那的确没什么意思。现在是时候施展魔法了，我们把Retrofit对象改为最常见的样子：

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://example.com/")
    .client(OkHttpClient())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

仅需要添加一个CallAdapterFactory以及一个ConverterFactory，我们的Service就可以变成这个样子：

```kotlin
interface SampleService {
    @GET("path/")
    fun request(): Observable<Person>
}
```

可以看到，Call和ResponseBody对象都不见了，取而代之的是Observable和具体的数据类，前者从Retrofit无缝衔接到了RxJava，后者直接透明地把ResponseBody解析成了数据类。Retrofit+RxJava+Gson的组合，应该是近年来使用最广泛的网络请求框架了。

至此Retrofit的核心思想我们已经都了解了。OkHttp隐藏了网络的细节，降低了网络请求的准入门槛，Retrofit则进一步把数据解析自动化了，就好像服务器会直接返回数据类一样。网络请求从一大段内容变成一行代码，这还不是魔法吗？

在Retrofit的 [官网](https://square.github.io/retrofit/) 上，它是这样介绍自己的：

> A type-safe HTTP client for Android and Java

什么是type-safe，在 [stackoverflow](https://stackoverflow.com/questions/260626/what-is-type-safe) 上有一段精彩的讨论，感兴趣的话可以去看看。体现在这里就是Retrofit通过包裹OkHttp，添加了许多规则，进行了许多校验。

Retrofit提供了一系列注解来完成Request的创建，这是有意义的。当我们通过Builder创建Request对象时，不仅有大量的方法使人眼花缭乱，在进行post等操作时我们还需要掌握如何正确创建一个RequestBody对象，这里的每一步都可能出错。Retrofit要求我们使用注解做这件事，虽然注解很多，但组合起来也就那么几种，而且ConverterFactory甚至能够帮助构建RequestBody。

在遵守规则的前提下，我们只要记住自己想要GET还是POST，剩下的一切都不必担心，这大大降低了出错的可能。现在，我们可以说，**这就是Retrofit！**

Retrofit从根本上来讲就做了以上三件事，在正确的前提下尽可能的简单，还真是令人赏心悦目啊。

# Retrofit是怎么实现的

接下来我们看看Retrofit是如何施法的，老实说，和天才的想法相比，以下内容稍有枯燥，但如果只知想法而不能实现，那只能算完成了一半。由于我们不打算了解全部的细节，以下代码都有精简。

## Service的创建

Retrofit使用的第一步是创建Service，它是一个接口，但却可以实现功能，这里用的是动态代理，简单来说就是动态生成类的实例，在调用方法时会执行InvocationHandler中的代码。

```java
public <T> T create(final Class<T> service) {
    // 校验类是不是符合规则
    validateServiceInterface(service);
    return (T)
        Proxy.newProxyInstance(
            service.getClassLoader(),
            new Class<?>[] {service},
            new InvocationHandler() {
                @Override
                public @Nullable Object invoke(Object proxy, Method method, @Nullable Object[] args)
                    throws Throwable {
                    // ...
                    return loadServiceMethod(method).invoke(args);
                }
            });
}
```

可以看到，当我们拿到Service的实例后，调用任何方法都会执行loadServiceMethod(method).invoke(args)。所以loadServiceMethod是接下来分析的关键。

## 获取ServiceMethod

```java
ServiceMethod<?> loadServiceMethod(Method method) {
    ServiceMethod<?> result = serviceMethodCache.get(method);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
      result = serviceMethodCache.get(method);
      if (result == null) {
        result = ServiceMethod.parseAnnotations(this, method);
        serviceMethodCache.put(method, result);
      }
    }
    return result;
}
```

重点是ServiceMethod.parseAnnotations方法，它的实现如下：

```java
static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
    RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);

    // ...

    return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
}
```

parseAnnotations内部做了两件关键的事，一个是生成RequestFactory，另一个就是生成实际的ServiceMethod对象了。RequestFactory不是主流程里最重要的内容，但它是Retrofit构建Request的实现，所以这里先看下它大概做了什么。

## RequestFactory的主要思路

```java
static RequestFactory parseAnnotations(Retrofit retrofit, Method method) {
    return new Builder(retrofit, method).build();
}

RequestFactory build() {
    for (Annotation annotation : methodAnnotations) {
        // 解析方法上的注解，@GET @POST等
        parseMethodAnnotation(annotation);
    }

    int parameterCount = parameterAnnotationsArray.length;
    parameterHandlers = new ParameterHandler<?>[parameterCount];
    for (int p = 0, lastParameter = parameterCount - 1; p < parameterCount; p++) {
        // 解析方法中的参数
        parameterHandlers[p] = parseParameter(p, parameterTypes[p], parameterAnnotationsArray[p], p == lastParameter);
    }
    
    return new RequestFactory(this);
}
```

parseMethodAnnotation很简单，就是保证所有的注解按照规范使用，parseParameter需要看一下，因为它涉及到前面提到的Request参数拼装。

```java
private @Nullable ParameterHandler<?> parseParameter(
    int p, Type parameterType, @Nullable Annotation[] annotations, boolean allowContinuation) {
    ParameterHandler<?> result = null;
    if (annotations != null) {
        for (Annotation annotation : annotations) {
            // param也必须有注解
            ParameterHandler<?> annotationAction =
                parseParameterAnnotation(p, parameterType, annotations, annotation);

            result = annotationAction;
        }
    }
    return result;
}

private ParameterHandler<?> parseParameterAnnotation(
        int p, Type type, Annotation[] annotations, Annotation annotation) {
    if (annotation instanceof Path) {
        Converter<?, String> converter = retrofit.stringConverter(type, annotations);
        return new ParameterHandler.Path<>(method, p, name, converter, path.encoded());
    } else if (annotation instanceof Body) {
        Converter<?, RequestBody> converter;
        converter = retrofit.requestBodyConverter(type, annotations, methodAnnotations);
        return new ParameterHandler.Body<>(method, p, converter);
    } 

    return null; // Not a Retrofit annotation.
}
```

parseParameterAnnotation里做了大量工作，我们只关注它把接口的参数转成Request参数的过程，主要是利用了retrofit.stringConverter和retrofit.requestBodyConverter来处理的。记得前面的addConverterFactory(GsonConverterFactory.create())吗？也就是这里实际上是用Gson把Bean对象转成了需要的格式，当然也可以使用不同的Converter，例如Moshi。

retrofit.requestBodyConverter的实现我们后边再看，现在回到前一步，看看HttpServiceMethod.parseAnnotations做了什么。

## 再回ServiceMethod的创建 

```java
static <ResponseT, ReturnT> HttpServiceMethod<ResponseT, ReturnT> parseAnnotations(
    Retrofit retrofit, Method method, RequestFactory requestFactory) {
    Annotation[] annotations = method.getAnnotations();
    Type adapterType;
    adapterType = method.getGenericReturnType();

    CallAdapter<ResponseT, ReturnT> callAdapter =
        createCallAdapter(retrofit, method, adapterType, annotations);
    Type responseType = callAdapter.responseType();

    Converter<ResponseBody, ResponseT> responseConverter =
        createResponseConverter(retrofit, method, responseType);

    okhttp3.Call.Factory callFactory = retrofit.callFactory;
    return new CallAdapted<>(requestFactory, callFactory, responseConverter, callAdapter);
}
```

这里出现了createCallAdapter和createResponseConverter方法，至此Retrofit里的三个魔法我们都看到了，它们是CallAdapter、RequestBodyConverter和ResponseBodyConverter，所有这些都是在Retrofit定义时添加的。

CallAdapter和Converter遵循一样的原理，我们用CallAdapter作为例子，看下它是怎么处理的。

```java
// 省略漫长的调用链...
public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
    return nextCallAdapter(null, returnType, annotations);
}

public CallAdapter<?, ?> nextCallAdapter(
    @Nullable CallAdapter.Factory skipPast, Type returnType, Annotation[] annotations) {

    int start = callAdapterFactories.indexOf(skipPast) + 1;
    for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
      CallAdapter<?, ?> adapter = callAdapterFactories.get(i).get(returnType, annotations, this);
      if (adapter != null) {
        return adapter;
      }
    }
}
```

现在真相大白，Retrofit遍历callAdapterFactories，直到某个Factory返回了Adapter实例，就用它来做解析，因此Factory的添加顺序很重要，如果两个Factory可以处理同一种类型，后加入的将没有机会运行。

最后返回的CallAdapted代码如下，它的作用是把原始的Call对象根据Adapter转换成其他对象，例如前面的Observable。

```java
static final class CallAdapted<ResponseT, ReturnT> extends HttpServiceMethod<ResponseT, ReturnT> {
    private final CallAdapter<ResponseT, ReturnT> callAdapter;

    @Override
    protected ReturnT adapt(Call<ResponseT> call, Object[] args) {
      return callAdapter.adapt(call);
    }
}
```

CallAdapted继承自HttpServiceMethod，HttpServiceMethod又是ServiceMethod的子类。现在，我们找到了真实的ServiceMethod。

## invoke做了什么

按照前面动态代理的内容，我们会执行ServiceMethod的invoke方法，具体实现在HttpServiceMethod中。

```java
final @Nullable ReturnT invoke(Object[] args) {
    Call<ResponseT> call = new OkHttpCall<>(requestFactory, args, callFactory, responseConverter);
    return adapt(call, args);
}
```

adapt我们刚刚说过了，现在只剩下最后一部分内容，也就是Retrofit和OkHttp交互的地方：OkHttpCall。它是一个Call对象，但内部执行的都是OkHttp的代码。OkHttp主要有enqueue和execute两种使用方法，我们看看它们做了什么即可。

```java
final class OkHttpCall<T> implements Call<T> {
  @Override
  public void enqueue(final Callback<T> callback) {
    okhttp3.Call call;
    call = rawCall = createRawCall();

    call.enqueue(
        new okhttp3.Callback() {
          @Override
          public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) {
            Response<T> response;
            response = parseResponse(rawResponse);
            callback.onResponse(OkHttpCall.this, response);
          }

          @Override
          public void onFailure(okhttp3.Call call, IOException e) {
            callFailure(e);
          }

          private void callFailure(Throwable e) {
            callback.onFailure(OkHttpCall.this, e);
          }
        });
  }

  @Override
  public Response<T> execute() throws IOException {
    okhttp3.Call call;
    call = getRawCall();
    return parseResponse(call.execute());
  }

  private okhttp3.Call createRawCall() throws IOException {
    okhttp3.Call call = callFactory.newCall(requestFactory.create(args));
    return call;
  }

  Response<T> parseResponse(okhttp3.Response rawResponse) throws IOException {
    ResponseBody rawBody = rawResponse.body();

    // 处理错误情况...

    ExceptionCatchingResponseBody catchingBody = new ExceptionCatchingResponseBody(rawBody);
    T body = responseConverter.convert(catchingBody);
    return Response.success(body, rawResponse);
  }
}
```

现在一切都明白了，Retrofit把OkHttp包装起来，不管是Request还是Response，或者是Callback等都自己定义一遍，然后供自己的Adapter进行其他操作，实现一波“偷梁换柱”。

最后我们看下RxJava2CallAdapter的adapt的主要内容吧，这里可以更清楚地看到应该怎么处理Call对象。

```java
final class RxJava2CallAdapter<R> implements CallAdapter<R, Object> {
  // ...

  @Override
  public Object adapt(Call<R> call) {
    Observable<Response<R>> responseObservable =
        isAsync ? new CallEnqueueObservable<>(call) : new CallExecuteObservable<>(call);

    Observable<?> observable;
    if (isResult) {
      observable = new ResultObservable<>(responseObservable);
    } else if (isBody) {
      observable = new BodyObservable<>(responseObservable);
    } else {
      observable = responseObservable;
    }

    if (scheduler != null) {
      observable = observable.subscribeOn(scheduler);
    }

    if (isFlowable) {
      return observable.toFlowable(BackpressureStrategy.LATEST);
    }
    if (isSingle) {
      return observable.singleOrError();
    }
    if (isMaybe) {
      return observable.singleElement();
    }
    if (isCompletable) {
      return observable.ignoreElements();
    }
    return RxJavaPlugins.onAssembly(observable);
  }
}
```

# 总结

总体来看，Retrofit的实现比较简单，但如果不是事先分析出它的主要思想，也可能会沉入代码细节。代码细节里可以学到很多东西，例如设计模式和代码规范等，但从局部看问题会丢失很多重要的东西。从代码细节，你体会不到Retrofit为何存在，也无法掌握Retrofit主要解决的问题，更重要的是没有全局观，我们就学不会设计一个库最精彩的思想。实践很重要，但没有理论支持，只能通过试探，怎么发现最佳实践呢？

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。