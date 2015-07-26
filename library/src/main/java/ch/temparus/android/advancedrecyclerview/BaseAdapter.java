package ch.temparus.android.advancedrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<ContentViewHolder extends BaseAdapter.ViewHolder>
        extends RecyclerView.Adapter<BaseAdapter.ViewHolder>
        implements AdvancedRecyclerAdapter<ContentViewHolder>, SelectionRecyclerAdapter {

    protected int HEADER_VIEW = 0;
    protected int EMPTY_VIEW = 1;
    protected int CONTENT_VIEW = 2;

    private Context mContext;
    private List<View> mHeaderList;
    private View mEmptyView;
    private SparseBooleanArray mSelectedItems;

    public BaseAdapter(Context context) {
        mContext = context;
        mHeaderList = new ArrayList<>();
        mSelectedItems = new SparseBooleanArray();
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent a content item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    public abstract ContentViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    /**
     * Called by onBindViewHolder(...) to display the data at the specified content position
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's content data set.
     * @param selected true - if the item is selected; false - otherwise
     */
    public abstract void onBindContentViewHolder(ContentViewHolder holder, int position, boolean selected);

    /**
     * Returns the total number of content items in the data set hold by the adapter.
     * @return The total number of content items in this adapter.
     */
    public abstract int getContentItemCount();

    /**
     * Check if the content item at the given position is selectable.
     *
     * Note: This method is called by isSelectable(int).
     * @param position content item position
     * @return true - if content item is selectable; false - otherwise
     */
    public abstract boolean isContentSelectable(int position);

    /**
     * Get content item of the adapter (not including header views!)
     * @param position position in content list
     * @return content item
     */
    public abstract Object getContentItem(int position);

    /**
     * Set empty view. This view will be displayed whenever the adapter does not contain any content items.
     *
     * Note: The given view may not be attached to a parent in the view hierarchy!
     * @param view empty view
     */
    @SuppressWarnings("unused")
    public void setEmptyView(View view) {
        mEmptyView = view;
    }

    /**
     * Add header view at the end of the header views
     * @param header view to be added
     */
    public final void addHeader(View header) {
        mHeaderList.add(header);
        notifyDataSetChanged();
    }

    /**
     * Add header view at the give position
     * @param position position to add the new header view
     * @param header view to be added
     */
    public final void addHeader(int position, View header) {
        if (position < mHeaderList.size()) {
            mHeaderList.add(position, header);
            notifyDataSetChanged();
        } else {
            mHeaderList.add(header);
            notifyDataSetChanged();
        }
    }

    /**
     * Remove the given view from the header views.
     * @param header the view to be removed
     */
    public final void removeHeader(View header) {
        mHeaderList.remove(header);
        notifyDataSetChanged();
    }

    /**
     * Remove header view at the given position
     * @param position header view position
     */
    public final void removeHeader(int position) {
        mHeaderList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of header views in the data set hold by the adapter.
     * @return The total number of header views in this adapter.
     */
    public final int getHeaderCount() {
        return mHeaderList.size();
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(layoutParams);
            return new HeaderViewHolder(frameLayout);
        } else if (viewType == EMPTY_VIEW) {
            return new EmptyViewHolder(mEmptyView);
        }
        return onCreateContentViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        int realPosition = getRealPosition(position);
        if (realPosition < 0) {
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                headerHolder.setHeaderView(mHeaderList.get(position));
            } else {
                throw new ClassCastException("ViewHolder may be of type BaseGridAdapter.HeaderViewHolder");
            }
        } else {
            if (getContentItemCount() > 0) {
                if (!(holder instanceof HeaderViewHolder)) {
                    onBindContentViewHolder((ContentViewHolder) holder, realPosition, mSelectedItems.get(realPosition, false));
                } else {
                    throw new ClassCastException("ViewHolder may be of generic type ContentViewHolder");
                }
            }
        }
    }

    @Override
    public final int getItemViewType(int position) {
        int realPosition = getRealPosition(position);
        int contentItemCount = getContentItemCount();
        return realPosition < 0 ? HEADER_VIEW : (realPosition == 0 && contentItemCount == 0) ? EMPTY_VIEW : getContentItemViewType(realPosition);
    }

    /**
     * Get view type of the given content item.
     *
     * Note: You should override this method, if you want to use different view types for your content items.
     * @param position position in content list
     * @return integer representing the content type
     */
    public int getContentItemViewType(int position) {
        return CONTENT_VIEW;
    }

    @Override
    public final int getItemCount() {
        int contentItemCount = getContentItemCount();
        return mHeaderList.size() + ((contentItemCount == 0 && mEmptyView != null) ? 1 : contentItemCount);
    }

    /**
     * Check if the item at the given position is selectable.
     *
     * Note: Header views are never selectable!
     * @param position item position
     * @return true - if item is selectable; false - otherwise
     */
    public final boolean isSelectable(int position) {
        int realPosition = getRealPosition(position);
        int contentItemCount = getContentItemCount();
        return realPosition >= 0 && contentItemCount > 0 && isContentSelectable(realPosition);
    }

    /**
     * Toggle the selection of the give position (header views included!)
     * @param position item position
     */
    public void toggleSelection(int position) {
        if (position >= 0 && position < getItemCount()) {
            int realPosition = getRealPosition(position);

            if (!isSelectable(position)) return;

            if (mSelectedItems.get(realPosition, false)) {
                mSelectedItems.delete(realPosition);
            } else {
                mSelectedItems.put(realPosition, true);
            }
            notifyItemChanged(position);
        }
    }

    /**
     * Clear the current selection.
     */
    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of selected items.
     * @return The total number of selected items in this adapter.
     */
    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    /**
     * Get selected item positions in content list (header views not included)
     * @return List with the positions of all selected items
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    /**
     * Notify the view that an item has been removed from the content list.
     * @param position position in content list (header views not included)
     */
    @SuppressWarnings("unused")
    public void notifyContentItemRemoved(int position) {
        notifyItemRemoved(position + mHeaderList.size());
    }

    /**
     * Get real position in content list
     * @param position position in this Adapter, including header views
     * @return position in content list
     */
    protected int getRealPosition(int position) {
        return position - mHeaderList.size();
    }

    /**
     * Default ViewHolder used by all connected views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * ViewHolder representing a view displayed when no content items are available
     */
    private static class EmptyViewHolder extends ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * ViewHolder representing a view in the header section
     */
    private static class HeaderViewHolder extends ViewHolder {

        public HeaderViewHolder(ViewGroup itemView) {
            super(itemView);
        }

        public void setHeaderView(View view) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            ((ViewGroup) itemView).addView(view);
        }
    }
}