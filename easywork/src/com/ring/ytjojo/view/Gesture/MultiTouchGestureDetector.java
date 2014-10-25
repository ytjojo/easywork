package com.ring.ytjojo.view.Gesture;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
public class MultiTouchGestureDetector {
    @SuppressWarnings("unused")
    private static final String MYTAG = "Ray";
    public static final String CLASS_NAME = "MultiTouchGestureDetector";
    /**
     * 事件信息类 <br/>
     * 用来记录一个手势
     */
    private class EventInfo {
        private MultiMotionEvent mCurrentDownEvent;    //当前的down事件
        private MultiMotionEvent mPreviousUpEvent;    //上一次up事件
        private boolean mStillDown;                    //当前手指是否还在屏幕上
        private boolean mInLongPress;                //当前事件是否属于长按手势
        private boolean mAlwaysInTapRegion;            //是否当前手指仅在小范围内移动，当手指仅在小范围内移动时，视为手指未曾移动过，不会触发onScroll手势
        private boolean mAlwaysInBiggerTapRegion;    //是否当前手指在较大范围内移动，仅当此值为true时，双击手势才能成立
        private boolean mIsDoubleTapping;            //当前手势，是否为双击手势
        private float mLastMotionY;                    //最后一次事件的X坐标
        private float mLastMotionX;                    //最后一次事件的Y坐标
        private EventInfo(MotionEvent e) {
            this(new MultiMotionEvent(e));
        }
        private EventInfo(MultiMotionEvent me) {
            mCurrentDownEvent = me;
            mStillDown = true;
            mInLongPress = false;
            mAlwaysInTapRegion = true;
            mAlwaysInBiggerTapRegion = true;
            mIsDoubleTapping = false;
        }
        //释放MotionEven对象，使系统能够继续使用它们
        public void recycle() {
            if (mCurrentDownEvent != null) {
                mCurrentDownEvent.recycle();
                mCurrentDownEvent = null;
            }
            if (mPreviousUpEvent != null) {
                mPreviousUpEvent.recycle();
                mPreviousUpEvent = null;
            }
        }
        @Override
        public void finalize() {
            this.recycle();
        }
    }
    /**
     * 多点事件类 <br/>
     * 将一个多点事件拆分为多个单点事件，并方便获得事件的绝对坐标
     * <br/> 绝对坐标用以在界面中找到触点所在的控件
     * @author ray-ni
     */
    public class MultiMotionEvent {
        private MotionEvent mEvent;
        private int mIndex;
        private MultiMotionEvent(MotionEvent e) {
            mEvent = e;
            mIndex = (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;  //等效于 mEvent.getActionIndex();
        }
        private MultiMotionEvent(MotionEvent e, int idx) {
            mEvent = e;
            mIndex = idx;
        }
        // 行为
        public int getAction() {
            int action = mEvent.getAction() & MotionEvent.ACTION_MASK;    //等效于 mEvent.getActionMasked();
            switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                action = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = MotionEvent.ACTION_UP;
                break;
            }
            return action;
        }
        // 返回X的绝对坐标
        public float getX() {
            return mEvent.getX(mIndex) + mEvent.getRawX() - mEvent.getX();
        }
        // 返回Y的绝对坐标
        public float getY() {
            return mEvent.getY(mIndex) + mEvent.getRawY() - mEvent.getY();
        }
        // 事件发生的时间
        public long getEventTime() {
            return mEvent.getEventTime();
        }
        // 事件序号
        public int getIndex() {
            return mIndex;
        }
    
        // 事件ID
        public int getId() {
            return mEvent.getPointerId(mIndex);
        }
       
