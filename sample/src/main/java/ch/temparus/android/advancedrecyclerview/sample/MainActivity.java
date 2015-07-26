package ch.temparus.android.advancedrecyclerview.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import ch.temparus.android.advancedrecyclerview.DividerItemDecoration;
import ch.temparus.android.advancedrecyclerview.AdvancedRecyclerView;
import ch.temparus.android.advancedrecyclerview.LinearLayoutManager;

public class MainActivity extends ActionBarActivity {

    private SampleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new SampleAdapter(this);
        mAdapter.addHeader(getLayoutInflater().inflate(R.layout.header_item, null));
        for (int i = 0; i < 4; ++i) {
            mAdapter.addItem("Test-Item " + i);
        }

        AdvancedRecyclerView recyclerView = (AdvancedRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, mAdapter));
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mAdapter.addItem("Test-Item " + mAdapter.getContentItemCount());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
