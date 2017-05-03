[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Material--Animations-brightgreen.svg?style=flat)](http://android-arsenal.com/details/3/1880)

[Android Transition Framework][transition-framework] 可以在以下 **3** 种情况下使用:

1. 从一个活动跳转到另一个活动时，界面切换动画.
2. 在活动之间的跳转, 共享元素的切换动画.
3. 同一个界面中的控件, 产生变化(**视图的添加，删除，状态，大小改变**)时的动画.


## 1. Transitions between Activities(界面过渡动画)

Animate existing activity layout **content**

![A Start B][transition_a_to_b]

当从**Activity A**转换到**Activity B**时，定义转换动画.  您可以使用以下三种预定义的转换 (`android.transition.Transition`)：**Explode**，**Slide** and **Fade**. 所有这些转换都会跟踪活动布局中目标视图的可视性的变化，并将这些视图动画化以遵循转换规则

[Explode][explode_link] | [Slide][slide_link] | [Fade][fade_link]
--- | --- | ---
![transition_explode] | ![transition_slide] | ![transition_fade]


You can define these transitions **declarative** using XML or **programmatically**. For the Fade Transition sample, it would look like this:

### Declarative(说明)
Transitions are defined on XML files in `res/transition`(在XML中定义过渡动画)

> res/transition/activity_fade.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<fade xmlns:android="http://schemas.android.com/apk/res/"
    android:duration="1000"/>

```

> res/transition/activity_slide.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<slide xmlns:android="http://schemas.android.com/apk/res/"
    android:duration="1000"/>

```

To use these transitions you need to inflate them using `TransitionInflater`
(通过 `TransitionInflater` 获取到过渡动画来使用)
> MainActivity.java

```java
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Slide slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }

```

> TransitionActivity.java

```java
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Fade fade = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

```

### Programmatically

> MainActivity.java

```java
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setExitTransition(slide);
    }

```

> TransitionActivity.java

```java
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }

```

#### Any of those produce this result:

![transition_fade]


### What is happening step by step:

1. Activity A starts Activity B

2. Transition Framework finds A Exit Transition (slide) and apply it to all visible views. **(Activity A 离开(平移动画))**
3. Transition Framework finds B Enter Transition (fade) and apply it to all visible views. **(Activity B 进入(淡入动画))**
4. **On Back Pressed** Transition Framework executes Enter and Exit reverse animations respectively (If we had defined output `returnTransition` and `reenterTransition`, these have been executed instead) **(Activity B 返回Activity A, B执行return动画, A执行reenter动画(如果没有定义return和reenter动画, 默认执行exit与enter动画))**

### ReturnTransition & ReenterTransition

Return and Reenter Transitions are the reverse animations for Enter and Exit respectively.

  * EnterTransition <--> ReturnTransition
  * ExitTransition <--> ReenterTransition

If Return or Reenter are not defined, Android will execute a reversed version of Enter and Exit Transitions. But if you do define them, you can have different transitions for entering and exiting an activity.
**(如果return or rernter 没有定义, 默认使用enter and exit 过渡动画. 如果定义了, 可以使用不同于enter and exit 的动画)**
![b back a][transition_b_to_a]

We can modify previous Fade sample and define a `ReturnTransition` for `TransitionActivity`, in this case, a **Slide** transition. This way, when returning from B to A, instead of seeing a Fade out (reversed Enter Transition) we will see a **Slide out** transition

> TransitionActivity.java

```java
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

```


Observe that if no Return Transition is defined then a reversed Enter Transition is executed.
If a Return Transition is defined that one is executed instead.

Without Return Transition | With Return Transition
--- | ---
Enter: `Fade In` | Enter: `Fade In`
Exit: `Fade Out` | Exit: `Slide out`
![transition_fade] | ![transition_fade2]


## 2. Shared elements between Activities **(界面过渡使用共享元素)**

The idea behind this is having two different views in two different layouts and link them somehow with an animation. **(背后的想法是在两个不同的布局中有两个不同的视图，并以某种方式与动画链接)**

Transition framework will then do _whatever animations it consider necessary_ to show the user a transition from one view to another. **(然后，转换框架将执行它认为必要的任何动画，向用户显示从一个视图到另一个视图的转换)**

Keep this always in mind: the view **is not really moving** from one layout to another. They are two independent views. **(牢记这一点：视图并不是真的从一个布局移动到另一个布局, 他们是两个独立的控件)**


![A Start B with shared][shared_element]


### a) Enable Window Content Transition (运行使用过渡动画)

This is something you need to set up once on your app `styles.xml`.

> values/styles.xml

```xml
<style name="MaterialAnimations" parent="@style/Theme.AppCompat.Light.NoActionBar">
    ...
    <item name="android:windowContentTransitions">true</item
    ...
</style>
```

Here you can also specify default enter, exit and shared element transitions for the whole app if you want **(你可以再这里指定过渡动画)**

```xml
<style name="MaterialAnimations" parent="@style/Theme.AppCompat.Light.NoActionBar">
    ...
    <!-- specify enter and exit transitions -->
    <item name="android:windowEnterTransition">@transition/explode</item>
    <item name="android:windowExitTransition">@transition/explode</item>

    <!-- specify shared element transitions -->
    <item name="android:windowSharedElementEnterTransition">@transition/changebounds</item>
    <item name="android:windowSharedElementExitTransition">@transition/changebounds</item>
    ...
</style>
```



### b) Define a common transition name (给共享元素指定一个transitionName)

To make the trick you need to give both, origin and target views, the same **`android:transitionName`**. They may have different ids or properties, but `android:transitionName` must be the same. **(给你的原始与目标控件设置相同的**`android:transitionName`**, 其他属性可以不一样)**

> layout/activity_a.xml

```xml
<ImageView
        android:id="@+id/small_blue_icon"
        style="@style/MaterialAnimations.Icon.Small"
        android:src="@drawable/circle"
        android:transitionName="@string/blue_name" />
```

> layout/activity_b.xml

```xml
<ImageView
        android:id="@+id/big_blue_icon"
        style="@style/MaterialAnimations.Icon.Big"
        android:src="@drawable/circle"
        android:transitionName="@string/blue_name" />
```

### c) Start an activity with a shared element

Use the `ActivityOptions.makeSceneTransitionAnimation()` method to define shared element origin view and transition name. **(使用`ActivityOptions.makeSceneTransitionAnimation()`设置共享元素view and name)**

> MainActivity.java

```java

blueIconImageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i = new Intent(MainActivity.this, SharedElementActivity.class);

        View sharedView = blueIconImageView;
        String transitionName = getString(R.string.blue_name);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
        startActivity(i, transitionActivityOptions.toBundle());
    }
});

```


Just that code will produce this beautiful transition animation:

![a to b with shared element][shared_element_anim]

As you can see, Transition framework is creating and executing an animation to create the illusion that views are moving and changing shape from one activity to the other

## Shared elements between fragments (在fragment中使用共享元素)

Shared element transition works with Fragments in a very similar way as it does with activities.

Steps **a)** and **b)** are exactly the **same**. Only **c)** changes

