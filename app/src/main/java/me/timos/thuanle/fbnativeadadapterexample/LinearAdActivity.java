package me.timos.thuanle.fbnativeadadapterexample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.timos.thuanle.fbnativeadadapter.FBNativeAdAdapter;

public class LinearAdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        MyAdapter adapter = new MyAdapter();

        FBNativeAdAdapter fbAdapter = FBNativeAdAdapter.Builder
                .with("407005126585091_407684516517152", adapter)
                .build();


        //AdmobNativeAd admobNativeAd = AdmobNativeAd.Builder.with("ca-app-pub-9538253184459093/2218305399", adapter).build();

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(fbAdapter);
    }
}
