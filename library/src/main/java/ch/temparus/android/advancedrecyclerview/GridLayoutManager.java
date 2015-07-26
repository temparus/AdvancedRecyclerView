package ch.temparus.android.advancedrecyclerview;

import android.content.Context;

/**
 * GridLayoutManager with another implementation of padding.
 *
 * The padding will only be applied to the items, but not to the scroll view, so that the edge effect will be
 * the full length of the RecyclerView.
 *
 * @author Sandro Lutz
 */
public class GridLayoutManager extends android.support.v7.widget.GridLayoutManager {

    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;

    public GridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    /**
     * Sets the padding.
     * @param left   the left padding in pixels
     * @param top    the top padding in pixels
     * @param right  the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    @SuppressWarnings("unused")
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
    }

    @Override
    public int getPaddingTop() {
        return super.getPaddingTop() + mPaddingTop;
    }

    @Override
    public int getPaddingBottom() {
        return super.getPaddingBottom() + mPaddingBottom;
    }

    @Override
    public int getPaddingLeft() {
        return super.getPaddingLeft() + mPaddingLeft;
    }

    @Override
    public int getPaddingRight() {
        return super.getPaddingRight() + mPaddingRight;
    }
}
