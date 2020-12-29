package com.example.cc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphDetailsActivity extends AppCompatActivity {
    TextView textView;
    LinearLayout chartLyt;
    private Coin coin;
    int number_of_days = 14;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_details2);
        Intent intent = getIntent();
        Gson gson = new Gson();
        coin = gson.fromJson(intent.getStringExtra("coinid"),Coin.class);
        textView = findViewById(R.id.cosss);
        chartLyt = findViewById(R.id.chart);
        textView.setText(coin.getName());


        CoinService coinService = RetrofitInstance.getRetrofitInstance().create(CoinService.class);
        Call<CoinChartData> coinApiCall = coinService.getCoinChartData(coin.getId(), "usd", number_of_days, "daily");
        coinApiCall.enqueue(new Callback<CoinChartData>() {
            @Override
            public void onResponse(Call<CoinChartData> call, Response<CoinChartData> response) {
                renderGraph(response.body());
            }

            @Override
            public void onFailure(Call<CoinChartData> call, Throwable t) {
                t.printStackTrace();

            }
        });


    }

    public void renderGraph(CoinChartData coinChartData) {
        XYSeries priceSeries = new XYSeries("Price");
        XYSeries volumeSeries = new XYSeries("Volume");

        for (int i = 0; i < number_of_days ; i++) {
            priceSeries.add(i + 1, Double.parseDouble(coinChartData.getPrices().get(i).get(1)));
            volumeSeries.add(i + 1, Double.parseDouble(coinChartData.getTotalVolumes().get(i).get(1)));
        }

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(12);
        renderer.setLineWidth(6f);
        renderer.setShowLegendItem(false);
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(priceSeries);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        mRenderer.setLabelsTextSize(35);
        mRenderer.setLabelsColor(android.R.color.transparent);
        mRenderer.setYLabelsAlign(Paint.Align.LEFT);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setDisplayValues(true);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true);

        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);

        chartLyt.addView(chartView,0);
    }


}