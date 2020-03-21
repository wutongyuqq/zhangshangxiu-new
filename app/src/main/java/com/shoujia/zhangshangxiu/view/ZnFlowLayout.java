package com.shoujia.zhangshangxiu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.util.Util;

import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2018, PING AN TECHNOLOGIES CO., LTD.
 * SubjectOptionItemView
 * <p>
 * Description
 *
 * @author WANGDAOYUN909
 * @version 1.0
 * <p>
 * Ver 1.0, 2018/9/27, WANGDAOYUN909, Create file
 */
public class ZnFlowLayout extends ViewGroup implements View.OnClickListener {

    /**
     * 存储行的集合，管理行
     */
    private List<Line> mLines = new ArrayList<>();
    /**
     * 水平和竖直的间距
     */
    private float vertical_space;
    private float horizontal_space;
    /**
     * 每一行标签的个数
     */
    private int item_num;
    /**
     * 每一行自适配调整剩余空间 true：适配调整剩余行空间，false：行间距固定
     */
    private boolean fit_space = true;
    /**
     * 每一个tag标签背景的颜色
     */
    private int tag_background = -1;
    /**
     * 标签里面的字体颜色
     */
    private int tag_text_color = -1;
    /**
     * 标签的样式
     */
    private int tag_style = TagStyle.NORMAL;
    /**
     * 当前行的指针
     */
    private Line mCurrentLine;
    /**
     * 行的最大宽度，除去边距的宽度
     */
    private int mMaxWidth;
    private float tag_text_size;
    private @LayoutRes int mItemLayoutId = R.layout.view_grid_item;

    private OnTagItemClickListener mClickListener;
    private OnItemSelectListener mItemSelectListener;

    private List<String> mDatas;
    private List<String> mSelectedList=new ArrayList<>();

    private Context mContext;

    public ZnFlowLayout(Context context) {
        this(context, null);
    }

