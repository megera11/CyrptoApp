package com.example.cc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphDetailsActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_details2);
        Intent intent = getIntent();
        String id = intent.getStringExtra("coinid");
        textView = findViewById(R.id.cosss);
        textView.setText(id);


        CoinService coinService = RetrofitInstance.getRetrofitInstance().create(CoinService.class);
        Call<CoinChartData> coinApiCall = coinService.getCoinChartData(id,"usd",7,"daily");
        coinApiCall.enqueue(new Callback<CoinChartData>() {
            @Override
            public void onResponse(Call<CoinChartData> call, Response<CoinChartData> response) {
                CoinChartData coinChartData = response.body();
                System.out.println("cosssss");
            }

            @Override
            public void onFailure(Call<CoinChartData> call, Throwable t) {
                t.printStackTrace();

            }
        });


    }
}