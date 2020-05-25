在[《也谈Android应用架构》](./也谈Android应用架构.md)中我们对MVC、MVP、MVVM进行了详尽的分析，但还有一个问题悬而未决，那就是生命周期。在Android平台上生命周期具有十分重要的意义，因此这也是架构必须考虑的因素之一。生命周期处理不好很容易发生内存泄漏，但对架构而言，真正困扰我们的却不是内存泄漏的问题，反而是因生命周期太短，实例被销毁重建，从而产生一系列不必要的行为。这种情况发生的场景主要在屏幕旋转以及页面被系统回收时。

Activity难免需要依赖网络、数据库等数据来渲染页面，当屏幕旋转时，Activity重建，因而数据需要重新加载，但这完全没有必要。一种策略是对数据进行缓存，这是一种可考虑的方案，但它只解决了一半的问题，如果Activity重建发生在数据返回前，此时根本来不及缓存，下一次请求就迅速地发生了。

在MVP、MVVM架构中，数据由M来提供，但真正和生命周期打交道的是P和VM，我们得从这里着手解决生命周期的问题。再明确地说一遍，我们要解决的问题是**不论在数据返回前还是返回后**，在屏幕旋转这种场景下都不需要多次加载数据。这个问题由两种状态组成：加载中和加载完成后，对于前者我们要知道当前正在加载数据，对于后者则只需要把数据缓存起来即可。

对数据缓存很简单，但加载中的状态就要好好斟酌一番了，我们可以轻易地给这个状态加标记，但随着重建这个标记也会被回收，由此可以想到两种应对之法，一是让P和VM不被回收，这样就可以进行标记了，二是让当前这个加载不被回收，也就是其生命周期不和P与VM同步。不让P和VM回收，有以下几种方式：

- 配置`android:configChanges="orientation|keyboardHidden|screenSize"`

- 
`onRetainCustomNonConfigurationInstance()`/`getLastCustomNonConfigurationInstance()`

- 继承Fragment

