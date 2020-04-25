package com.afiq.motorcycleassist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.ShopListRecyclerView.ShopListAdapter;
import com.afiq.motorcycleassist.ShopListRecyclerView.ShopListObject;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {

    private RecyclerView mShopListReyclerView;
    private RecyclerView.Adapter mShopListAdapter;
    private RecyclerView.LayoutManager mShopListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        mShopListReyclerView = (RecyclerView) findViewById(R.id.shopListRecyclerView);
        mShopListReyclerView.setNestedScrollingEnabled(false);
        mShopListReyclerView.setHasFixedSize(true);

        mShopListLayoutManager = new LinearLayoutManager(ShopListActivity.this);
        mShopListReyclerView.setLayoutManager(mShopListLayoutManager);
        mShopListAdapter = new ShopListAdapter(getDataSetShopList(),ShopListActivity.this);
        mShopListReyclerView.setAdapter(mShopListAdapter);


        for(int x=0;x<20;x++){
            ShopListObject obj = new ShopListObject(Integer.toString(x));
            resultsShopList.add(obj);
        }

        mShopListAdapter.notifyDataSetChanged();
    }

    private ArrayList resultsShopList = new ArrayList<ShopListObject>();
    private ArrayList<ShopListObject> getDataSetShopList() {

        return resultsShopList;
    }
}
