package ch.temparus.android.advancedrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.*;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * AdvancedRecyclerView is an extended RecyclerView with the addition of headers at the top of the scrolling area.
 *
 * Note: Header views take always the full width of the parent even if you use GridLayoutManager.
 *
 * @author Sandro Lutz
 */
public class AdvancedRecyclerView extends RecyclerView {

    final private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    private int mOverScrollMode = -1;
    private View mEmptyView;
    private View.OnTouchListener mOnInterceptTouchListener;

    public AdvancedRecyclerView(Context context) {
        this(context, null);
    }

    public AdvancedRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvancedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.overScrollMode });
        mOverScrollMode = ta.getInt(0, OVER_SCROLL_ALWAYS);
        ta.recycle();
    }

    @Override
    public void setLayoutManager(LayoutManager manager) {
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    Adapter adapter = getAdapter();
                    if (adapter instanceof AdvancedRecyclerAdapter) {
                        AdvancedRecyclerAdapter headerAdapter = (AdvancedRecyclerAdapter) adapter;
                        if (position < headerAdapter.getHeaderCount() || (headerAdapter.getContentItemCount() == 0 && position == headerAdapter.getHeaderCount())) {
                            return gridManager.getSpanCount();
                        }
                    }
                    return 1;
                }
            });
            super.setLayoutManager(gridManager);
        } else {
            super.setLayoutManager(manager);
        }
    }

    /**
     * Set OnInterceptTouchListener for advanced usage of this view.
     * @param onInterceptTouchListener OnTouchListener
     */
    @SuppressWarnings("unused")
    public void setOnInterceptTouchListener(View.OnTouchListener onInterceptTouchListener) {
        mOnInterceptTouchListener = onInterceptTouchListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent) || (mOnInterceptTouchListener != null && mOnInterceptTouchListener.onTouch(this, motionEvent));
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }

        checkIfEmpty();
    }

    @Override
    public void setOverScrollMode(int overScrollMode) {
        mOverScrollMode = overScrollMode;

        applyOverScrollMode();
    }

    /**
     * Set Empty view.
     *
     * This view will be displayed whenever the connected adapter is empty.
     * The view has to be attached to a parent in the view hierarchy for being visible.
     * @param emptyView view to be displayed
     */
    @SuppressWarnings("unused")
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        applyOverScrollMode();
    }

    private void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    private void applyOverScrollMode() {
        switch (mOverScrollMode) {
            case OVER_SCROLL_IF_CONTENT_SCROLLS:
                LayoutManager layoutManager = getLayoutManager();
                if (layoutManager != null) {
                    boolean isScrollable;
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager.SpanSizeLookup spanSizeLookup = ((GridLayoutManager) layoutManager).getSpanSizeLookup();
                        int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                        int contentHeight = 0;
                        int rowHeight = 0;
                        int currentSpan = 0;
                        int spanSize;
                        int itemHeight;
                        for (int i = 0; i < getChildCount(); ++i) {
                            spanSize = spanSizeLookup.getSpanSize(i);
                            if (currentSpan + spanSize > spanCount) {
                                contentHeight += rowHeight;
                                rowHeight = 0;
                                currentSpan = 0;
                            }
                            itemHeight = getChildAt(i).getHeight();
                            rowHeight = (itemHeight > rowHeight) ? itemHeight : rowHeight;
                            currentSpan += spanSize;
                        }
                        contentHeight += rowHeight;
                        isScrollable = contentHeight > getHeight();
                    } else {
                        if (layoutManager.canScrollVertically()) {
                            int contentHeight = 0;
                            for (int i = 0; i < getChildCount(); ++i) {
                                contentHeight += getChildAt(i).getHeight();
                            }
                            isScrollable = contentHeight > getHeight();
                        } else {
                            int contentWidth = 0;
                            for (int i = 0; i < getChildCount(); ++i) {
                                contentWidth += getChildAt(i).getWidth();
                            }
                            isScrollable = contentWidth > getWidth();
                        }
                    }
                    if (isScrollable) {
                        super.setOverScrollMode(OVER_SCROLL_ALWAYS);
                    } else {
                        super.setOverScrollMode(OVER_SCROLL_NEVER);
                    }
                } else {
                    super.setOverScrollMode(mOverScrollMode);
                }
                break;
            default: // View.OVER_SCROLL_ALWAYS && View.OVER_SCROLL_NEVER
                super.setOverScrollMode(mOverScrollMode);
        }
    }
}
