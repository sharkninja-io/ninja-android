package com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models

import java.util.Date

@kotlinx.serialization.Serializable
data class EcommerceProfile (
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val customerNo: String = "",
    val address: EcommerceAddress? = null,
    val receiveSharkEmails: Boolean = false
) {

    fun allValuesAreNullOrEmpty(): Boolean {
        // Email should always be populated which is why we don't need it here.
        return (firstName.isEmpty() && lastName.isEmpty()
                && phoneNumber.isEmpty() && customerNo.isEmpty())
                && (address == null || address.allValuesAreNullOrEmpty())
    }
}

data class EcommerceProductRegistration(
    val firstName: String?,
    val lastName: String?,
    val postalCode: String?,
    val email: String?,
    val productName: String?,
    val productSku: String?,
    val serialNumber: String?,
    val purchaseDate: Date?,
    val purchaseLocation: String?,
    val purchaseLocationName: String?,
    val customerId: String?,
    val creationDate: Date?,
    val competition: Boolean?,
    val offers: Boolean?,
    val city: String?,
    val country: String?,
    val state: String?,
    val customerLocale: String?
)

@kotlinx.serialization.Serializable
data class EcommerceAddress(val id: String?, val firstName: String?, val lastName: String?,
                            val addressLine1: String?, val addressLine2: String?, val city: String?, var state: String?,
                            val countryCode: String?, val postalCode: String?) {
    val shipToAddress = true
    val invoiceToAddress = true

    fun allValuesAreNullOrEmpty(): Boolean {
        return id.isNullOrEmpty() && firstName.isNullOrEmpty() && lastName.isNullOrEmpty()
                && addressLine1.isNullOrEmpty() && addressLine2.isNullOrEmpty()
                && city.isNullOrEmpty() && postalCode.isNullOrEmpty() && state.isNullOrEmpty()
    }
}

data class EcommerceChangeEmail(
    val currentEmail: String? = null,
    val newEmail: String,
    val customerNo: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = ""
)