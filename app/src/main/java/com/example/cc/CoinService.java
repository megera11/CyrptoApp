package com.example.cc;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinService {

    @GET("coins/markets?vs_currency=usd&order=market_cap_desc&per_page=30")
    Call<List<Coin>> getTop10Coins();

    @GET("coins/{id}/market_chart?vs_currency=usd&days=1&interval=daily")
    Call<CoinChartData> getCoinChartData(@Path("id") String id,@Query("vs_currency") String currency,@Query("days") int number_of_days,@Query("interval") String interval);

}
