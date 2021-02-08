package com.example.cc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchData();
    }

    public boolean isOnline(){
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            setContentView(R.layout.no_connection);
            return false;
        }
        return true;
    }

    private void fetchData(){
        if(isOnline()) {
            CoinService coinService = RetrofitInstance.getRetrofitInstance().create(CoinService.class);
            Call<List<Coin>> coinApiCall = coinService.getTop10Coins();
            coinApiCall.enqueue(new Callback<List<Coin>>() {
                @Override
                public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {

                    setupCoinListView(response.body());

                }

                @Override
                public void onFailure(Call<List<Coin>> call, Throwable t) {
                    if(t instanceof SocketTimeoutException){
                        Toast toast = Toast.makeText(getApplicationContext(), "Connection timeout. Please try again", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }
    }



    private void setupCoinListView(List<Coin> coins){
        RecyclerView recyclerView = findViewById(R.id.top10_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));
        final CoinAdapter adapter = new CoinAdapter();
        adapter.setBooks(coins);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class CoinHolder extends RecyclerView.ViewHolder {

        private TextView coinNumberTextView;
        private TextView coinNameTextView;
        private TextView coinPriceTextView;
        private TextView coinChangeTextView;
        private ImageView imageView;
        private Coin coin;

        public CoinHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.coin_list_item, parent, false));
            coinNumberTextView = itemView.findViewById(R.id.text_list_number);
            coinNameTextView = itemView.findViewById(R.id.text_list_coin_name);
            coinPriceTextView = itemView.findViewById(R.id.text_list_coin_price);
            coinChangeTextView = itemView.findViewById(R.id.text_list_coin_change);
            imageView = itemView.findViewById(R.id.img_list_coin);
        }

        public void bind(Coin coin) {
            if (coin != null) {
                this.coin = coin;
                coinNumberTextView.setText(coin.getMarketCapRank()+"");
                coinNameTextView.setText(coin.getName());
                coinPriceTextView.setText("$" + coin.getCurrentPrice());
                if(coin.getPriceChange24h()<0){
                    coinChangeTextView.setTextColor(getResources().getColor(R.color.downRed));
                }
                if(coin.getPriceChange24h()>0){
                    coinChangeTextView.setTextColor(getResources().getColor(R.color.upGreen));
                }
                coinChangeTextView.setText( String.format("%.4f",coin.getPriceChangePercentage24h())+"%");
                Picasso.with(itemView.getContext())
                        .load(coin.getImage())
                        .placeholder(R.drawable.ic_list_placeholder_image).into(imageView);

            }
        }


    }


    private class CoinAdapter extends RecyclerView.Adapter<CoinHolder> implements SensorEventListener{
        private List<Coin> coins;
        Random random = new Random();

        private float mLastX, mLastY, mLastZ;
        private boolean mInitialized;
        private SensorManager mSensorManager;
        private Sensor mAccelerometer;
        private final float NOISE = (float) 8.0;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (!mInitialized) {
                mLastX = x;
                mLastY = y;
                mLastZ = z;
                mInitialized = true;
            } else {
                float deltaX = Math.abs(mLastX - x);
                float deltaY = Math.abs(mLastY - y);
                float deltaZ = Math.abs(mLastZ - z);
                if (deltaX < NOISE) deltaX = (float)0.0;
                if (deltaY < NOISE) deltaY = (float)0.0;
                if (deltaZ < NOISE) deltaZ = (float)0.0;
                mLastX = x;
                mLastY = y;
                mLastZ = z;
                if (deltaY > deltaX) {
                    Intent intent = new Intent(MainActivity.this,GraphDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    Gson gson = new Gson();
                    String myjson = gson.toJson(coins.get(random.nextInt(29)));
                    intent.putExtra("coinid",myjson);
                    startActivity(intent);
                }
            }
        }



        @NonNull
        @Override
        public CoinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

            mInitialized = false;
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

            return new CoinHolder(getLayoutInflater(), parent);
        }


        @Override
        public void onBindViewHolder(@NonNull CoinHolder holder, final int position) {
            if (coins != null) {
                Coin coin = coins.get(position);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,GraphDetailsActivity.class);
                        Gson gson = new Gson();
                        String myjson = gson.toJson(coins.get(position));
                        intent.putExtra("coinid",myjson);
                        startActivity(intent);
                    }
                });

                holder.bind(coin);
            } else {
                Log.d("MainActivity", "No Coins");
            }
        }

        @Override
        public int getItemCount() {
            if (coins != null) {
                return coins.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<Coin> coins) {
            this.coins = coins;
            notifyDataSetChanged();
        }


    }


}