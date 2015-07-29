package com.ymttzs.hospital.library;

import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/4/4.
 */
public class MyLinkMoveMethod extends LinkMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);

                    widget.setBackgroundColor(Color.parseColor("#00000000"));

                    Selection.removeSelection(buffer);

                } else if (action == MotionEvent.ACTION_DOWN) {

                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));

                  //  widget.setBackgroundColor(Color.parseColor("#CCCCCC"));

                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        if (action != MotionEvent.ACTION_MOVE) {
            widget.setBackgroundColor(Color.parseColor("#00000000"));
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    private static MyLinkMoveMethod sInstance;

    public static MyLinkMoveMethod getInstance() {
        if (sInstance == null)
            sInstance = new MyLinkMoveMethod();
        return sInstance;
    }
}
