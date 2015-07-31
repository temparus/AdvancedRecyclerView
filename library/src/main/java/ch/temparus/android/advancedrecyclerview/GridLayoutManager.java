package ch.temparus.android.advancedrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * GridLayoutManager with another implementation of padding.
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
public class GridLayoutManager extends android.support.v7.widget.GridLayoutManager {

    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;

    private Dimension mChildDimension = new Dimension(0, 0);

    private RecyclerView.Adapter mAdapter;

    public GridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
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
    public GridLayoutManager(Context context, int spanCount, RecyclerView.Adapter adapter) {
        super(context, spanCount);

        mAdapter = adapter;
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

        int width = 0;
        int height = 0;

        recycler.clear();

        final int stateItemCount = state.getItemCount();
        final int adapterItemCount = getItemCount();
        final int spanCount = getSpanCount();
        SpanSizeLookup spanSizeLookup = getSpanSizeLookup();
        int currentSpan = 0;
        int spanSize;

        // evaluate first row/column size, depending on orientation.
        for (int i = 0; i < adapterItemCount; i++) {
            spanSize = spanSizeLookup.getSpanSize(i);
            if (vertical) {
                if (currentSpan + spanSize > spanCount) {
                    break;
                }
                if (i < stateItemCount) {
                    // we should not exceed state count, otherwise we'll get IndexOutOfBoundsException.
                    // For such items we will use previously calculated dimensions
                    measureChild(recycler, i, View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.AT_MOST), unspecified, mChildDimension);
                }
                width += mChildDimension.width;
                if (width >= widthSize) {
                    width = widthSize;
                    break;
                }
            } else {
                if (currentSpan + spanSize > spanCount) {
                    break;
                }
                if (i < stateItemCount) {
                    // we should not exceed state count, otherwise we'll get IndexOutOfBoundsException.
                    // For such items we will use previously calculated dimensions
                    measureChild(recycler, i, unspecified, View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.AT_MOST), mChildDimension);
                }
                height += mChildDimension.height;
                if (height >= heightSize) {
                    height = heightSize;
                    break;
                }
            }
            currentSpan += spanSize;
        }

        if (vertical) {
            width = (width < 5) ? widthSize : width;
        } else {
            height = (height < 5) ? heightSize : height;
        }

        final int singleSpanDimension = vertical ? widthSize/spanCount : heightSize/spanCount;
        int rowSize = 0;
        currentSpan = 0;

        // calculate the other content dimension
        for (int i = 0; i < adapterItemCount; i++) {
            spanSize = spanSizeLookup.getSpanSize(i);
            if (vertical) {
                if (currentSpan + spanSize > spanCount) {
                    height += rowSize;
                    rowSize = 0;
                    currentSpan = 0;
                }
                if (i < stateItemCount) {
                    // we should not exceed state count, otherwise we'll get IndexOutOfBoundsException.
                    // For such items we will use previously calculated dimensions
                    measureChild(recycler, i, View.MeasureSpec.makeMeasureSpec(singleSpanDimension * spanSize, View.MeasureSpec.EXACTLY), unspecified, mChildDimension);
                }

                rowSize = (mChildDimension.height > rowSize) ? mChildDimension.height : rowSize;
                if (height >= heightSize) {
                    break;
                }
            } else {
                if (currentSpan + spanSize > spanCount) {
                    width += rowSize;
                    rowSize = 0;
                    currentSpan = 0;
                }
                if (i < stateItemCount) {
                    // we should not exceed state count, otherwise we'll get IndexOutOfBoundsException.
                    // For such items we will use previously calculated dimensions
                    measureChild(recycler, i, unspecified, heightSpec, mChildDimension);
                }
                rowSize = (mChildDimension.height > rowSize) ? mChildDimension.height : rowSize;
                if (width >= widthSize) {
                    break;
                }
            }
            currentSpan += spanSize;
        }

        if (vertical) {
            // add last row
            height += rowSize;
            if (height > heightSize) {
                height = heightSize;
            }
        } else {
            // add last column
            width += rowSize;
            if (width > widthSize) {
                width = widthSize;
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
