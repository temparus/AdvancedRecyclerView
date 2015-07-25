package ch.temparus.advancedrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * This ItemDecoration class provides basic item decorations for the GridLayoutManage of this library.
 *
 * @author Sandro Lutz
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;
    private int mLeft;
    private int mRight;

    /**
     * @param spacing spacing between items in pixels
     */
    public GridItemDecoration(int spacing) {
        this(spacing, spacing, spacing);
    }

    /**
     * @param spacing spacing between items in pixels
     * @param left    left padding in pixels
     * @param right   right padding in pixels
     */
    public GridItemDecoration(int spacing, int left, int right) {
        mSpacing = spacing;
        mLeft = left;
        mRight = right;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        BaseAdapter adapter = (BaseAdapter) parent.getAdapter();

        int childCount = adapter.getContentItemCount();
        int headerCount = adapter.getHeaderCount();
        int childIndex = parent.getChildLayoutPosition(view);
        int spanCount = getTotalSpan(parent);
        int halfSpacing = mSpacing / 2;

        // invalid spanCount
        if (spanCount < 1) return;

        if (childIndex < headerCount || childCount == 0 && childIndex == headerCount) {
            outRect.top = mSpacing;
            outRect.bottom = halfSpacing;
            outRect.left = mLeft;
            outRect.right = mRight;
            return;
        }

        childIndex -= adapter.getHeaderCount();

        int spanIndex = childIndex % spanCount;


        outRect.top = halfSpacing;
        outRect.bottom = halfSpacing;
        outRect.left = halfSpacing;
        outRect.right = halfSpacing;

        if (isTopEdge(childIndex, spanCount)) {
            outRect.top = mSpacing;
        }

        if (isLeftEdge(spanIndex)) {
            outRect.left = mLeft;
        }

        if (isRightEdge(spanIndex, spanCount)) {
            outRect.right = mRight;
        }

        if (isBottomEdge(childIndex, childCount, spanCount)) {
            outRect.bottom = mSpacing;
        }
    }

    protected int getTotalSpan(RecyclerView parent) {

        RecyclerView.LayoutManager mgr = parent.getLayoutManager();
        if (mgr instanceof GridLayoutManager) {
            return ((GridLayoutManager) mgr).getSpanCount();
        } else if (mgr instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) mgr).getSpanCount();
        }

        return -1;
    }

    protected boolean isLeftEdge(int spanIndex) {

        return spanIndex == 0;
    }

    protected boolean isRightEdge(int spanIndex, int spanCount) {

        return spanIndex == spanCount - 1;
    }

    protected boolean isTopEdge(int childIndex, int spanCount) {

        return childIndex < spanCount;
    }

    protected boolean isBottomEdge(int childIndex, int childCount, int spanCount) {

        return childIndex >= childCount - spanCount;
    }
}