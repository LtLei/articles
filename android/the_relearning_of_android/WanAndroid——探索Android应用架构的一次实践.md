在 [《也谈Android应用架构》](./也谈Android应用架构.md) 和 [《Jetpack之Lifecycle、LiveData及ViewModel是如何让架构起飞的》](./Jetpack之Lifecycle、LiveData及ViewModel是如何让架构起飞的.md) 两篇文章中，我们详细论述了MVC、MVP、MVVM架构的思想、优缺点以及使用注意事项，并阐述了借助Jetpack强大的生命周期管控能力解决架构“本地化”的问题。但没有实践的论述不仅不直观，也应了那句`Talk is cheap, show me the code.`的经典名言，因此这个基于 [玩Android](https://www.wanandroid.com/) 网站提供的开放API实现的客户端便应运而生了。

项目地址：[https://github.com/LtLei/wanandroid](https://github.com/LtLei/wanandroid)

<div align="center"><img src ="./image/img_2_1.gif" /><br/>Wan Android</div>


在1.0版本中，涵盖了首页，分类页，搜索页，以及登录账号并对文章进行收藏，查看收藏列表等功能。总体来说功能并不复杂，但作为演示已经足够了。项目整体采用了**MVP**+**Lifecycle**+**ViewModel**+**LiveData**，结合**Room**进行数据缓存，使用**Retrofit**+**Coroutines**进行网络请求。为什么没有**DataBinding**？请听我稍后解释。

下面是几个我认为在实践中比较重要的点，特此列出来以便大家思考以及对我进行指点。

# 为什么是MVP而不是MVVM+DataBinding？

说为什么不是MVVM，不如说为什么要是MVVM。最近看到许多文章都在大力“推销”MVVM和DataBinding，听起来好像MVP已经过时了，Jetpack+DataBinding已经君临天下，成为了Android开发的唯一范式。且不说MVVM有没有全面超越MVP，单就这样的“吹捧”本身就值得我们反思。

在[《也谈Android应用架构》](./也谈Android应用架构.md) 中，我曾说过MVP最大的问题就是VP相互纠缠，即使利用了**单一职责原则**对P进一步分化，采用了MVP-R技术，最后因为P要通知V的顽疾不得不留有遗憾。让我们再次对这个问题进行更深入的探究，出现问题的部分如下：

```
public class SharedVipPresenter{
    private VipRepository mVipRepository;
    private SharedVipView mSharedVipView;
    // ...
    public void getVipInfo(){
        VipInfo vi = mVipRepository.getVipInfo();
        mSharedVipView.getVipInfoSuccess(vi);
    }
}

public class VipPresenter{
    private VipRepository mVipRepository;
    private VipView mVipView;
    // ...
    public void getVipInfo(){
        VipInfo vi = mVipRepository.getVipInfo();
        mVipView.getVipInfoSuccess(vi);
    }
}
```

可以看到为了反馈到不同的V，我们写了几乎一样的代码，且很难优化它，于是这就成了MVP的一个显著缺点，也反向证明了MVVM是多么优越。但是难以处理不代表无法处理，这时候反而可以从MVVM中吸取一点经验了，要做的事情其实只有一件：让P把结果通知给V。说到通知，至少有三种方案可以考虑：

* P持有V，直接调用V的方法，也就是经典的MVP实现方式
* P不再持有V，但可以通过注册回调方式由V自行接收结果，类似Callback方式
* P不持有V，但可以使用订阅模式，类似DataBinding

让我们暂时抛开成见来仔细琢磨下这几种方案的异同，你会发现它们只是三种不同的表现形式而已。不管是LiveData还是DataBinding，不都只是A通知B这一核心问题的一种解决方式而已吗？只不过我们使用MVP时选择了耦合的这一种方式而已，这并不是LiveData或DataBinding本身优秀的原因。仅在A通知B这一方面，没有对错，也没有输赢，因为回调也好，订阅也好，本就不是它们的专利。

所以，如果我们在P和V之间使用订阅或回调，也不会对DataBinding等产生任何“侵权”行为，但DataBinding给我们以启示，使得我们能跳出表现层而看出更深层次的含义，这一点倒不得不给它“颁奖”了。有了这一层认知，现在可以非常肯定的说，Jetpack和MVVM根本就是两个方向的东西，唯一看起来有些相似的不过是都用了订阅模式而已，因而没有必要绑定在一起，**Jetpack和MVP一样可以无缝衔接**。

没有了后顾之忧，我们就可以好好观察一番MVVM了，在Android中实现MVVM主要靠DataBinding。这无疑是一个伟大的框架，是会让无数人爱不释手的框架，但如果你还没有使用它倒也不必惊慌。DataBinding的核心是数据绑定，也就是把Model绑定到UI组件上，同时也负责Model和UI之间的数据同步问题。在使用上的体验就是样板代码大大减少了，这应该是最主要最明显的感受了。

这种体验确实是无法拒绝的，但因“一腔热血”而全身心投入是不够理智的行为。经过仔细分析，DataBinding还是有一大一小两个问题：

## 问题1 UI复用

这应该是再小不过的问题了，使用DataBinding后Layout将很难复用，虽然这和UI优化有一定的冲突，但不是所有的都不能复用，也不是所有人都会严格地遵守复用原则（如果没有极强的规范，难道不是直接写布局最简便？）。所以和它的优势相比，这个问题的影响可以小到不计了。

## 问题2 设计模式上的打击

我认为这是相对较大的一个问题，DataBinding把原本仅在Activity/Fragment中的UI逻辑部分地搬到了Layout中。原本的Layout文件和Model毫不相干，是纯粹的UI组件，它的数据绑定完全由Activity来操作，DataBinding把这个步骤搬到了Layout里，但并不彻底，一些操作还是需要Activity+Layout组合完成。这个现象破坏了一些原则，我们经常强调单一职责，但没怎么提过一件事应该有始有终，已经不可再分的一个职责明显没必要由两个组件一起完成。

所以说，如果你已经选择了DadaBinding，那么继续使用也没有什么问题，一个优点和一个缺点互相抵消，至少表明这样做是不会错的。但是如果你还没有打算使用它，也没必要因为热度而急于切换，尽管我们相信未来MVVM一定会大放异彩，但能够一起出道的是不是DataBinding还犹未可知，所以保持观望也未免不是一种明智的选择。另外，Google近期又推出了ViewBinding大杀器，很久就会到来的还有Compose，这一切不都说明了战争才刚刚开始吗？

如果你也希望直达战争的结果，那就和我一样作“冷眼旁观”状吧，硝烟结束的一刻，才是MVP光荣退役的真正时刻。

# 关于依赖注入（DI）

关于DI，早就有一个家喻户晓的框架叫做Dagger2，但它出场的概率和其他框架相比实在差距太远了。我想不外乎两个原因，一是Dagger本身太复杂了，学习成本奇高，二就是DI本身并没有那么被重视（当然我是讲在广泛的范围下）。DI已经算是非常基础的思想了，想来大家都很了解，这里不多作介绍了。不过不使用Dagger照样可以做好DI，如果你被Dagger折磨过，那么手动DI一定会让你爱不释手的。

# LiveData使用的注意事项

当屏幕旋转或页面被回收，或其他原因导致页面生命周期变化后，由于LiveData被很好地保持了下来，当页面重建后通过新的Observer与之关联时，必定会触发onChange方法把数据同步到页面中（如果有数据的话），从添加Observer的流程就可以看出这一点：

```
// 添加Observer的过程
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread("observe");
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
        // ignore
        return;
    }
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    // ...
}

// LifecycleBoundObserver实现了LifecycleEventObserver，当生命周期变化时会回调 onStateChanged 方法
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
    // ...

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source,
            @NonNull Lifecycle.Event event) {
        if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
            removeObserver(mObserver);
            return;
        }
        activeStateChanged(shouldBeActive());
    }
}

// 经过一系列操作，当生命周期变化时会调用dispatchingValue方法
void activeStateChanged(boolean newActive) {
    // ...
    if (mActive) {
        dispatchingValue(this);
    }
}

// 通过considerNotify决定是否需要同步数据
void dispatchingValue(@Nullable ObserverWrapper initiator) {
    if (mDispatchingValue) {
        mDispatchInvalidated = true;
        return;
    }
    mDispatchingValue = true;
    do {
        mDispatchInvalidated = false;
        if (initiator != null) {
            considerNotify(initiator);
            initiator = null;
        } else {
            for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
                    mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
                considerNotify(iterator.next().getValue());
                if (mDispatchInvalidated) {
                    break;
                }
            }
        }
    } while (mDispatchInvalidated);
    mDispatchingValue = false;
}

// 通过对比LiveData的mVersion字段和Observer的mLastVersion字段，决定是否需要同步数据
private void considerNotify(ObserverWrapper observer) {
    // ...
    
    if (observer.mLastVersion >= mVersion) {
        return;
    }
    observer.mLastVersion = mVersion;
    observer.mObserver.onChanged((T) mData);
}
```

当Observer刚添加进来时，它的mLastVersion是-1，而LiveData除非没操作过否则一定不是-1，这时候就必然回调onChange了。

这对我们有什么影响呢？假如LiveData中存储的是页面需要的数据，例如一个列表，当页面重建后恢复列表的数据，这正是我们想要的。但假如我们进行的是单次操作，例如点击按钮进行收藏，LiveData用来说明收藏结果，这时重建后LiveData的值会再次反映到页面中，就会看到类似旋转一次屏幕就弹出一次“收藏成功”的怪诞现象了。

一种方式是对于单次操作，每次使用完数据后手动置为null，null通常会被我们过滤掉，所以即使onChange回调了也不会有问题。不过手动置空给我们增加了负担，必须在想到这是一次单次操作的同时想到置空，所以最好是我们知道它是单次操作后，可以自动处理这种情况。这里提供一种较为合理，侵入性又较小的方式，使用Event包装返回数据，使用EventObserver代替Observer即可：

```
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}
```

# 关于LiveDataBus

本项目中并没有使用LiveDataBus，但还是有必要谈一下关于消息总线问题的一些想法。现阶段消息总线主要有EventBus，RxBus以及这个LiveDataBus三种实现，前两种早已在漫长的时间里经过了无数检验，我们主要谈一谈LiveDataBus。

当一个页面的状态改变，需要通知到许多页面时，就需要消息总线了。不管是EventBus还是RxBus，以及现在的LiveDataBus，都是基于订阅模式实现的，所不同的是LiveData具备了生命周期安全的优势。但正如上面所说的，LiveDataBus也会有一订阅就收到数据的问题，而且由于这是一对多的关系，不能通过类似Event那样的方式解决。因此当你看到LiveDataBus时，基本上都是通过反射修改了Observer的mLastVersion字段值，使之与LiveData当前的mVersion一致，来变相达到目的。

其实我并不认为这是一种好的解决方案，LiveData并不是为了消息总线而设计的，它的Observer也仅仅是页面级的组件，不应该处理跨页面级的事务，这种反射实际上给LiveData增加了不属于它的能力，破坏了原结构的完整性。在LiveData本身不具备这个机制前，保守地使用专业的、经过检验的EventBus和RxBus等，也是一种合情合理的态度。

虽然LiveDataBus算不得脱颖而出，LiveData本身还是可以处理一些和数据更新相关的事情的。例如很多页面离不开User信息，那么维护一个公用的UserLiveData就可以保证任何时候取得的信息都是最新的，这也是LiveData服务相当周到的一点体现吧。

# 关于Repository的小优化

在M层的优化中，有一步骤是使用Repository来处理全部的业务，包含纯粹的M和经数据加工后的M两部分。但是伴随M增大，需要处理的数据也会变多，Repository也会发生体积暴涨问题。对其进行优化我觉得有一个很重要的前提，那就是要M之外的组件对此零感知，ViewModel永远仅依赖Repository本身。在这个前提下可以进行的优化空间并不大，主要是把业务进行分组，由一系列的小Repository分别处理一部分问题，分组要选择合适的粒度，太细的话就会发生类的数量暴涨了。

# 总结

理论总是空洞的，使人觉得似懂非懂，这次的实践在一些方面对其进行了很好的诠释。当然它不是也不会是Android开发的最佳实践，技术会不断进步，思想本身也会不断演进，而且APP还有非常多的方面需要我们注意，我们需要的是保持活跃的思维跟进技术思想变革，同时也要保持理智，要对事物有自己的分辨。

在这次实践里，我认为最大的启示是不要过于贪心，不能执着于把每一部分都做到完美，反而是把目标变小一些，仅在一个范围内做到最好，然后逐渐扩展到全局，也就是实践出真知，眼高手低是无法把事情做到卓越的。

# 后续规划

1.0版本仅仅是开始，我们演示了架构最基础的结构，在一些类上做了一定的优化，但APP的开发还有更多的知识需要探索。所以接下来让我们继续扬帆起航，探索更多的奥秘吧。

后续我会先介绍单元测试的作用，并展示更多组件的使用方式，以及当项目变大时使用组件化优化等方面的问题，接下来是对一些基础知识的深入探索，最后会对一些面向未来的新事物略窥一二。

学无止境，希望能和热爱学习的你，共勉。

**本项目github地址 [https://github.com/LtLei/wanandroid](https://github.com/LtLei/wanandroid)**

---

本文到此就结束了，如果您喜欢我的文章，可以关注我的微信公众号： **大大纸飞机** 

或者扫描下方二维码直接添加：

<div align="center"><img src ="./image/qrcode.jpg" /><br/>扫描二维码关注</div>

您也可以关注我的简书：https://www.jianshu.com/u/9ee83a8ee52d

编程之路，道阻且长。唯，路漫漫其修远兮，吾将上下而求索。