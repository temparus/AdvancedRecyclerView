package ch.temparus.android.advancedrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * LinearLayoutManager with support for WRAP_CONTENT and another implementation of padding.
 *
 * The padding will only be applied to the items, but not to the scroll view, so that the edge effect will be
 * the full length of the RecyclerView.
 *
 * Known issue: If your are using header views and the RecyclerView has a height of WRAP_CONTENT,
 *              the list starts with the first content item. If you call "adapter.notifyDatasetHasChanged()",
 *              the list behaves as expected.
 *              This error is caused whenever recycler.getViewForPosition(position) is called.
 *              See method measureChild().
 *              If you have an idea how to resolve this issue, please drop me a line or even a pull request. Thank you.
 *
 * @author Sandro Lutz
 */
public class LinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {

    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;

    private Dimension mChildDimension = new Dimension(0, 0);
    private Integer mChildSize;

    private RecyclerView.Adapter mAdapter;

    @SuppressWarnings("unused")
    public LinearLayoutManager(Context context) {
        super(context);
    }

    /**
     * Constructor.
     *
     * Note: This constructor is part of the workaround for the known issue with the header views and
     *       is only available until this issue has been resolved.
     *       Any advice or even a pull request is very appreciated.
     * @param context Context of the current Activity / Application
     * @param adapter Adapter of the connected RecyclerView
     */
    @SuppressWarnings("unused")
    public LinearLayoutManager(Context context, RecyclerView.Adapter adapter) {
        super(context);

        mAdapter = adapter;
    }

    @SuppressWarnings("unused")
    public LinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
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

    /**
     * This is just for the workaround of the known issue with the header view.
     * For more information see {@link LinearLayoutManager}
     * @param adapter adapter of the connected RecyclerView
     */
    @SuppressWarnings("unused")
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    /**
     * Set the child size if you use WRAP_CONTENT and the size of the items displayed in the
     * RecyclerView is known and equal for every item.
     *
     * Note: If the child size is not set or set to null, all items will be measured to evaluate
     *       the total sizeof the RecyclerView.
     * @param childSize the size of the child views / adapter items
     */
    @SuppressWarnings("unused")
    public void setChildSize(Integer childSize) {
        if (!mChildSize.equals(childSize)) {
            mChildSize = childSize;
            requestLayout();
        }
    }

    /**
     * Clears the child size.
     */
    @SuppressWarnings("unused")
    public void clearChildSize() {
        setChildSize(null);
    }

    @Override
    public void setOrientation(int orientation) {
        if (mChildDimension != null) {
            if (getOrientation() != orientation) {
                mChildDimension.width = 0;
                mChildDimension.height = 0;
            }
        }
        super.setOrientation(orientation);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);

        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        final boolean exactWidth = widthMode == View.MeasureSpec.EXACTLY;
        final boolean exactHeight = heightMode == View.MeasureSpec.EXACTLY;

        final int unspecified = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        if (exactWidth && exactHeight) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return;
        }

        final boolean vertical = getOrientation() == VERTICAL;

        initChildDimensions(widthSize, heightSize, vertical);

        int width = 0;
        int height = 0;

        recycler.clear();

        final int stateItemCount = state.getItemCount();
        final int adapterItemCount = getItemCount();
        // adapter always contains actual data while state might contain old data.
        // As we want to measure the view with actual data we must use data from the adapter and not from  the state
        for (int i = 0; i < adapterItemCount; i++) {
            if (vertical) {
                if (mChildSize == null) {
                    if (i < stateItemCount) {
                        // we should not exceed state count, otherwise we'll get IndexOutOfBoundsException.
                        // For such items we will use previously calculated dimensions
                        measureChild(recycler, i, widthSpec, unspecified, mChildDimension);
                    }
                }
                height += mChildDimension.height;
                if (i == 0) {
                    width = mChildDimension.width;
                }
                if (height >= heightSize) {
                    break;
                }
            } else {
                if (mChildSize == null) {
                    if (i < stateItemCount) {
                        // we should not exceed state count, otherwise we'll get IndexOutOfBoundsException.
                        // For such items we will use previously calculated dimensions
                        measureChild(recycler, i, unspecified, heightSpec, mChildDimension);
                    }
                }
                width += mChildDimension.width;
                if (i == 0) {
                    height = mChildDimension.height;
                }
                if (width >= widthSize) {
                    break;
                }
            }
        }

        if ((vertical && height < heightSize) || (!vertical && width < widthSize)) {
            if (exactWidth) {
                width = widthSize;
            } else {
                width += getPaddingLeft() + getPaddingRight();
            }

            if (exactHeight) {
                height = heightSize;
            } else {
                height += getPaddingTop() + getPaddingBottom();
            }

            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initChildDimensions(int width, int height, boolean vertical) {
        if (mChildDimension.width != 0 || mChildDimension.height != 0) {
            // already initialized, skipping
            return;
        }
        if (vertical) {
            mChildDimension.width = width;
            mChildDimension.height = 0;
        } else {
            mChildDimension.width = 0;
            mChildDimension.height = height;
        }
    }

    private void measureChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, Dimension dimension) {
        final View child = recycler.getViewForPosition(position);

        final RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();

        final int hPadding = getPaddingLeft() + getPaddingRight();
        final int vPadding = getPaddingTop() + getPaddingBottom();

        final int hMargin = p.leftMargin + p.rightMargin;
        final int vMargin = p.topMargin + p.bottomMargin;

        final int hDecoration = getRightDecorationWidth(child) + getLeftDecorationWidth(child);
        final int vDecoration = getTopDecorationHeight(child) + getBottomDecorationHeight(child);

        final int childWidthSpec = getChildMeasureSpec(widthSpec, hPadding + hMargin + hDecoration, p.width, canScrollHorizontally());
        final int childHeightSpec = getChildMeasureSpec(heightSpec, vPadding + vMargin + vDecoration, p.height, canScrollVertically());

        child.measure(childWidthSpec, childHeightSpec);

        Rect rect = new Rect();
        calculateItemDecorationsForChild(child, rect);

        dimension.width = rect.width() + child.getMeasuredWidth() + p.leftMargin + p.rightMargin;
        dimension.height = rect.height() + child.getMeasuredHeight() + p.bottomMargin + p.topMargin;

        child.invalidate();
        recycler.recycleView(child);
    }

    /**
     * Inner class for holding the dimension of a child view.
     */
    private class Dimension {

        private int width;
        private int height;

        public Dimension(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
