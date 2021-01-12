package com.example.cc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphDetailsActivity extends AppCompatActivity {
    ImageView imageView;
    TextView nameTextView;
    TextView priceTextView;
    TextView volumeTextView;
    TextView highTextView;
    TextView lowTextView;
    TextView updateTextView;
    LinearLayout chartLyt;
    private Coin coin;
    private static final int number_of_days = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_graph_details2);

        Intent intent = getIntent();
        Gson gson = new Gson();
        coin = gson.fromJson(intent.getStringExtra("coinid"),Coin.class);

        nameTextView = findViewById(R.id.details_title);
        priceTextView = findViewById(R.id.text_details_price);
        volumeTextView = findViewById(R.id.text_details_totalvolume);
        highTextView = findViewById(R.id.text_details_high24);
        lowTextView = findViewById(R.id.text_details_low24);
        imageView = findViewById(R.id.img_details_coin);
        //updateTextView = findViewById(R.id.text_details_updatedDate);
        chartLyt = findViewById(R.id.chart);

        Picasso.with(this)
                .load(coin.getImage())
                .placeholder(R.drawable.ic_list_placeholder_image).into(imageView);





        CoinService coinService = RetrofitInstance.getRetrofitInstance().create(CoinService.class);
        Call<CoinChartData> coinApiCall = coinService.getCoinChartData(coin.getId(), "usd", number_of_days, "hourly");
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

        nameTextView.setText(coin.getName());
        priceTextView.setText("$"+coin.getCurrentPrice());
        volumeTextView.setText(coin.getTotalVolume());
        highTextView.setText("$"+coin.getHigh24h());
        lowTextView.setText("$"+coin.getLow24h());
       // updateTextView.setText(coin.getLastUpdated());

    }

    public void renderGraph(CoinChartData coinChartData) {
        XYSeries priceSeries = new XYSeries("Price",0);
        XYSeries volumeSeries = new XYSeries("Volume",1);




        for (int i = 0; i < coinChartData.getPrices().size(); i=i+2 ) {
            priceSeries.add(i+1 , Double.parseDouble(coinChartData.getPrices().get(i).get(1)));

            volumeSeries.add(i +1, Double.parseDouble( coinChartData.getTotalVolumes().get(i).get(1).substring(0,5)));
        }

        XYSeriesRenderer lineRenderer = new XYSeriesRenderer();

        lineRenderer.setColor(Color.RED);
        lineRenderer.setDisplayBoundingPoints(true);
        lineRenderer.setPointStyle(PointStyle.CIRCLE);
        lineRenderer.setPointStrokeWidth(0);
        lineRenderer.setLineWidth(6f);
        lineRenderer.setShowLegendItem(false);

        XYSeriesRenderer barRenderer = new XYSeriesRenderer();
        barRenderer.setColor(R.color.colorPrimaryDark);
        barRenderer.setDisplayBoundingPoints(true);
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
        mRenderer.setYLabels(8);

        mRenderer.setXLabels(number_of_days);
        mRenderer.setLabelsColor(android.R.color.transparent);
        mRenderer.setBarSpacing(1.2);

        mRenderer.setYAxisAlign(Paint.Align.LEFT,0);
        mRenderer.setYAxisAlign(Paint.Align.RIGHT,1);
        mRenderer.setYLabelsAlign(Paint.Align.LEFT,0);
        mRenderer.setYLabelsAlign(Paint.Align.LEFT,1);


        mRenderer.setYLabelsPadding(1);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setDisplayValues(true);
        mRenderer.setShowGrid(true);
        mRenderer.setShowLegend(false);



        CombinedXYChart.XYCombinedChartDef[] types = new CombinedXYChart.XYCombinedChartDef[] { new CombinedXYChart.XYCombinedChartDef(LineChart.TYPE, 0), new CombinedXYChart.XYCombinedChartDef(BarChart.TYPE, 1) };

        GraphicalView chartView = ChartFactory.getCombinedXYChartView(this,dataset,mRenderer,types);

        chartLyt.addView(chartView,0);
    }


}