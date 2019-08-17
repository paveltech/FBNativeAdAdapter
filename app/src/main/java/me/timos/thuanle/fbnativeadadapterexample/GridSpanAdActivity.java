package me.timos.thuanle.fbnativeadadapterexample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.timos.thuanle.fbnativeadadapter.FBNativeAdAdapter;

public class GridSpanAdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        GridLayoutManager lm = new GridLayoutManager(this, 3);
        MyAdapter adapter = new MyAdapter();
        FBNativeAdAdapter fbAdapter = FBNativeAdAdapter.Builder
                .with("321357575164976_321357668498300", adapter)
                .adItemInterval(9)
                .enableSpanRow(lm)
                .build();

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(lm);
        rv.setAdapter(fbAdapter);
    }
}
