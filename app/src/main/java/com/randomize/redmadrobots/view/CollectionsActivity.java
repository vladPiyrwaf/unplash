package com.randomize.redmadrobots.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.randomize.redmadrobots.R;
import com.randomize.redmadrobots.adapters.CollectionsPhotoRecyclerAdapter;
import com.randomize.redmadrobots.api.NetworkService;
import com.randomize.redmadrobots.models.collections.Collection;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionsActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "e1302c9b61d67d3011bfed17ff854fa7aa0426c2adbe9c9fd18528a073476682";

    private CollectionsPhotoRecyclerAdapter photoRecyclerAdapter;
    private ProgressBar progressBar;

    private boolean loading = false;
    private int pageCount = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        progressBar = findViewById(R.id.progress_collections);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCollections);
        recyclerView.setLayoutManager(layoutManager);

        photoRecyclerAdapter = new CollectionsPhotoRecyclerAdapter(this);
        recyclerView.setAdapter(photoRecyclerAdapter);

        firstLoadData(1);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition == photoRecyclerAdapter.getItemCount() - 1 && !loading) {
                    Log.d("newlog", "lastVisibleItemPosition: " + lastVisibleItemPosition + "\n"
                            + "getItemCount: " + (photoRecyclerAdapter.getItemCount() - 1));
                    loading = true;
                    addData(++pageCount);
                    Log.d("pagecount", "pageCount: " + pageCount);

                }
            }
        });
    }

    private void addData(int pageCount){
        NetworkService.getInstance()
                .getJSONApi()
                .getPhotoCollections(pageCount, 10, CLIENT_ID)
                .enqueue(new Callback<List<Collection>>() {
                    @Override
                    public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                        List<Collection> collections = response.body();
                        progressBar.setVisibility(View.GONE);
                        photoRecyclerAdapter.addColletction(collections);
                        loading = false;
                    }

                    @Override
                    public void onFailure(Call<List<Collection>> call, Throwable t) {

                    }
                });
    }

    private void firstLoadData(int pageCount) {
        NetworkService.getInstance()
                .getJSONApi()
                .getPhotoCollections(pageCount, 10, CLIENT_ID)
                .enqueue(new Callback<List<Collection>>() {
                    @Override
                    public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
                        List<Collection> collections = response.body();
                        photoRecyclerAdapter.setCollections(collections);
                        loading = false;
                    }

                    @Override
                    public void onFailure(Call<List<Collection>> call, Throwable t) {
                        Log.d("response", "onFailure: " + t.getMessage());
                    }
                });
    }
}
