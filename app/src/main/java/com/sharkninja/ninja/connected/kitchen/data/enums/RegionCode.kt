package com.sharkninja.ninja.connected.kitchen.data.enums

import com.sharkninja.ninja.connected.kitchen.data.models.CountryRegionSupport
import com.sharkninja.ninja.connected.kitchen.data.models.CountryRegionSupport.CountryRegionServer.*

enum class RegionCode(val item: CountryRegionSupport) {
//    AT(CountryRegionSupport("AT", "Austria", Europe)),
//    BE(CountryRegionSupport("BE", "Belgium", Europe)),
//    BG(CountryRegionSupport("BG", "Bulgaria", Europe)),
    CA(CountryRegionSupport("CA", "Canada", NorthAmerica)),
//    CN(CountryRegionSupport("CN", "China", China)),
//    HR(CountryRegionSupport("HR", "Croatia", Europe)),
//    CY(CountryRegionSupport("CY", "Cyprus", Europe)),
//    CZ(CountryRegionSupport("CZ", "Czechia", Europe)),
//    DK(CountryRegionSupport("DK", "Denmark", Europe)),
//    EE(CountryRegionSupport("EE", "Estonia", Europe)),
//    FI(CountryRegionSupport("FI", "Finland", Europe)),
    FR(CountryRegionSupport("FR", "France", Europe)),
    DE(CountryRegionSupport("DE", "Germany", Europe)),
//    GR(CountryRegionSupport("GR", "Greece", Europe)),
//    HU(CountryRegionSupport("HU", "Hungary", Europe)),
//    IS(CountryRegionSupport("IS", "Iceland", Europe)),
//    IE(CountryRegionSupport("IE", "Ireland", Europe)),
//    IT(CountryRegionSupport("IT", "Italy", Europe)),
//    JP(CountryRegionSupport("JP", "Japan", NorthAmerica)),
//    LV(CountryRegionSupport("LV", "Latvia", Europe)),
//    LI(CountryRegionSupport("LI", "Liechtenstein", Europe)),
//    LT(CountryRegionSupport("LT", "Lithuania", Europe)),
//    LU(CountryRegionSupport("LU", "Luxembourg", Europe)),
//    MT(CountryRegionSupport("MT", "Malta", Europe)),
//    NL(CountryRegionSupport("NL", "Netherlands", Europe)),
//    NO(CountryRegionSupport("NO", "Norway", Europe)),
//    PL(CountryRegionSupport("PL", "Poland", Europe)),
//    PT(CountryRegionSupport("PT", "Portugal", Europe)),
//    RO(CountryRegionSupport("RO", "Romania", Europe)),
//    SK(CountryRegionSupport("SK", "Slovakia", Europe)),
//    SI(CountryRegionSupport("SI", "Slovenia", Europe)),
//    ES(CountryRegionSupport("ES", "Spain", Europe)),
//    SE(CountryRegionSupport("SE", "Sweden", Europe)),
//    CH(CountryRegionSupport("CH", "Switzerland", Europe)),
    GB(CountryRegionSupport("GB", "United Kingdom", Europe)),
    US(CountryRegionSupport("US", "United States", NorthAmerica)),
//    RestOfWorld(CountryRegionSupport("RestOfWorld", "Rest of World", NorthAmerica))
}

/**
 * @return [RegionCode] matching given country code or name
 * @throws [IllegalArgumentException] if unable to find a match
 */
fun String.toRegionCode(): RegionCode {
    return RegionCode.values().firstOrNull { regionCode ->
        regionCode.item.code == this || regionCode.item.name == this
    }
        ?: throw IllegalArgumentException("${RegionCode::class.java.simpleName}: Failed to find a region code matching code($this).")
}

fun String.toRegionServer(): CountryRegionSupport.CountryRegionServer {
    val region = RegionCode.values().firstOrNull { regionCode ->
        regionCode.item.code == this || regionCode.item.name == this
    }
    return region?.item?.server
        ?: throw IllegalArgumentException("${RegionCode::class.java.simpleName}: Failed to find a region server matching code($this).")
}