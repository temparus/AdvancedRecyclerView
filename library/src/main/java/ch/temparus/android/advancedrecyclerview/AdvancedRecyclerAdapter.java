package ch.temparus.android.advancedrecyclerview;

import android.view.View;
import android.view.ViewGroup;

/**
 * Adapter interface for use with {@link AdvancedRecyclerView}.
 *
 * @author Sandro Lutz
 */
public interface AdvancedRecyclerAdapter<ContentViewHolder> {

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent a content item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    ContentViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    /**
     * Called by onBindViewHolder(...) to display the data at the specified content position
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's content data set.
     * @param selected true - if the item is selected; false - otherwise
     */
    void onBindContentViewHolder(ContentViewHolder holder, int position, boolean selected);

    /**
     * Returns the total number of content items in the data set hold by the adapter.
     * @return The total number of content items in this adapter.
     */
    int getContentItemCount();

    /**
     * Get view type of the given content item.
     *
     * Note: You should override this method, if you want to use different view types for your content items.
     * @param position position in content list
     * @return integer representing the content type
     */
    int getContentItemViewType(int position);

    /**
     * Get content item of the adapter (not including header views!)
     * @param position position in content list
     * @return content item
     */
    @SuppressWarnings("unused")
    Object getContentItem(int position);

    /**
     * Add header view at the end of the header views
     * @param header view to be added
     */
    @SuppressWarnings("unused")
    void addHeader(View header);

    /**
     * Add header view at the give position
     * @param position position to add the new header view
     * @param header view to be added
     */
    @SuppressWarnings("unused")
    void addHeader(int position, View header);

    /**
     * Remove the given view from the header views.
     * @param header the view to be removed
     */
    @SuppressWarnings("unused")
    void removeHeader(View header);

    /**
     * Remove header view at the given position
     * @param position header view position
     */
    @SuppressWarnings("unused")
    void removeHeader(int position);

    /**
     * Returns the total number of header views in the data set hold by the adapter.
     * @return The total number of header views in this adapter.
     */
    int getHeaderCount();
}
