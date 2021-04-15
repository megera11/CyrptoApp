package com.example.cc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Coin {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("current_price")
    @Expose
    private Double currentPrice;
    @SerializedName("market_cap")
    @Expose
    private String marketCap;
    @SerializedName("market_cap_rank")
    @Expose
    private Integer marketCapRank;
    @SerializedName("fully_diluted_valuation")
    @Expose
    private String fullyDilutedValuation;
    @SerializedName("total_volume")
    @Expose
    private String totalVolume;
    @SerializedName("high_24h")
    @Expose
    private Double high24h;
    @SerializedName("low_24h")
    @Expose
    private Double low24h;
    @SerializedName("price_change_24h")
    @Expose
    private Double priceChange24h;
    @SerializedName("price_change_percentage_24h")
    @Expose
    private Double priceChangePercentage24h;
    @SerializedName("market_cap_change_24h")
    @Expose
    private String marketCapChange24h;
    @SerializedName("market_cap_change_percentage_24h")
    @Expose
    private Double marketCapChangePercentage24h;
    @SerializedName("circulating_supply")
    @Expose
    private Double circulatingSupply;
    @SerializedName("total_supply")
    @Expose
    private Double totalSupply;
    @SerializedName("max_supply")
    @Expose
    private Double maxSupply;
    @SerializedName("ath")
    @Expose
    private Double ath;
    @SerializedName("ath_change_percentage")
    @Expose
    private Double athChangePercentage;
    @SerializedName("ath_date")
    @Expose
    private String athDate;
    @SerializedName("atl")
    @Expose
    private Double atl;
    @SerializedName("atl_change_percentage")
    @Expose
    private Double atlChangePercentage;
    @SerializedName("atl_date")
    @Expose
    private String atlDate;
    @SerializedName("roi")
    @Expose
    private Object roi;
    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;

    public String getId() {
        return id;
    }

     public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Integer getMarketCapRank() {
        return marketCapRank;
    }

    public String getTotalVolume() {
        return totalVolume;
    }

    public Double getHigh24h() {
        return high24h;
    }

    public Double getLow24h() {
        return low24h;
    }

    public Double getPriceChange24h() {
        return priceChange24h;
    }

    public Double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }


}
