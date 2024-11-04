package com.sharkninja.ninja.connected.kitchen.data.models

data class CountryRegionSupport(val code: String, val name: String, val server: CountryRegionServer) {

    enum class CountryRegionServer(val code: String) {
        NorthAmerica("NA"),
        Europe("EU"),
        China("CN")
    }
}