### a) Enable Window Content Transition

> values/styles.xml

```xml
<style name="MaterialAnimations" parent="@style/Theme.AppCompat.Light.NoActionBar">
    ...
    <item name="android:windowContentTransitions">true</item>
    ...
</style>
```

### b) Define a common transition name

> layout/fragment_a.xml

```xml
<ImageView
        android:id="@+id/small_blue_icon"
        style="@style/MaterialAnimations.Icon.Small"
        android:src="@drawable/circle"
        android:transitionName="@string/blue_name" />
```

> layout/fragment_b.xml

```xml
<ImageView
        android:id="@+id/big_blue_icon"
        style="@style/MaterialAnimations.Icon.Big"
        android:src="@drawable/circle"
        android:transitionName="@string/blue_name" />
```

###  c) Start a fragment with a shared element

To do this you need to include shared element transition information as part of the **`FragmentTransaction`** process. **(创建fragment对象, 设置过渡动画与共享元素动画)**

```java
FragmentB fragmentB = FragmentB.newInstance(sample);

// Defines enter transition for all fragment views
Slide slideTransition = new Slide(Gravity.RIGHT);
slideTransition.setDuration(1000);
sharedElementFragment2.setEnterTransition(slideTransition);

// Defines enter transition only for shared element
ChangeBounds changeBoundsTransition = TransitionInflater.from(this).inflateTransition(R.transition.change_bounds);
fragmentB.setSharedElementEnterTransition(changeBoundsTransition);

getFragmentManager().beginTransaction()
        .replace(R.id.content, fragmentB)
        .addSharedElement(blueView, getString(R.string.blue_name))
        .commit();
```