    public ZnFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZnFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        // 获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZnFlowLayout);
        horizontal_space = array.getDimension(R.styleable.ZnFlowLayout_hor_space, 0);
        vertical_space = array.getDimension(R.styleable.ZnFlowLayout_ver_space, 0);
        item_num = array.getInt(R.styleable.ZnFlowLayout_line_item_num, 0);
        fit_space = array.getBoolean(R.styleable.ZnFlowLayout_fit_space, true);
        tag_background = array.getResourceId(R.styleable.ZnFlowLayout_flow_tag_background, 0);
        tag_text_color = array.getResourceId(R.styleable.ZnFlowLayout_flow_tag_text_color, 0);
        tag_style = array.getInt(R.styleable.ZnFlowLayout_flow_style, TagStyle.NORMAL);
        tag_text_size = array.getDimension(R.styleable.ZnFlowLayout_flow_tag_text_size, 0);
        mItemLayoutId = array.getResourceId(R.styleable.ZnFlowLayout_flow_tag_layout, R.layout.view_grid_item);
        array.recycle();
    }

    public void setOnItemSelectListener(OnTagItemClickListener selectListener) {
        mClickListener = selectListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.mItemSelectListener = onItemSelectListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 每次测量之前都先清空集合，不让会覆盖掉以前
        mLines.clear();
        mCurrentLine = null;

        // 获取总宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 计算最大的宽度
        mMaxWidth = width - getPaddingLeft() - getPaddingRight();

        // ******************** 测量孩子 ********************
        // 遍历获取孩子
        int childCount = this.getChildCount();

        if (item_num == 0) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.setTag(i);
                childView.setOnClickListener(this);
                // 测量孩子
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);

                // 测量完需要将孩子添加到管理行的孩子的集合中，将行添加到管理行的集合中
                if (mCurrentLine == null) {
                    // 初次添加第一个孩子的时候
                    mCurrentLine = new Line(mMaxWidth, horizontal_space);

                    if (mCurrentLine.canAddView(childView)) {
                        // 添加孩子
                        mCurrentLine.addView(childView);
                    }
                    // 添加行
                    mLines.add(mCurrentLine);

                } else {
                    // 行中有孩子的时候，判断时候能添加
                    if (mCurrentLine.canAddView(childView)) {
                        // 继续往该行里添加
                        mCurrentLine.addView(childView);
                    } else {
                        //  添加到下一行
                        mCurrentLine = new Line(mMaxWidth, horizontal_space);
                        mCurrentLine.addView(childView);
                        mLines.add(mCurrentLine);
                    }
                }
            }
        } else {
            Line line = null;
            for (int i = 0; i < childCount; i++) {
                if (i % item_num == 0) {
                    line = new Line(mMaxWidth, horizontal_space);
                    mLines.add(line);
                }
                View childView = getChildAt(i);
                childView.setTag(i);
                childView.setOnClickListener(this);
                // 测量孩子
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                if (null != line) {
                    line.addView(childView);
                }
            }
        }


        // ******************** 测量自己 *********************
        // 测量自己只需要计算高度，宽度肯定会被填充满的
        int height = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < mLines.size(); i++) {
            // 所有行的高度
            height += mLines.get(i).height;
        }
        // 所有竖直的间距
        height += (mLines.size() - 1) * vertical_space;

        // 测量
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 这里只负责高度的位置，具体的宽度和子孩子的位置让具体的行去管理
        l = getPaddingLeft();
        t = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            // 获取行
            Line line = mLines.get(i);
            // 管理
            line.layout(t, l);

            // 更新高度
            t += line.height;
            if (i != mLines.size() - 1) {
                // 不是最后一条就添加间距
                t += vertical_space;
            }
        }
    }

    public void setData(List<String> data) {
        if (data==null||data.size()==0) {
            return;
        }
        this.mDatas = data;
        addViewWithData();
    }

    @Override
    public void setEnabled(boolean enabled) {
        int count = getChildCount();

    }

    private void addViewWithData() {
        if (mDatas==null||mDatas.size()==0) {
            return;
        }
        removeAllViews();
        for (String data : mDatas) {
            if (StringUtils.isEmpty(data)) {
                continue;
            }
            RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(getItemLayoutResId(), null);
            TextView tabView = itemView.findViewById(R.id.tv_item);
            ImageView tv_fold_img = itemView.findViewById(R.id.tv_fold_img);
            if(data.equals("返回")){
                tv_fold_img.setImageDrawable(getResources().getDrawable(R.drawable.fold_back_img));
            }else{
                tv_fold_img.setImageDrawable(getResources().getDrawable(R.drawable.file_img));
            }

            if (tag_background > 0) {
                itemView.setBackgroundResource(tag_background);
            }
            if (tag_text_color > 0) {
                tabView.setTextColor(ContextCompat.getColorStateList(getContext(), tag_text_color));
            }

            if (tag_text_size > 0){
                tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tag_text_size);
            }
            tabView.setText(data);
            if (itemWidth != -1) {
                ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
                layoutParams.width = Util.dp2px(mContext, itemWidth);
                tabView.setLayoutParams(layoutParams);
            }
            addView(itemView);
        }
    }

    private int itemWidth = -1;
    /**
     * 设置每个item的宽度 dp
     * @param width
     */
    public void setItemWidth(int width) {
        this.itemWidth = width;
    }

    private @LayoutRes int getItemLayoutResId(){
        return mItemLayoutId;
    }

    @Override
    public void onClick(View view) {
        if (null == view) {
            return;
        }
        int pos = (Integer) view.getTag();
        view.setSelected(!view.isSelected());
        String content = mDatas.get(pos);
        if (content != null) {
            if (view.isSelected() && !mSelectedList.contains(content)) {
                mSelectedList.add(content);
            } else if (!view.isSelected() && mSelectedList.contains(content)) {
                mSelectedList.remove(content);
            }
        }
        if (TagStyle.NORMAL == tag_style) {
            if (null == mClickListener) {
                return;
            }
            mClickListener.onItemClick(pos, view, mDatas.get(pos));
        } else if (TagStyle.SINGLE == tag_style) {
            if (null == mItemSelectListener) {
                return;
            }

            //如果是单选模式，当点击view时，应将view以外的其他tag设置成没有选中状态。
            if (getChildCount() <= 0) {
                return;
            }
            for (int i = 0; i < getChildCount(); i++) {
                if (i == pos) {
                    continue;
                }
                getChildAt(i).setSelected(false);
            }
            mItemSelectListener.onItemSelect(pos, view, mDatas.get(pos), view.isSelected());
        } else if (TagStyle.MULTI == tag_style) {
            //如果是多选模式，暂时不用做任何操作
            if (null == mItemSelectListener) {
                return;
            }
            mItemSelectListener.onItemSelect(pos, view, mDatas.get(pos), view.isSelected());
        }
    }

    public int getLineNum() {
        return mLines==null ? 0 : mLines.size();
    }

    public int getLineHeight(int lineNum) {
        if (mLines==null||mLines.size()==0 || lineNum <= 0) {
            return 0;
        }
        int height = (int) (lineNum * (mLines.get(0).height + vertical_space) - vertical_space);
        return height;
    }

    /*
    *
    * 获取某一行的子元素个数
    * */
    public int getItemNum(int lineIndex){

        if(lineIndex<mLines.size()){
            if(mLines==null||mLines.get(lineIndex)==null||mLines.get(lineIndex).views==null){
                return 0;
            }
            return mLines.get(lineIndex).views.size();
        }
        return 0;
    }

    /*
    *
    * 设置某个元素是否点击
    * */
    public void setItemClickable(int index,boolean isEnable){
            View view = getChildAt(index);
            if(view!=null){
                view.setClickable(isEnable);
            }
    }


    public List<Line> getLines(){
        return mLines;
    }

    /**
     * 获取当前选中的元素
     * @return
     */
    public List<String> getSelectData() {
        List<String> data = new ArrayList<>();
        int count = getChildCount();
        if (count <= 0 || mDatas==null||mDatas.size()==0) {
            return data;
        }
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.isSelected()) {
                String itemData = mDatas.get(i);
                if (StringUtils.isEmpty(itemData)) {
                    continue;
                }
                data.add(itemData);
            }
        }
        return data;
    }

    public void setAllSelect(boolean isSelect){
        int count = getChildCount();
        if (count <= 0 || mDatas==null||mDatas.size()==0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            view.setSelected(isSelect);
        }

    }

    public boolean isAllSelected(){
        int count = getChildCount();
        if (count <= 0 || mDatas==null||mDatas.size()==0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if(!view.isSelected()){
                return false;
            }
        }
        return true;

    }

    /**
     *
     * @return 获取选中元素按点击顺序排列
     */
    public List<String> getSelectOrderData(){
        return mSelectedList;
    }

    /**
     * 内部类，行管理器，管理每一行的孩子
     */
    public class Line {
        /**
         * 定义一个行的集合来存放子View
         */
        private List<View> views = new ArrayList<>();
        /**
         * 行的最大宽度
         */
        private int maxWidth;
        /**
         * 行中已经使用的宽度
         */
        private int usedWidth;
        /**
         * 行的高度
         */
        private int height;
        /**
         * 孩子之间的距离
         */
        private float space;

        /**
         * 通过构造初始化最大宽度和边距
         */
        Line(int maxWidth, float horizontalSpace) {
            this.maxWidth = maxWidth;
            this.space = horizontalSpace;
        }

        /**
         * 通过构造初始化最大宽度和边距
         */
        public Line(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        public void setHorizontalSpace(float space) {
            this.space = space;
        }

        /**
         * 往集合里添加孩子
         */
        public void addView(View view) {
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            // 更新行的使用宽度和高度
            if (views.size() == 0) {
                // 集合里没有孩子的时候
                if (childWidth > maxWidth) {
                    usedWidth = maxWidth;
                    height = childHeight;
                } else {
                    usedWidth = childWidth;
                    height = childHeight;
                }
            } else {
                usedWidth += childWidth + space;
                height = childHeight > height ? childHeight : height;
            }

            // 添加孩子到集合
            views.add(view);
        }


        /**
         * 判断当前的行是否能添加孩子
         *
         * @return
         */
        public boolean canAddView(View view) {
            // 集合里没有数据可以添加
            if (views.size() == 0) {
                return true;
            }

            // 最后一个孩子的宽度大于剩余宽度就不添加
            if (view.getMeasuredWidth() > (maxWidth - usedWidth - space)) {
                return false;
            }

            // 默认可以添加
            return true;
        }

        /**
         * 指定孩子显示的位置
         *
         * @param t
         * @param l
         */
        public void layout(int t, int l) {
            // 平分剩下的空间
            int avg = (maxWidth - usedWidth) / views.size();
            float childWidth = avg;
            if (item_num > 0) {
                childWidth = (maxWidth - horizontal_space * (item_num - 1)) / item_num;
            }

            // 循环指定孩子位置
            for (View view : views) {
                // 获取宽高
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                // 重新测量
                if (item_num > 0) {
                    view.measure(MeasureSpec.makeMeasureSpec((int) childWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                } else {
                    if (fit_space) {
                        view.measure(MeasureSpec.makeMeasureSpec(measuredWidth + avg, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                    } else {
                        view.measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                    }
                }
                // 重新获取宽度值
                measuredWidth = view.getMeasuredWidth();


                int top = t;
                int left = l;
                int right = measuredWidth + left;
                int bottom = measuredHeight + top;
                // 指定位置
                view.layout(left, top, right, bottom);

                // 更新数据
                l += measuredWidth + space;
            }
        }
    }

    public interface OnTagItemClickListener {

        /**
         * 选择答案的回调
         *
         * @param pos  选项位置
         * @param view View
         */
        void onItemClick(int pos, View view, String data);
    }

    /**
     * 单选模式和多选模式时，使用该监听
     */
    public interface OnItemSelectListener {

        /**
         *
         * @param pos index
         * @param view View
         * @param data data
         * @param isSelect 状态
         */
        void onItemSelect(int pos, View view, String data, boolean isSelect);
    }



    /**
     * 标签布局的样式
     */
    public interface TagStyle {
        /**普通样式*/
        int NORMAL = 0;
        /**单选样式*/
        int SINGLE = 1;
        /**多选样式*/
        int MULTI = 2;
    }


}