        // 释放事件对象，使系统能够继续使用
        public void recycle() {
            if (mEvent != null) {
                mEvent.recycle();
                mEvent = null;
            }
        }
    }
    // 多点手势监听器
    public interface MultiTouchGestureListener {
        // 手指触碰到屏幕，由一个 ACTION_DOWN触发
        boolean onDown(MultiMotionEvent e);
        // 确定一个press事件，强调手指按下的一段时间（TAP_TIMEOUT）内，手指未曾移动或抬起
        void onShowPress(MultiMotionEvent e);
        // 手指点击屏幕后离开，由 ACTION_UP引发，可以简单的理解为单击事件，即手指点击时间不长（未构成长按事件），也不曾移动过
        boolean onSingleTapUp(MultiMotionEvent e);
        // 长按，手指点下后一段时间（DOUBLE_TAP_TIMEOUT）内，不曾抬起或移动
        void onLongPress(MultiMotionEvent e);
        // 拖动，由ACTION_MOVE触发，手指地按下后，在屏幕上移动
        boolean onScroll(MultiMotionEvent e1, MultiMotionEvent e2, float distanceX, float distanceY);
        // 滑动，由ACTION_UP触发，手指按下并移动一段距离后，抬起时触发
        boolean onFling(MultiMotionEvent e1, MultiMotionEvent e2, float velocityX, float velocityY);
    }
    // 多点双击监听器
    public interface MultiTouchDoubleTapListener {
        // 单击事件确认，强调第一个单击事件发生后，一段时间内，未发生第二次单击事件，即确定不会触发双击事件
        boolean onSingleTapConfirmed(MultiMotionEvent e);
        // 双击事件， 由ACTION_DOWN触发，从第一次单击事件的DOWN事件开始的一段时间(DOUBLE_TAP_TIMEOUT)内结束（即手指），
        // 并且在第一次单击事件的UP时间开始后的一段时间内（DOUBLE_TAP_TIMEOUT）发生第二次单击事件，
        // 除此之外两者坐标间距小于定值（DOUBLE_TAP_SLAP）时，则触发双击事件
        boolean onDoubleTap(MultiMotionEvent e);
        // 双击事件，与onDoubleTap事件不同之处在于，构成双击的第二次点击的ACTION_DOWN,ACTION_MOVE和ACTION_UP都会触发该事件
        boolean onDoubleTapEvent(MultiMotionEvent e);
    }
    // 事件信息队列，队列的下标与MotionEvent的pointId对应
    private static List<EventInfo> sEventInfos = new ArrayList<EventInfo>(10);
    // 双击判断队列，这个队列中的元素等待双击超时的判断结果
    private static List<EventInfo> sEventForDoubleTap = new ArrayList<EventInfo>(5);
    // 指定大点击区域的大小（这个比较拗口），这个值主要用于帮助判断双击是否成立
    private int mBiggerTouchSlopSquare = 20 * 20;
    // 判断是否构成onScroll手势，当手指在这个范围内移动时，不触发onScroll手势
    private int mTouchSlopSquare;
    // 判断是否构成双击，只有两次点击的距离小于该值，才能构成双击手势
    private int mDoubleTapSlopSquare;
    // 最小滑动速度
    private int mMinimumFlingVelocity;
    // 最大滑动速度
    private int mMaximumFlingVelocity;

    // 长按阀值，当手指按下后，在该阀值的时间内，未移动超过mTouchSlopSquare的距离并未抬起，则长按手势触发
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    // showPress手势的触发阀值，当手指按下后，在该阀值的时间内，未移动超过mTouchSlopSquare的距离并未抬起，则showPress手势触发
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    // 双击超时阀值，仅在两次双击事件的间隔（第一次单击的UP事件和第二次单击的DOWN事件）小于此阀值，双击事件才能成立
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    // 双击区域阀值，仅在两次双击事件的距离小于此阀值，双击事件才能成立
    private static final int DOUBLE_TAP_SLAP = 64;

    // GestureHandler所处理的Message的what属性可能为以下 常量：
    // showPress手势
    private static final int SHOW_PRESS = 1;
    // 长按手势
    private static final int LONG_PRESS = 2;
    // SingleTapConfirmed手势
    private static final int TAP_SINGLE = 3;
    // 双击手势
    private static final int TAP_DOUBLE = 4;
    
    // 手势处理器
    private final GestureHandler mHandler;
    // 手势监听器
    private final MultiTouchGestureListener mListener;
    // 双击监听器
    private MultiTouchDoubleTapListener mDoubleTapListener;
    
    // 长按允许阀值
    private boolean mIsLongpressEnabled;
    // 速度追踪器
    private VelocityTracker mVelocityTracker;

