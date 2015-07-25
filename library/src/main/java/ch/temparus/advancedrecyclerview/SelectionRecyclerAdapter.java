package ch.temparus.advancedrecyclerview;

import java.util.List;

/**
 * Adapter interface for selectable items.
 *
 * @author Sandro Lutz
 */
public interface SelectionRecyclerAdapter {

    /**
     * Check if the item at the given position is selectable.
     *
     * Note: Header views are never selectable!
     * @param position item position
     * @return true - if item is selectable; false - otherwise
     */
    boolean isSelectable(int position);

    /**
     * Toggle the selection of the give position (header views included!)
     * @param position item position
     */
    @SuppressWarnings("unused")
    void toggleSelection(int position);

    /**
     * Clear the current selection.
     */
    @SuppressWarnings("unused")
    void clearSelection();

    /**
     * Returns the total number of selected items.
     * @return The total number of selected items in this adapter.
     */
    @SuppressWarnings("unused")
    int getSelectedItemCount();

    /**
     * Get selected item positions in data set of the adapter
     * @return a list containing the positions of all selected items
     */
    @SuppressWarnings("unused")
    List<Integer> getSelectedItems();
}
