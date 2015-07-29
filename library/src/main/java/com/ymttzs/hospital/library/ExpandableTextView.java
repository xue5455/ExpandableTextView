package com.ymttzs.hospital.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/7/28 0028.
 */
public class ExpandableTextView extends TextView {
    private float lineSpacingMultiplier = 1.0f;
    private float lineAdditionalVerticalPadding = 0.0f;

    private String mFullText;
    private String COLLAPSE_TEXT;
    private String FOLD_TEXT;
    private int mAdditionalTextColor;
    public static final String DEFAULT_COLLAPSE_TEXT = "展开";
    public static final String DEFAULT_FOLD_TEXT = "收起";
    public static final int DEFAULT_ADDITIONAL_TEXT_COLOR = Color.BLACK;

    public static final int DEFAULT_MAX_LINES = -1;

    private boolean mUnderlined = true;//是否有下划线
    private boolean mSelfChanged = false;//if the text is changed by myself,then false ,or true;
    private boolean mIsStale = true;
    private boolean mCollapsed = false;
    private int mMaxLines;

    private ClickableSpan mClickableSpan;

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);
        setMovementMethod(MyLinkMoveMethod.getInstance());
        mClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mCollapsed = !mCollapsed;
                if (mCollapsed) {
                    try {
                        mSelfChanged = true;
                        setMaxLines(Integer.MAX_VALUE);
                    } finally {
                        mSelfChanged = false;
                    }
                } else {
                    try {
                        mSelfChanged = true;
                        setMaxLines(mMaxLines);
                    } finally {
                        mSelfChanged = false;
                    }
                }
                mIsStale = true;
            }

            /**
             * Makes the text underlined and in the link color.
             */
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(mAdditionalTextColor);
                ds.setUnderlineText(mUnderlined);
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (mIsStale) {
            resetText();
        }
        super.onDraw(canvas);
    }

    /**
     * 初始化各个参数
     *
     * @param context
     * @param attrs
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ExpandableTextView);
        COLLAPSE_TEXT = attr.getString(R.styleable.ExpandableTextView_collapse_text);
        if (COLLAPSE_TEXT == null)
            COLLAPSE_TEXT = DEFAULT_COLLAPSE_TEXT;
        FOLD_TEXT = attr.getString(R.styleable.ExpandableTextView_fold_text);
        if (FOLD_TEXT == null)
            FOLD_TEXT = DEFAULT_FOLD_TEXT;
        mAdditionalTextColor = attr.getColor(R.styleable.ExpandableTextView_additional_text_color, DEFAULT_ADDITIONAL_TEXT_COLOR);
        mMaxLines = attr.getInt(R.styleable.ExpandableTextView_max_lines, DEFAULT_MAX_LINES);
        mUnderlined = attr.getBoolean(R.styleable.ExpandableTextView_underlined, true);
        attr.recycle();
        if (mMaxLines != -1)
            setMaxLines(mMaxLines);
    }

    protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        if (!mSelfChanged)
            mMaxLines = maxLines;
    }

    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!mSelfChanged) {
            mFullText = text.toString();
            mIsStale = true;
        }
    }

    private void resetText() {
        if (!mCollapsed) {
            final int maxLines = getMaxLines();
            if (maxLines != -1) {
                String workingText = mFullText;
                Layout layout = createWorkingLayout(workingText.toString());
                if (layout.getLineCount() > mMaxLines) {
                    workingText = mFullText.substring(0, layout.getLineEnd(mMaxLines - 1));//获得maxlines的文本
                    workingText = workingText.substring(0, workingText.length() - COLLAPSE_TEXT.length());

                    try {
                        mSelfChanged = true;
                        setText(createSpannableString(workingText, COLLAPSE_TEXT));
                    } finally {
                        mSelfChanged = false;
                    }
                    mIsStale = false;
                }
            }
        } else {
            String workingText = mFullText;
            try {
                mSelfChanged = true;
                setText(createSpannableString(workingText, FOLD_TEXT));
            } finally {
                mSelfChanged = false;
            }
            mIsStale = false;

        }
    }


    private SpannableString createSpannableString(String workingText, String additionalText) {
        StringBuilder stringBuilder = new StringBuilder(workingText);
        int start = stringBuilder.length();
        int end = start + additionalText.length();
        stringBuilder.append(additionalText);
        SpannableString spannableString = new SpannableString(stringBuilder);
        spannableString.setSpan(mClickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private Layout createWorkingLayout(String workingText) {
        return new StaticLayout(workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        this.lineAdditionalVerticalPadding = add;
        this.lineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }
}
