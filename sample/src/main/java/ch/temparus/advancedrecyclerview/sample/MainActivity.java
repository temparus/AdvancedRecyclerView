package ch.temparus.advancedrecyclerview.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import ch.temparus.advancedrecyclerview.DividerItemDecoration;
import ch.temparus.advancedrecyclerview.AdvancedRecyclerView;
import ch.temparus.advancedrecyclerview.LinearLayoutManager;


public class MainActivity extends ActionBarActivity {

    private SampleAdapter mAdapter;
    private AdvancedRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new SampleAdapter(this);
        mAdapter.addHeader(getLayoutInflater().inflate(R.layout.header_item, null));
        for (int i = 0; i < 4; ++i) {
            mAdapter.addItem("Test-Item " + i);
        }

        mRecyclerView = (AdvancedRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, mAdapter));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
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
