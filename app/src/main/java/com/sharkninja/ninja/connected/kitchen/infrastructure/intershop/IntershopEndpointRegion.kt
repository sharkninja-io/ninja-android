package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

data class InterShopEndpoint(
    val code: String,
    val baseUrl: String,
    val subscriptionKey: String
)

enum class InterShopEndpointRegion(val endPoint: InterShopEndpoint) {
    EU(
        InterShopEndpoint(
            "EU",
            "https://sharkninja.azure-api.net/icm/b2c/SharkNinja-EU-Site/",
            "d2290321efb34f3d99fd76c284091761"
        )
    ),
    GB(
        InterShopEndpoint(
            "GB",
            "https://sharkninja.azure-api.net/icm/b2c/SharkNinja-GB-Site/",
            "d2290321efb34f3d99fd76c284091761"
        )
    ),
    US(
        InterShopEndpoint(
            "US",
            "https://sharkninja-prd-cus-001.azure-api.net/icm/b2c/SharkNinja-US-Site/sharkus/",
            "b8290058ce47499f912798e5d2774852"
        )
    ),
    US_DEV(
        InterShopEndpoint(
            "US",
            "https://apim-snj-uat-cus-001.azure-api.net/icm/b2c/SharkNinja-US-Site/sharkus/",
            "b8290058ce47499f912798e5d2774852"
        )
    ),
    CA(
        InterShopEndpoint(
            "CA",
            "https://sharkninja-prd-cus-001.azure-api.net/icm/b2c/SharkNinja-CA-Site/sharkca/",
            "c9c2932f4b064f989c5b7caf3ad0793a"
        )
    ),
    CA_DEV(
        InterShopEndpoint(
            "CA",
            "https://apim-snj-uat-cus-001.azure-api.net/icm/b2c/SharkNinja-CA-Site/sharkca/",
            "b8290058ce47499f912798e5d2774852"
        )
    ),
    NA_WARRANTY(
        InterShopEndpoint(
            "US",
            "https://sharkninja-prd-cus-001.azure-api.net/product/",
            "b8290058ce47499f912798e5d2774852"
        )
    ),
    NA_WARRANTY_DEV(
        InterShopEndpoint(
            "US",
            "https://apim-snj-uat-cus-001.azure-api.net/product/",
            "b8290058ce47499f912798e5d2774852"
        )
    );
}