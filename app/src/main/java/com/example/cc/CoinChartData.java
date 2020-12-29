package com.example.cc;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoinChartData {

    @SerializedName("prices")
    @Expose
    private List<List<String>> prices = null;
    @SerializedName("market_caps")
    @Expose
    private List<List<String>> marketCaps = null;
    @SerializedName("total_volumes")
    @Expose
    private List<List<String>> totalVolumes = null;

    public List<List<String>> getPrices() {
        return prices;
    }

    public void setPrices(List<List<String>> prices) {
        this.prices = prices;
    }

    public List<List<String>> getMarketCaps() {
        return marketCaps;
    }

    public void setMarketCaps(List<List<String>> marketCaps) {
        this.marketCaps = marketCaps;
    }

    public List<List<String>> getTotalVolumes() {
        return totalVolumes;
    }

    public void setTotalVolumes(List<List<String>> totalVolumes) {
        this.totalVolumes = totalVolumes;
    }

}
