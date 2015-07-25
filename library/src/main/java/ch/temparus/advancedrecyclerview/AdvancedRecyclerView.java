package ch.temparus.advancedrecyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private View mEmptyView;
    private View.OnTouchListener mOnInterceptTouchListener;

    public AdvancedRecyclerView(Context context) {
        super(context);
    }

    public AdvancedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    /**
     * Set Empty view.
     *
     * This view will be displayed whenever the connected adapter is empty.
     * The view has to be attached to a parent in the view hierarchy for being visible.
     * @param emptyView view to be displayd
     */
    @SuppressWarnings("unused")
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }
}
