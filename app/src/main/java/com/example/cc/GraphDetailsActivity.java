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
import org.achartengine.chart.BarChart;
import org.achartengine.chart.CombinedXYChart;
import org.achartengine.chart.LineChart;
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
    private static final int number_of_days = 15;
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
        XYSeries priceSeries = new XYSeries("Price",0);
        XYSeries volumeSeries = new XYSeries("Volume",1);




        for (int i = 1; i < number_of_days ; i++) {
            priceSeries.add(i + 1, Double.parseDouble(coinChartData.getPrices().get(i).get(1)));
            volumeSeries.add(i + 1, Double.parseDouble(coinChartData.getTotalVolumes().get(i).get(1))/10000000);
        }

        XYSeriesRenderer lineRenderer = new XYSeriesRenderer();

        lineRenderer.setColor(Color.RED);
        lineRenderer.setDisplayBoundingPoints(true);
        lineRenderer.setPointStyle(PointStyle.CIRCLE);
        lineRenderer.setPointStrokeWidth(12);
        lineRenderer.setLineWidth(6f);
        lineRenderer.setShowLegendItem(false);

        XYSeriesRenderer barRenderer = new XYSeriesRenderer();
        barRenderer.setColor(R.color.colorPrimaryDark);
        barRenderer.setDisplayBoundingPoints(true);
        barRenderer.setPointStyle(PointStyle.CIRCLE);
        barRenderer.setPointStrokeWidth(1);
        barRenderer.setLineWidth(1f);
        barRenderer.setShowLegendItem(false);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(priceSeries);
        dataset.addSeries(volumeSeries);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(2);
        mRenderer.addSeriesRenderer(lineRenderer);
        mRenderer.addSeriesRenderer(barRenderer);
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        mRenderer.setLabelsTextSize(35);
        mRenderer.setYLabels(10);
        mRenderer.setXLabels(number_of_days);
        mRenderer.setLabelsColor(android.R.color.transparent);
        mRenderer.setBarSpacing(1);

        mRenderer.setYAxisAlign(Paint.Align.LEFT,0);
        mRenderer.setYAxisAlign(Paint.Align.RIGHT,1);
        mRenderer.setYLabelsAlign(Paint.Align.LEFT);
        mRenderer.setYLabelsPadding(1);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setDisplayValues(true);
        mRenderer.setShowGrid(true);

        CombinedXYChart.XYCombinedChartDef[] types = new CombinedXYChart.XYCombinedChartDef[] { new CombinedXYChart.XYCombinedChartDef(LineChart.TYPE, 0), new CombinedXYChart.XYCombinedChartDef(BarChart.TYPE, 1) };

        GraphicalView chartView = ChartFactory.getCombinedXYChartView(this,dataset,mRenderer,types);

        chartLyt.addView(chartView,0);
    }


}