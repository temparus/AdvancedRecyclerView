package ch.temparus.advancedrecyclerview.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.temparus.advancedrecyclerview.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sandro Lutz
 */
public class SampleAdapter extends BaseAdapter<SampleAdapter.ViewHolder> {

    private List<String> mData;

    public SampleAdapter(Context context) {
        super(context);

        mData = new ArrayList<>();
    }

    public void addItem(String value) {
        mData.add(value);
        notifyItemInserted(mData.size() + getHeaderCount() - 1);
    }

    @Override
    public ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindContentViewHolder(final ViewHolder holder, final int position, boolean selected) {
        ((TextView) holder.itemView).setText(mData.get(position));
    }

    @Override
    public int getContentItemCount() {
        return mData.size();
    }

    public int getContentItemViewType(int position) {
        return position+2;
    }

    @Override
    public boolean isContentSelectable(int position) {
        return true;
    }

    @Override
    public String getContentItem(int position) {
        return mData.get(position);
    }

    public static class ViewHolder extends BaseAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