除了配置configChanges，其余两种方式都是不错的解决办法。除此之外还有一种方式可以同时实现我们说的两种应对之法，这就是**Loader**。关于什么是Loader以及Loader如何保持P和VM不被回收，大家可以自行查阅相关资料，如何保持一个加载任务不被回收，可以参阅[architecture-samples](https://github.com/android/architecture-samples)
，并切换到分支`deprecated-todo-mvp-loaders`。

我们不打算大刀阔斧地讲述每个方案的细节和优缺点，因为随着Jetpack诞生，这种复杂又费力的方案系统已经帮我们完成了，我们只需要了解系统是如何处理的即可。从书写代码变成查看代码，可以说大大减少了我们对生命周期的“怨恨”，不得不说Google这波操作很圈粉呢。在这里，我们只关注Lifecycle、ViewModel和LiveData三部分。

# Lifecycle

生命周期让人困扰的很大一部分原因是只有Activity这样的系统组件才可以感知生命周期的变化，而Lifecycle的出现则把这种感知力放大到了任何类。Lifecycle的原理很简单，当生命周期变化时，Activity通知到Lifecycle，其他类就可以通过Lifecycle感知生命周期的变化了。

Lifecycle的核心就三个类：**Lifecycle**、**LifecycleOwner**和**LifecycleObserver**。从名字就可以轻易看出这是一个观察者模式，Activity作为LifecycleOwner，把生命周期的变化反映到Lifecycle，Lifecycle再通知给所有的LifecycleObserver即可。这个概念太简单了，就不在此赘述源码了（看了一下没有什么亮点~），不过如果你感兴趣，请注意一下**ReportFragment**这个类，Activity的生命周期就是通过它来通知Lifecycle的（添加一个看不见的Fragment，这个操作似乎似曾相识？）。

Lifecycle只是让P和VM获得了生命周期感知能力，并没有解决如何保持的问题，不过它是我们后面内容的基础，所以还是很有必要了解一番。

# ViewModel

这个ViewModel其实就是MVVM中的VM，但经过Google的加工之后具备了很好的生命周期感知能力，这就是我们苦苦追寻的东西呀。现在我们就对它抽丝剥茧，看看系统是如何完成这件事的。

ViewModel的使用非常简单，就是一句话：

```
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }
}
```

当重建发生时，LoginActivity、ViewModelProvider都是新的实例，但是LoginViewModel一定得是原来的实例，这说明它在某处被缓存了起来。先看下ViewModelProvider做了什么吧：

```
public ViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
    this(owner.getViewModelStore(), factory);
}

public <T extends ViewModel> T get(@NonNull Class<T> modelClass) {
    String canonicalName = modelClass.getCanonicalName();
    // ...
    return get(DEFAULT_KEY + ":" + canonicalName, modelClass);
}

public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
    ViewModel viewModel = mViewModelStore.get(key);

    if (modelClass.isInstance(viewModel)) {
        // ...
        return (T) viewModel;
    } else {
        // ...   
    }
    if (mFactory instanceof KeyedFactory) {
        viewModel = ((KeyedFactory) (mFactory)).create(key, modelClass);
    } else {
        viewModel = (mFactory).create(modelClass);
    }
    mViewModelStore.put(key, viewModel);
    return (T) viewModel;
}
```

非常简单，从Activity获取到了一个ViewModelStore，如果里面包含了LoginViewModel就直接取出来，否则新建一个并缓存到ViewModelStore里。那么ViewModelStore是什么，它是如何保持下来的？

ViewModelStore里维护了一个Map，存储ViewModel实例，仅此而已。AppCompatActivity实现了**ViewModelStoreOwner**接口，里面只有一个方法**getViewModelStore**，它的实现如下：

```
public ViewModelStore getViewModelStore() {
    if (getApplication() == null) {
        throw new IllegalStateException("Your activity is not yet attached to the "
                + "Application instance. You can't request ViewModel before onCreate call.");
    }
    if (mViewModelStore == null) {
        NonConfigurationInstances nc =
            (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            // Restore the ViewModelStore from NonConfigurationInstances
            mViewModelStore = nc.viewModelStore;
        }
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
    }
    return mViewModelStore;
}
```

这里出现了一个**getLastNonConfigurationInstance()**，我们在前面提过一个**getLastCustomNonConfigurationInstance()**方法，那么应该也有一个**onRetainNonConfigurationInstance()**与之对应，它的实现如下：

```
public final Object onRetainNonConfigurationInstance() {
    Object custom = onRetainCustomNonConfigurationInstance();

    ViewModelStore viewModelStore = mViewModelStore;
    if (viewModelStore == null) {
        // No one called getViewModelStore(), so see if there was an existing
        // ViewModelStore from our last NonConfigurationInstance
        NonConfigurationInstances nc =
                (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            viewModelStore = nc.viewModelStore;
        }
    }

    if (viewModelStore == null && custom == null) {
        return null;
    }

    NonConfigurationInstances nci = new NonConfigurationInstances();
    nci.custom = custom;
    nci.viewModelStore = viewModelStore;
    return nci;
}
```

一切都很明了了，系统用的是和我们一样的方法，只是方法名称稍有区别而已。ViewModelStore里缓存了ViewModel实例，那么在Activity真正销毁时肯定需要清空，ViewModel和ViewModelStore都提供了一个**clear()**方法，ViewModelStore的clear方法实现如下：

```
public final void clear() {
    for (ViewModel vm : mMap.values()) {
        vm.clear();
    }
    mMap.clear();
}
```

它会调用其中每个ViewModel的clear方法使我们有机会清除一些数据或任务，然后就将Map清空了。在Activity中它是这样被调用的：

```
getLifecycle().addObserver(new LifecycleEventObserver() {
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source,
            @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            if (!isChangingConfigurations()) {
                getViewModelStore().clear();
            }
        }
    }
});
```

**isChangingConfigurations()**用以标识Activity执行onDestory方法后是否准备重建，只有不重建时才会清空ViewModel，所以只要在clear时清理数据和中断任务就好了。

现在我们解决了ViewModel实例保持的问题，接下来让我们再想想应该怎么解决数据重复加载的问题。数据重复加载主要是因为一个异步任务被多次调用，例如请求一个列表数据时，如果屏幕发生旋转，以下方法会被多次调用：

```
fun getUsers(){
    executor.execute {
        val users = model.getUsers()
        handler.post{
            view?.getUsers(users)
        }
    }
}
```

按照之前的说法，可以给加载任务加上标记，当它正在加载中就等待它加载完成，如果已经加载完就取缓存的数据，但是这太复杂了，稍有不慎就会出问题。如何让事情变得简单一些，出错率低一些呢？

要想避免此问题，最好的方式是只调用一次getUsers()方法，那这个方法就不能由Activity来调用了，需要ViewModel自己调用，等它拿到结果后反过来通知Activity。这不就是MVVM吗？现在我们总算明白为什么被系统实现的这个类叫ViewModel了，因为它就是为MVVM量身定制的。

关于数据反过来通知Activity这件事，也不需要担心，因为系统照样帮我们实现了，这就是LiveData。

# LiveData

可观察的数据并不是只有LiveData，但LiveData有自己独特的本领，它也具备生命周期感知力。LiveData只有在有效的生命周期范围内通知观察者，并在生命周期结束后自动移除观察者，仅这一点就足够让它脱颖而出。我们可以从它的**observe**方法，了解它处理生命周期的大致流程。

```
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread("observe");
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
        // ignore
        return;
    }
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    if (existing != null && !existing.isAttachedTo(owner)) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    if (existing != null) {
        return;
    }
    owner.getLifecycle().addObserver(wrapper);
}
```

这里创建了一个LifecycleBoundObserver来观察Activity的生命周期，我们看看它做了哪些工作吧：

```
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
    // ...

    @Override
    boolean shouldBeActive() {
        return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source,
        @NonNull Lifecycle.Event event) {
        if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
            removeObserver(mObserver);
            return;
        }
        activeStateChanged(shouldBeActive());
    }

    // ...
}
```

它实现了LifecycleEventObserver，并在DESTROYED状态时移除了观察者，其后只是调用了一个activeStateChanged方法，这个方法实现如下：

```
void activeStateChanged(boolean newActive) {
    if (newActive == mActive) {
        return;
    }
    // immediately set active state, so we'd never dispatch anything to inactive
    // owner
    mActive = newActive;
    boolean wasInactive = LiveData.this.mActiveCount == 0;
    LiveData.this.mActiveCount += mActive ? 1 : -1;
    if (wasInactive && mActive) {
        onActive();
    }
    if (LiveData.this.mActiveCount == 0 && !mActive) {
        onInactive();
    }
    if (mActive) {
        dispatchingValue(this);
    }
}
```

这里通过是否active来分发数据，在dispatchingValue中会通知所有的观察者。

LiveData实际上是一个双层的观察者模式，它通过观察Lifecycle得知是否active，在此充当的是观察者。当它的值发生变化或者监听到Lifecycle变化时再通知到它的观察者，在此又充当被观察者。如此它就具备了我们想要的一切能力。

# 总结

现在我们的架构“本地化”工作又前进了一大步，它终于在生命周期方面也不存在问题了，使用Lifecycle+ViewModel+LiveData组合，解决了架构最棘手的问题，也把MVVM推向了另一个高度。当然这并不代表着MVP就彻底败下阵来，毕竟生命周期问题只影响了初始化时的数据，大量场景下还是有无数的交互行为，需要根据用户的操作主动加载各种各样的数据，这种情况下，MVP的直观性要远远强于MVVM，这个特点也可以简单理解为MVP适合复杂交互场景，MVVM适合展示型场景。因此我们应该根据具体场景灵活选用MVP和MVVM，甚至在某些情况下可以合二为一。

还是那句话，没有最好的架构，只有最适合当前场景的架构。

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。