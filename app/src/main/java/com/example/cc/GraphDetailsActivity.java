package com.example.cc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphDetailsActivity extends AppCompatActivity{
    ImageView imageView;
    TextView nameTextView;
    TextView priceTextView;
    TextView volumeTextView;
    TextView highTextView;
    TextView lowTextView;
    LinearLayout priceChart;
    LinearLayout volumeChart;
    TabLayout tabLayout;
    ProgressBar progressBar_cyclic;
    private Coin coin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_graph_display);

        Intent intent = getIntent();
        Gson gson = new Gson();
        coin = gson.fromJson(intent.getStringExtra("coinid"),Coin.class);

        nameTextView = findViewById(R.id.details_title);
        priceTextView = findViewById(R.id.text_details_price);
        volumeTextView = findViewById(R.id.text_details_totalvolume);
        highTextView = findViewById(R.id.text_details_high24);
        lowTextView = findViewById(R.id.text_details_low24);
        imageView = findViewById(R.id.img_details_coin);
        priceChart = findViewById(R.id.price_chart);
        volumeChart = findViewById(R.id.volume_chart);
        tabLayout = findViewById(R.id.tab_layout);
        progressBar_cyclic = findViewById(R.id.progressBar_cyclic);


        Picasso.with(this).load(coin.getImage()).placeholder(R.drawable.ic_list_placeholder_image).into(imageView);

        nameTextView.setText(coin.getName());
        priceTextView.setText("$"+coin.getCurrentPrice());
        volumeTextView.setText(coin.getTotalVolume());
        highTextView.setText("$"+coin.getHigh24h());
        lowTextView.setText("$"+coin.getLow24h());


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        fetchData(1);
                        break;
                    case 1:
                        fetchData(7);
                        break;
                    case 2:
                        fetchData(14);
                        break;
                    case 3:
                        fetchData(30);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.addTab(tabLayout.newTab().setText("1D"));
        tabLayout.addTab(tabLayout.newTab().setText("7D"));
        tabLayout.addTab(tabLayout.newTab().setText("14D"));
        tabLayout.addTab(tabLayout.newTab().setText("30D"));



    }



    private void fetchData(final int number_of_days){


        CoinService coinService = RetrofitInstance.getRetrofitInstance().create(CoinService.class);
        Call<CoinChartData> coinApiCall = coinService.getCoinChartData(coin.getId(), "usd", number_of_days, "hourly");
        coinApiCall.enqueue(new Callback<CoinChartData>() {
            @Override
            public void onResponse(Call<CoinChartData> call, Response<CoinChartData> response) {
                progressBar_cyclic.setVisibility(View.VISIBLE);
                setupGraph(response.body(),number_of_days);
                progressBar_cyclic.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CoinChartData> call, Throwable t) {
                t.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), "Connection timeout. Please try again", Toast.LENGTH_LONG);
                toast.show();
            }

        });

    }



    private void setupGraph(CoinChartData coinChartData, int number_of_days) {
        XYSeries priceSeries = new XYSeries("Price");
        XYSeries volumeSeries = new XYSeries("Volume");




        for (int i = 0; i < coinChartData.getPrices().size(); i=i+1 ) {
            priceSeries.add(i+1 , Double.parseDouble(coinChartData.getPrices().get(i).get(1)));

            volumeSeries.add(i +1, Double.parseDouble( coinChartData.getTotalVolumes().get(i).get(1)));
        }

        XYSeriesRenderer lineRenderer = new XYSeriesRenderer();
        lineRenderer.setColor(Color.RED);
        lineRenderer.setDisplayBoundingPoints(true);
        lineRenderer.setPointStyle(PointStyle.CIRCLE);
        lineRenderer.setPointStrokeWidth(0);
        lineRenderer.setLineWidth(6f);
        lineRenderer.setShowLegendItem(false);

        XYSeriesRenderer barRenderer = new XYSeriesRenderer();
        barRenderer.setColor(R.color.dimTextColor2);
        barRenderer.setDisplayBoundingPoints(true);
        barRenderer.setPointStrokeWidth(1);
        barRenderer.setLineWidth(1f);
        barRenderer.setShowLegendItem(false);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(priceSeries);
        XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
        dataset1.addSeries(volumeSeries);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(lineRenderer);

        mRenderer.setMarginsColor(android.R.color.transparent); // transparent margins
        mRenderer.setLabelsTextSize(50);
        mRenderer.setYLabels(8);
        mRenderer.setXLabels(number_of_days);
        mRenderer.setLabelsColor(android.R.color.transparent);
        mRenderer.setBarSpacing(1.2);


        mRenderer.setYAxisAlign(Paint.Align.LEFT,0);
        mRenderer.setYLabelsAlign(Paint.Align.LEFT,0);


        mRenderer.setYLabelsPadding(1);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setDisplayValues(true);
        mRenderer.setShowAxes(false);
        mRenderer.setShowLabels(false,true);
        mRenderer.setShowGrid(true);
        mRenderer.setShowLegend(false);

        XYMultipleSeriesRenderer mRenderer1 = new XYMultipleSeriesRenderer();
        mRenderer1.addSeriesRenderer(barRenderer);

        mRenderer1.setMarginsColor(android.R.color.transparent);
        mRenderer1.setLabelsColor(android.R.color.transparent);

        mRenderer1.setBarSpacing(1.2);
        mRenderer1.setYLabelsPadding(1);
        mRenderer1.setPanEnabled(false, false);
        mRenderer1.setDisplayValues(false);
        mRenderer1.setShowLabels(false);
        mRenderer1.setShowAxes(false);
        mRenderer1.setShowGrid(false);
        mRenderer1.setShowLegend(false);

        if (priceChart.getChildCount() != 1 && volumeChart.getChildCount() != 0) {
            priceChart.removeViewAt(1);
            volumeChart.removeViewAt(0);
        }

        GraphicalView chartView = ChartFactory.getLineChartView(this,dataset,mRenderer);
        priceChart.addView(chartView);
        chartView = ChartFactory.getBarChartView(this,dataset1,mRenderer1,BarChart.Type.DEFAULT);
        volumeChart.addView(chartView);

    }


}