package me.timos.thuanle.fbnativeadadapterexample;


import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import me.timos.thuanle.fbnativeadadapter.AdmobNativeAd;


public class LinearAdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        MyAdapter adapter = new MyAdapter();
//        FBNativeAdAdapter fbAdapter = FBNativeAdAdapter.Builder
//                .with("321357575164976_321357668498300", 2, adapter)
//                .build();
//
//        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
//        rv.setAdapter(fbAdapter);
//        rv.setLayoutManager(new LinearLayoutManager(this));


        AdmobNativeAd admobNativeAd = AdmobNativeAd.Builder
                .with("ca-app-pub-9538253184459093/2218305399", adapter)
                .build();

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(admobNativeAd);
    }
}
