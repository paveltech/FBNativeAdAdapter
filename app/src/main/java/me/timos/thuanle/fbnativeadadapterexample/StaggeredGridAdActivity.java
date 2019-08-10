package me.timos.thuanle.fbnativeadadapterexample;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import me.timos.thuanle.fbnativeadadapter.FBNativeAdAdapter;

public class StaggeredGridAdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        MyAdapter adapter = new MyAdapter();
        FBNativeAdAdapter fbAdapter = FBNativeAdAdapter.Builder
                .with("321357575164976_321357668498300",2, adapter)
                .build();

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(lm);
        rv.setAdapter(fbAdapter);
    }
}