And this is the final result:

![shared_element_no_overlap]

## Allow Transition Overlap(覆盖)

You can define if enter and exit transitions can overlap each other.

From [Android documentation](http://developer.android.com/intl/ko/reference/android/app/Fragment.html#getAllowEnterTransitionOverlap()):
> When **true**, the enter transition will start as soon as possible. (设置为true, 过渡动画不用等待上一个动画执行完毕, 直接执行)
>
> When **false**, the enter transition will wait until the exit transition completes before starting. (设置为false, 过渡动画等待上一个动画执行完毕, 在执行)

This works for both Fragments and Activities shared element transitions.

```java
FragmentB fragmentB = FragmentB.newInstance(sample);

// Defines enter transition for all fragment views
Slide slideTransition = new Slide(Gravity.RIGHT);
slideTransition.setDuration(1000);
sharedElementFragment2.setEnterTransition(slideTransition);

// Defines enter transition only for shared element
ChangeBounds changeBoundsTransition = TransitionInflater.from(this).inflateTransition(R.transition.change_bounds);
fragmentB.setSharedElementEnterTransition(changeBoundsTransition);

// Prevent transitions for overlapping
fragmentB.setAllowEnterTransitionOverlap(overlap);
fragmentB.setAllowReturnTransitionOverlap(overlap);

getFragmentManager().beginTransaction()
        .replace(R.id.content, fragmentB)
        .addSharedElement(blueView, getString(R.string.blue_name))
        .commit();
```

It is very easy to spot the difference in this example:

Overlap True | Overlap False
--- | ---
Fragment_2 appears on top of Fragment_1 | Fragment_2 waits until Fragment_1 is gone
![shared_element_overlap] | ![shared_element_no_overlap]



## 3. Animate view layout elements (动画视图布局元素)

### Scenes(场景)
Transition Framework can also be used to animate element changes within current activity layout.
**(过渡框架也可用于在当前activity布局中动画元素更改)**

Transitions happen between scenes. A scene is just a regular layout which **defines a static state of our UI**. You can transition from one scene to another and Transition Framework will animate views in between.
**(过渡动画在scenes间发生, scenes只是定义我们UI的静态状态的常规布局, 您可以从一个scenes转换到另一个scenes，而过渡框架将在两者之间动画化视图)**
```java
scene1 = Scene.getSceneForLayout(sceneRoot, R.layout.activity_animations_scene1, this);
scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.activity_animations_scene2, this);
scene3 = Scene.getSceneForLayout(sceneRoot, R.layout.activity_animations_scene3, this);
scene4 = Scene.getSceneForLayout(sceneRoot, R.layout.activity_animations_scene4, this);

(...)

@Override
public void onClick(View v) {
    switch (v.getId()) {
        case R.id.button1:
            TransitionManager.go(scene1, new ChangeBounds());
            break;
        case R.id.button2:
            TransitionManager.go(scene2, TransitionInflater.from(this).inflateTransition(R.transition.slide_and_changebounds));
            break;
        case R.id.button3:
            TransitionManager.go(scene3, TransitionInflater.from(this).inflateTransition(R.transition.slide_and_changebounds_sequential));
            break;
        case R.id.button4:
            TransitionManager.go(scene4, TransitionInflater.from(this).inflateTransition(R.transition.slide_and_changebounds_sequential_with_interpolators));
            break;
    }
}
```

That code would produce transition between four scenes in the same activity. Each transition has a different animation defined.  **(该代码将在同一活动中的四个场景之间产生转换。 每个转换都有不同的动画定义)**

Transition Framework will take all visible views in current scene and calculate whatever necessary animations are needed to arrange those views according to next scene. **(过渡框架将在当前场景中采取所有可见的视图，并计算根据下一个场景安排这些视图所需的必要动画)**

![scenes_anim]


### Layout changes (view属性变化)

Transition Framework can also be used to animate layout property changes in a view. You just need to make whatever changes you want and it will perform necessary animations for you **(过渡框架也可用于在视图中对布局属性更改进行动画设置。 你只需要做任何你想要的更改，它将为你执行必要的动画)**

#### a) Begin Delayed Transition (延迟动画)

With just this line of code we are telling the framework we are going to perform some UI changes that it will need to animate. **(这一行代码，告诉framework, 我们将要执行一些需要动画化的UI更改)**

```java
TransitionManager.beginDelayedTransition(sceneRoot);
```
#### b) Change view layout properties (改变view的layout属性)


```java
ViewGroup.LayoutParams params = greenIconView.getLayoutParams();
params.width = 200;
greenIconView.setLayoutParams(params);

```

Changing view width attribute to make it smaller will trigger a `layoutMeasure`. At that point the Transition framework will record start and ending values and will create an animation to transition from one to another.
**(改变视图宽度属性使其变小会触发layoutMeasure。 在这一点上，Transition框架将记录开始和结束值，并将创建一个从一个过渡到另一个的动画)**

![view layout animation][view_layout_anim]


## 4. (Bonus) Shared elements + Circular Reveal (共享元素 + 圆形揭示)
Circular Reveal is just an animation to show or hide a group of UI elements. It is available since API 21 in `ViewAnimationUtils` class.  **(圆形揭示只是一个动画来显示或隐藏一组UI元素。 它可以从API 21中的ViewAnimationUtils类获得)**


Circular Reveal animation can be used in combination of Shared Element Transition to create meaningful animations that smoothly teach the user what is happening in the app.

![reveal_shared_anim]

What is happening in this example step by step is:

* Orange circle is a shared element transitioning from `MainActivity` to `RevealActivity`. **(橙色的圆形作为共享元素 from MainActivity to RevealActivity)**
* On `RevealActivity` there is a listener to listen for shared element transition end. When that happens it does two things:
  * Execute a Circular Reveal animation for the Toolbar **(Toolbar执行一个圆形揭示动画)**
  * Execute a scale up animation on `RevealActivity` views using plain old `ViewPropertyAnimator` **(使用老的API: ViewPropertyAnimator, 让RevealActivity的控件执行放大动画)**


> Listen to shared element enter transition end

```java
Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
getWindow().setSharedElementEnterTransition(transition);
transition.addListener(new Transition.TransitionListener() {
    @Override
    public void onTransitionEnd(Transition transition) {
        animateRevealShow(toolbar);
        animateButtonsIn();
    }

    (...)

});

```

> Reveal Toolbar

```java
private void animateRevealShow(View viewRoot) {
    int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
    int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
    int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

    Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
    viewRoot.setVisibility(View.VISIBLE);
    anim.setDuration(1000);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.start();
}
```

> Scale up activity layout views

```java
private void animateButtonsIn() {
    for (int i = 0; i < bgViewGroup.getChildCount(); i++) {
        View child = bgViewGroup.getChildAt(i);
        child.animate()
                .setStartDelay(100 + i * DELAY)
                .setInterpolator(interpolator)
                .alpha(1)
                .scaleX(1)
                .scaleY(1);
    }
}
```

### More circular reveal animations

There are many different ways you can create a reveal animation. The important thing is to use the animation to help the user understand what is happening in the app.

#### Circular Reveal from the middle of target view (目标控件中间开始圆形揭示动画)

![reveal_green]

```java
int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
int cy = viewRoot.getTop();
int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
viewRoot.setBackgroundColor(color);
anim.start();
```

#### Circular Reveal from top of target view + animations (从目标控件上方执行动画 + 子控件执行进入动画)

![reveal_blue]

```java
int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
viewRoot.setBackgroundColor(color);
anim.addListener(new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
        animateButtonsIn();
    }
});
anim.start();
```


#### Circular Reveal from touch point (从点击的地方开始执行动画)

![reveal_yellow]

```java
@Override
public boolean onTouch(View view, MotionEvent motionEvent) {
    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
        if (view.getId() == R.id.square_yellow) {
            revealFromCoordinates(motionEvent.getRawX(), motionEvent.getRawY());
        }
    }
    return false;
}
```

```java 
private Animator animateRevealColorFromCoordinates(int x, int y) {
    float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

    Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
    viewRoot.setBackgroundColor(color);
    anim.start();
}
```       

#### Animate and Reveal

![reveal_red]

```java
Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
transition.addListener(new Transition.TransitionListener() {
    @Override
    public void onTransitionEnd(Transition transition) {
        animateRevealColor(bgViewGroup, R.color.red);
    }
    (...)
   
});
TransitionManager.beginDelayedTransition(bgViewGroup, transition);
RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
btnRed.setLayoutParams(layoutParams);
```         
  

# Sample source code

**[https://github.com/lgvalle/Material-Animations](https://github.com/lgvalle/Material-Animations/)**


# More information

  * Alex Lockwood posts about Transition Framework. A great in deep into this topic: [http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html](http://www.androiddesignpatterns.com/2014/12/activity-fragment-transitions-in-android-lollipop-part1.html)
  * Amazing repository with lot of Material Design samples by Saul Molinero: [https://github.com/saulmm/Android-Material-Examples](https://github.com/saulmm/Android-Material-Examples)
  * Chet Hasse video explaining Transition framework: [https://www.youtube.com/watch?v=S3H7nJ4QaD8](https://www.youtube.com/watch?v=S3H7nJ4QaD8)



[transition-framework]: https://developer.android.com/training/transitions/overview.html

[explode_link]: https://developer.android.com/reference/android/transition/Explode.html
[fade_link]: https://developer.android.com/reference/android/transition/Fade.html
[slide_link]: https://developer.android.com/reference/android/transition/Slide.html

[transition_explode]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/transition_explode.gif
[transition_slide]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/transition_slide.gif
[transition_fade]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/transition_fade.gif
[transition_fade2]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/transition_fade2.gif
[transition_a_to_b]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/transition_A_to_B.png
[transition_b_to_a]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/transition_B_to_A.png

[shared_element]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/shared_element.png
[shared_element_anim]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/shared_element_anim.gif
[shared_element_no_overlap]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/shared_element_no_overlap.gif
[shared_element_overlap]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/shared_element_overlap.gif

[scenes_anim]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/scenes_anim.gif
[view_layout_anim]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/view_layout_anim.gif

[reveal_blue]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/reveal_blue.gif
[reveal_red]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/reveal_red.gif
[reveal_green]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/reveal_green.gif
[reveal_yellow]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/reveal_yellow.gif
[reveal_shared_anim]: https://raw.githubusercontent.com/lgvalle/Material-Animations/master/screenshots/shared_reveal_anim.gif
