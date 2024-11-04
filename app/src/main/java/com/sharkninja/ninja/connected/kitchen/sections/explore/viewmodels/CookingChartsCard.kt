package com.sharkninja.ninja.connected.kitchen.sections.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CookingChartCard(
    @SerializedName("title") @Expose var title: String,
    @SerializedName("protein") @Expose var protein: String,
    @SerializedName("mode") @Expose var mode: String,
    @SerializedName("image") @Expose var image: Int,
    @SerializedName("prep") @Expose var prep: String,
    @SerializedName("fahrenheitTemp") @Expose var fahrenheitTemp: Int,
    @SerializedName("genericTemp") @Expose var genericTemp: String,
    @SerializedName("time") @Expose var time: Int,
    @SerializedName("notes") @Expose var notes: String,
    @SerializedName("countries") @Expose var countries: List<String>,
)