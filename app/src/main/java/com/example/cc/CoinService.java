package com.example.cc;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinService {

    @GET("coins/markets?vs_currency=usd&order=market_cap_desc&per_page=30")
    Call<List<Coin>> getTop10Coins();

}
