package com.sharkninja.ninja.connected.kitchen.data.enums

enum class State(private val displayName: String) {
    // US
    AL("Alabama"),
    AK("Alaska"),
    AS("American Samoa"),
    AZ("Arizona"),
    AR("Arkansas"),
    CA("California"),
    CO("Colorado"),
    CT("Connecticut"),
    DE("Delaware"),
    FL("Florida"),
    GA("Georgia"),
    GU("Guam"),
    HI("Hawaii"),
    ID("Idaho"),
    IL("Illinois"),
    IN("Indiana"),
    IA("Iowa"),
    KS("Kansas"),
    KY("Kentucky"),
    LA("Louisiana"),
    ME("Maine"),
    MD("Maryland"),
    MA("Massachusetts"),
    MI("Michigan"),
    MN("Minnesota"),
    MS("Mississippi"),
    MO("Missouri"),
    MT("Montana"),
    NE("Nebraska"),
    NV("Nevada"),
    NH("New Hampshire"),
    NJ("New Jersey"),
    NM("New Mexico"),
    NY("New York"),
    NC("North Carolina"),
    ND("North Dakota"),
    OH("Ohio"),
    OK("Oklahoma"),
    OR("Oregon"),
    PA("Pennsylvania"),
    PR("Puerto Rico"),
    RI("Rhode Island"),
    SC("South Carolina"),
    SD("South Dakota"),
    TN("Tennessee"),
    TX("Texas"),
    UT("Utah"),
    VT("Vermont"),
    VI("Virgin Islands"),
    VA("Virginia"),
    WA("Washington"),
    DC("Washington DC"),
    WV("West Virginia"),
    WI("Wisconsin"),
    WY("Wyoming"),

    // Canada
    AB("Alberta"),
    BC("British Columbia"),
    MB("Manitoba"),
    NB("New Brunswick"),
    NL("Newfoundland and Labrador"),
    NT("Northwest Territories"),
    NS("Nova Scotia"),
    NU("Nunavut"),
    ON("Ontario"),
    PE("Prince Edward Island"),
    QC("Quebec"),
    SK("Saskatchewan"),
    YT("Yukon");

    fun getDisplayName(): String {
        return displayName
    }

    companion object {
        fun getEnumByName(name: String): State? {
            for (i in values()) {
                if (i.name == name) return i
            }
            return null
        }

        fun getEnumByDisplayName(name: String): String? {
            for (i in values()) {
                if (i.displayName == name) return i.toString()
            }
            return null
        }
    }
}