    private class GestureHandler extends Handler {
        GestureHandler() {
            super();
        }
        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            int idx = (Integer) msg.obj;
            switch (msg.what) {
            case SHOW_PRESS: {
                if (idx >= sEventInfos.size()) {
//                    Log.w(MYTAG, CLASS_NAME + ":handleMessage, msg.what = SHOW_PRESS, idx=" + idx + ", while sEventInfos.size()="
//                            + sEventInfos.size());
                    break;
                }
                EventInfo info = sEventInfos.get(idx);
                if (info == null) {
//                    Log.e(MYTAG, CLASS_NAME + ":handleMessage, msg.what = SHOW_PRESS, idx=" + idx + ", Info = null");
                    break;
                }
                // 触发手势监听器的onShowPress事件
                mListener.onShowPress(info.mCurrentDownEvent);
                break;
            }
            case LONG_PRESS: {
                // Log.d(MYTAG, CLASS_NAME + ":trigger LONG_PRESS");


                if (idx >= sEventInfos.size()) {
//                    Log.w(MYTAG, CLASS_NAME + ":handleMessage, msg.what = LONG_PRESS, idx=" + idx + ", while sEventInfos.size()="
//                            + sEventInfos.size());
                    break;
                }
                EventInfo info = sEventInfos.get(idx);
                if (info == null) {
//                    Log.e(MYTAG, CLASS_NAME + ":handleMessage, msg.what = LONG_PRESS, idx=" + idx + ", Info = null");
                    break;
                }
                dispatchLongPress(info, idx);
                break;
            }
            case TAP_SINGLE: {
                // Log.d(MYTAG, CLASS_NAME + ":trriger TAP_SINGLE");
                // If the user's finger is still down, do not count it as a tap
                if (idx >= sEventInfos.size()) {
//                    Log.e(MYTAG, CLASS_NAME + ":handleMessage, msg.what = TAP_SINGLE, idx=" + idx + ", while sEventInfos.size()="
//                            + sEventInfos.size());
                    break;
                }
                EventInfo info = sEventInfos.get(idx);
                if (info == null) {
//                    Log.e(MYTAG, CLASS_NAME + ":handleMessage, msg.what = TAP_SINGLE, idx=" + idx + ", Info = null");
                    break;
                }
                if (mDoubleTapListener != null && !info.mStillDown) { //手指在双击超时的阀值内未离开屏幕进行第二次单击事件，则确定单击事件成立（不再触发双击事件）
                    mDoubleTapListener.onSingleTapConfirmed(info.mCurrentDownEvent);
                }
                break;
            }
            case TAP_DOUBLE: {
                if (idx >= sEventForDoubleTap.size()) {
//                    Log.w(MYTAG, CLASS_NAME + ":handleMessage, msg.what = TAP_DOUBLE, idx=" + idx + ", while sEventForDoubleTap.size()="
//                            + sEventForDoubleTap.size());
                    break;
                }
                EventInfo info = sEventForDoubleTap.get(idx);
                if (info == null) {
//                    Log.w(MYTAG, CLASS_NAME + ":handleMessage, msg.what = TAP_DOUBLE, idx=" + idx + ", Info = null");
                    break;
                }
                sEventForDoubleTap.set(idx, null);// 这个没什么好做的，就是把队列中对应的元素清除而已
                break;
            }
            default:
                throw new RuntimeException("Unknown message " + msg); // never
            }
        }
    }
    /**
     * 触发长按事件
     * @param info
     * @param idx
     */
    private void dispatchLongPress(EventInfo info, int idx) {
        mHandler.removeMessages(TAP_SINGLE, idx);//移除单击事件确认
        info.mInLongPress = true;
        mListener.onLongPress(info.mCurrentDownEvent);
    }
    
     /**
     * 构造器1
     * @param context
     * @param listener
     */
    public MultiTouchGestureDetector(Context context, MultiTouchGestureListener listener) {
        this(context, listener, null);
    }
    /**
     * 构造器2
     * @param context
     * @param listener
     * @param handler
     */
    public MultiTouchGestureDetector(Context context, MultiTouchGestureListener listener, Handler handler) {
        if (handler != null) {
            mHandler = new GestureHandler(handler);
        } else {
            mHandler = new GestureHandler();
        }
        mListener = listener;
        if (listener instanceof MultiTouchDoubleTapListener) {
            setOnDoubleTapListener((MultiTouchDoubleTapListener) listener);
        }
        init(context);
    }
    /**
     * 初始化识别器
     * @param context
     */
    private void init(Context context) {
        if (mListener == null) {
            throw new NullPointerException("OnGestureListener must not be null");
        }
        mIsLongpressEnabled = true;
        int touchSlop, doubleTapSlop;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
            doubleTapSlop = DOUBLE_TAP_SLAP;
            mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
        } else {//允许识别器在App中，使用偏好的设定
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            touchSlop = configuration.getScaledTouchSlop();
            doubleTapSlop = configuration.getScaledDoubleTapSlop();
            mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
            mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        }
        mTouchSlopSquare = touchSlop * touchSlop / 16;
        mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }
    /**
     * 设置双击监听器
     * @param onDoubleTapListener
     */
    public void setOnDoubleTapListener(MultiTouchDoubleTapListener onDoubleTapListener) {
        mDoubleTapListener = onDoubleTapListener;
    }
    /**
     * 设置是否允许长按
     * @param isLongpressEnabled
     */
    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        mIsLongpressEnabled = isLongpressEnabled;
    }
    /**
     * 判断是否允许长按
     * @return
     */
    public boolean isLongpressEnabled() {
        return mIsLongpressEnabled;
    }
    /**
     * 判断当前事件是否为双击事件
     * <br/> 通过遍历sEventForDoubleTap来匹配是否存在能够构成双击事件的单击事件
     * @param e
     * @return
     */
    private EventInfo checkForDoubleTap(MultiMotionEvent e) {
        if (sEventForDoubleTap.isEmpty()) {
//            Log.e(MYTAG, CLASS_NAME + ":checkForDoubleTap(), sEventForDoubleTap is empty !");
            return null;
        }
        for (int i = 0; i < sEventForDoubleTap.size(); i++) {
            EventInfo info = sEventForDoubleTap.get(i);
            if (info != null && isConsideredDoubleTap(info, e)) {
                sEventForDoubleTap.set(i, null);// 这个单击事件已经被消耗了，所以置为null
                mHandler.removeMessages(TAP_DOUBLE, i);// 移除Handler内的为处理消息
                return info;
            }
        }
        return null;
    }
    /**
     * 判断当前按下事件是否能和指定的单击事件构成双击事件
     * 
     * @param info
     * @param secondDown
     * @return
     */
    private boolean isConsideredDoubleTap(EventInfo info, MultiMotionEvent secondDown) {
        if (!info.mAlwaysInBiggerTapRegion) { //如多第一次单击事件有过较大距离的移动，则无法构成双击事件
            return false;
        }
        if (secondDown.getEventTime() - info.mPreviousUpEvent.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            //如果第一次单击的UP时间和第二次单击的down时间时间间隔大于DOUBLE_TAP_TIMEOUT，也无法构成双击事件
            return false;
        }
        int deltaX = (int) info.mCurrentDownEvent.getX() - (int) secondDown.getX();
        int deltaY = (int) info.mCurrentDownEvent.getY() - (int) secondDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < mDoubleTapSlopSquare);//最后判断两次单击事件的距离
    }

    /**
     * 将事件信息放入双击判断队列，并返回序号
     * 
     * @param info
     * @return
     */
    private int addIntoTheMinIndex(EventInfo info) {
        for (int i = 0; i < sEventForDoubleTap.size(); i++) {
            if (sEventForDoubleTap.get(i) == null) {
                sEventForDoubleTap.set(i, info);
                return i;
            }
        }
        sEventForDoubleTap.add(info);
        return sEventForDoubleTap.size() - 1;
    }

    /**
     * 从事件信息队列中移除指定序号的事件
     * 
     * @param idx
     */
    private void removeEventFromList(int id) {
        if (id > sEventInfos.size() || id < 0) {
//            Log.e(MYTAG, CLASS_NAME + ".removeEventFromList(), id=" + id + ", while sEventInfos.size() =" + sEventInfos.size());
            return;
        }
        sEventInfos.set(id, null);
    }

    /**
     * 向事件队列中添加新信息
     * 
     * @param e
     */
    private void addEventIntoList(EventInfo info) {
        int id = info.mCurrentDownEvent.getId();
        if (id < sEventInfos.size()) {
//            if (sEventInfos.get(id) != null)
//                Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, info(" + id + ") has not set to null !");
            sEventInfos.set(info.mCurrentDownEvent.getId(), info);
        } else if (id == sEventInfos.size()) {
            sEventInfos.add(info);
        } else {
//            Log.e(MYTAG, CLASS_NAME + ".addEventIntoList, invalidata id !");
        }
    }
    
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);//把所有事件都添加到速度追踪器，为计算速度做准备
        boolean handled = false;
        final int action = ev.getAction(); //获取Action 
//        int idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;//获取触摸事件的序号
        int idx = ev.getPointerId(ev.getActionIndex());//获取触摸事件的id
        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN: {
            EventInfo info = new EventInfo(MotionEvent.obtain(ev));
            this.addEventIntoList(info);//将手势信息保存到队列中
            if (mDoubleTapListener != null) {//如果双击监听器不为null
                if (mHandler.hasMessages(TAP_DOUBLE)) {
                    MultiMotionEvent e = new MultiMotionEvent(ev);
                    EventInfo origInfo = checkForDoubleTap(e);//检查是否构成双击事件
                    if (origInfo != null) {
                        info.mIsDoubleTapping = true;
                        handled |= mDoubleTapListener.onDoubleTap(origInfo.mCurrentDownEvent);
                        handled |= mDoubleTapListener.onDoubleTapEvent(e);
                    }
                }
                if (!info.mIsDoubleTapping) {//当前事件不构成双击事件，那么发送延迟消息以判断onSingleTapConfirmed事件
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(TAP_SINGLE, idx), DOUBLE_TAP_TIMEOUT);
                    // Log.d(MYTAG, CLASS_NAME + ": add TAP_SINGLE");
                }
            }
            // 记录X坐标和Y坐标
            info.mLastMotionX = info.mCurrentDownEvent.getX();
            info.mLastMotionY = info.mCurrentDownEvent.getY();
            
            if (mIsLongpressEnabled) {//允许长按
                mHandler.removeMessages(LONG_PRESS, idx);
                mHandler.sendMessageAtTime(mHandler.obtainMessage(LONG_PRESS, idx), info.mCurrentDownEvent.getEventTime() + TAP_TIMEOUT
                        + LONGPRESS_TIMEOUT);//延时消息以触发长按手势
                // Log.d(MYTAG, CLASS_NAME +
                // ":add LONG_PRESS to handler  for idx " + idx);
            }
            mHandler.sendMessageAtTime(mHandler.obtainMessage(SHOW_PRESS, idx), info.mCurrentDownEvent.getEventTime() + TAP_TIMEOUT);// 延时消息，触发showPress手势
            handled |= mListener.onDown(info.mCurrentDownEvent);//触发onDown（）
            break;
        }
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP: {
            MultiMotionEvent currentUpEvent = new MultiMotionEvent(ev);
            if (idx >= sEventInfos.size()) {
//                Log.e(MYTAG, CLASS_NAME + ":ACTION_POINTER_UP, idx=" + idx + ", while sEventInfos.size()=" + sEventInfos.size());
                break;
            }
            EventInfo info = sEventInfos.get(currentUpEvent.getId());
            if (info == null) {
//                Log.e(MYTAG, CLASS_NAME + ":ACTION_POINTER_UP, idx=" + idx + ", Info = null");
                break;
            }
            info.mStillDown = false;
            if (info.mIsDoubleTapping) { //处于双击状态，则触发onDoubleTapEvent事件
                handled |= mDoubleTapListener.onDoubleTapEvent(currentUpEvent);
            } else if (info.mInLongPress) {//处于长按状态
                mHandler.removeMessages(TAP_SINGLE, idx);//可以无视这行代码
                info.mInLongPress = false;
            } else if (info.mAlwaysInTapRegion) {//尚未移动过
                if (mHandler.hasMessages(TAP_SINGLE, idx)) {//还在双击的时间阀值内，所以要为双击判断做额外处理
                    mHandler.removeMessages(TAP_SINGLE, idx);
                    info.mPreviousUpEvent = new MultiMotionEvent(MotionEvent.obtain(ev));
                    int index = this.addIntoTheMinIndex(info);// 把当前事件放入队列，等待双击的判断
                    mHandler.sendMessageAtTime(mHandler.obtainMessage(TAP_DOUBLE, index), info.mCurrentDownEvent.getEventTime()
                            + DOUBLE_TAP_TIMEOUT); // 将双击超时判断添加到Handler
                    // Log.d(MYTAG, CLASS_NAME + ": add TAP_DOUBLE");
                }
                handled = mListener.onSingleTapUp(currentUpEvent);//触发onSingleTapUp事件
            } else {
                // A fling must travel the minimum tap distance
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);//计算1秒钟内的滑动速度
                //获取X和Y方向的速度
                final float velocityX = velocityTracker.getXVelocity(idx);
                final float velocityY = velocityTracker.getYVelocity(idx);
                // Log.i(MYTAG, CLASS_NAME + ":ACTION_POINTER_UP, idx=" + idx +
                // ", vx=" + velocityX + ", vy=" + velocityY);
                // 触发滑动事件
                if ((Math.abs(velocityY) > mMinimumFlingVelocity) || (Math.abs(velocityX) > mMinimumFlingVelocity)) {
                    handled = mListener.onFling(info.mCurrentDownEvent, currentUpEvent, velocityX, velocityY);
                }
            }
            // Hold the event we obtained above - listeners may have changed the
            // original.
            if (action == MotionEvent.ACTION_UP) {    //释放速度追踪器
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                // Log.w(MYTAG, CLASS_NAME +
                // ":ACTION_POINTER_UP, mVelocityTracker.recycle()");
            }
         
            info.mIsDoubleTapping = false;
            // Log.d(MYTAG, CLASS_NAME + "remove LONG_PRESS");
            // 移除showPress和长按消息
            mHandler.removeMessages(SHOW_PRESS, idx);
            mHandler.removeMessages(LONG_PRESS, idx);
            removeEventFromList(currentUpEvent.getId());//手指离开，则从队列中删除手势信息
            break;
        }
        case MotionEvent.ACTION_MOVE:
            for (int rIdx = 0; rIdx < ev.getPointerCount(); rIdx++) {//因为无法确定当前发生移动的是哪个手指，所以遍历处理所有手指
                MultiMotionEvent e = new MultiMotionEvent(ev, rIdx);
                if (e.getId() >= sEventInfos.size()) {
//                    Log.e(MYTAG, CLASS_NAME + ":ACTION_MOVE, idx=" + rIdx + ", while sEventInfos.size()=" + sEventInfos.size());
                    break;
                }
                EventInfo info = sEventInfos.get(e.getId());
                if (info == null) {
//                    Log.e(MYTAG, CLASS_NAME + ":ACTION_MOVE, idx=" + rIdx + ", Info = null");
                    break;
                }
                if (info.mInLongPress) {    //长按，则不处理move事件
                    break;
                }
                //当前坐标
                float x = e.getX();
                float y = e.getY();
                //距离上次事件移动的位置
                final float scrollX = x - info.mLastMotionX;
                final float scrollY = y - info.mLastMotionY;
                if (info.mIsDoubleTapping) {//双击事件
                    handled |= mDoubleTapListener.onDoubleTapEvent(e);
                } else if (info.mAlwaysInTapRegion) {//该手势尚未移动过（移动的距离小于mTouchSlopSquare,视为未移动过）
                    // 计算从落下到当前事件，移动的距离
                    final int deltaX = (int) (x - info.mCurrentDownEvent.getX());
                    final int deltaY = (int) (y - info.mCurrentDownEvent.getY());
                    // Log.d(MYTAG, CLASS_NAME + "deltaX="+deltaX+";deltaY=" +
                    // deltaX +"mTouchSlopSquare=" + mTouchSlopSquare);
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    if (distance > mTouchSlopSquare) {     // 移动距离超过mTouchSlopSquare
                        handled = mListener.onScroll(info.mCurrentDownEvent, e, scrollX, scrollY);
                        info.mLastMotionX = e.getX();
                        info.mLastMotionY = e.getY();
                        info.mAlwaysInTapRegion = false;
                        // Log.d(MYTAG, CLASS_NAME +
                        // ":remove LONG_PRESS for idx" + rIdx +
                        // ",mTouchSlopSquare("+mTouchSlopSquare+"), distance("+distance+")");
                        // 清除onSingleTapConform，showPress,longPress三种消息
                        int id = e.getId();
                        mHandler.removeMessages(TAP_SINGLE, id);
                        mHandler.removeMessages(SHOW_PRESS, id);
                        mHandler.removeMessages(LONG_PRESS, id);
                    }
                    if (distance > mBiggerTouchSlopSquare) {//移动距离大于mBiggerTouchSlopSquare，则无法构成双击事件
                        info.mAlwaysInBiggerTapRegion = false;
                    }
                } else if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {//之前已经移动过了
                    handled = mListener.onScroll(info.mCurrentDownEvent, e, scrollX, scrollY);
                    info.mLastMotionX = x;
                    info.mLastMotionY = y;
                }
            }
            break;
        case MotionEvent.ACTION_CANCEL:
            cancel();//清理
        }
        return handled;
    }
    // 清理所有队列
    private void cancel() {
        mHandler.removeMessages(SHOW_PRESS);
        mHandler.removeMessages(LONG_PRESS);
        mHandler.removeMessages(TAP_SINGLE);
        mVelocityTracker.recycle();
        mVelocityTracker = null;
        sEventInfos.clear();
        sEventForDoubleTap.clear();
    }
}