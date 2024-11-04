package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import com.google.gson.annotations.SerializedName
import com.sharkninja.ninja.connected.kitchen.ext.getOrEmpty
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceAddress
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile


data class IntershopLogin(val login: String)

data class IntershopChangeEmail(
    val email: String,
    val customerNo: String?,
    val firstName: String = "",
    val lastName: String = "",
    @SerializedName("phoneHome") val phoneNumber: String = ""
)

// From what I can tell, sometimes we get perferredShipToAddress as the address back, and sometimes
// it's preferredInvoiceToAddress. Added support for both. If there's a new JSON they change, just
// add it to the alternate list.
data class Customer @JvmOverloads constructor(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var phoneHome: String? = null,
    var customerNo: String? = null,
    var customerType: String? = null,
    @SerializedName("preferredInvoiceToAddress", alternate = ["preferredShipToAddress"]) var address: Address? = null
) {

    fun allValuesAreNullOrEmpty(): Boolean {
        // Email should always be populated which is why we don't need it here.
        return (firstName.isNullOrEmpty() && lastName.isNullOrEmpty()
                && phoneHome.isNullOrEmpty() && customerNo.isNullOrEmpty()) &&
                (address == null || address?.allValuesAreNullOrEmpty() == true)
    }
}

data class Address(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    var state: String?,
    val countryCode: String?,
    val postalCode: String?
) {
    val shipToAddress = true
    val invoiceToAddress = true

    fun allValuesAreNullOrEmpty(): Boolean {
        return id.isNullOrEmpty() && firstName.isNullOrEmpty() && lastName.isNullOrEmpty()
                && addressLine1.isNullOrEmpty() && addressLine2.isNullOrEmpty()
                && city.isNullOrEmpty() && postalCode.isNullOrEmpty() && state.isNullOrEmpty()
    }
}

data class ProductsRegistration(val productWarrantyDTO: List<ProductRegistration>, val totalRecords: Int?, val totalNumberOfPages: Int?)

data class SharkProductTypes(val SharkProductTypes: List<SharkProductType>, val NinjaProductTypes: List<SharkProductType>, val SharkSellingLocations: List<String>, val NinjaSellingLocations: List<String>)

data class ProductRegistration(
    val brand: String?,
    val competitions: Boolean?,
    val createdDate: Long?,
    @SerializedName("customerid", alternate = ["customerId"])  val customerId: String?,
    val customerLocale: String?,
    val email: String?,
    val factoryCode: String?,
    @SerializedName("firstname", alternate = ["firstName"]) val firstName: String?,
    val imageUrl: String?,
    val kitsku: String?,
    val lastModifiedDate: Int?,
    @SerializedName("lastname", alternate = ["lastName"]) val lastName: String?,
    val offers: Boolean?,
    val partWarrantyRegId: List<Any?>?,
    @SerializedName("productname", alternate = ["productName"]) val productName: String?,
    val productUrl: String?,
    val productionCode: String?,
    @SerializedName("sellinglocationname", alternate = ["purchaseSourceName"]) val sellingLocationName: String?,
    val recordSource: String?,
    val regCountry: String?,
    val regId: String?,
    val regStatus: String?,
    @SerializedName("productid", alternate = ["sku"]) val productSku: String?,
    @SerializedName("serialnumber", alternate = ["smartSerialNumber"]) val serialNumber: String?,
    val tips: Boolean?,
    val warrantyDuration: Int?,
    val warrantyEffectiveDate: Long?,
    val warrantyEndDate: Long?,
    val warrantyFAQURL: String?,
    val warrantyInfoURL: String?,
    val warrantyType: String?,
    val warrantyUserManualURL: String?,
)


data class ProductToRegistration(
    @SerializedName("firstname") val firstName: String?,
    @SerializedName("lastname") val lastName: String?,
    @SerializedName("postalcode") val postalCode: String?,
    val email: String?,
    @SerializedName("productname") val productName: String?,
    @SerializedName("purchasedate") val purchaseDate: String?,
    @SerializedName("sellinglocation") val sellingLocation: String?,
    @SerializedName("sellinglocationname") val sellingLocationName: String?,
    @SerializedName("serialnumber") val serialNumber: String?,
    val competitions: String?,
    @SerializedName("offers") val receiveOffers: Boolean?,
    @SerializedName("customerid")  val customerId: String?,
    @SerializedName("productid") val productId: String?
)

data class CreateProductRegistration(
    val customerId: String?,
    val customerLocale: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val purchaseSourceName: String?,
    val recordSource: String?,
    val regCountry: String?,
    val warrantyEffectiveDate: Long?,
    @SerializedName("sku") val productSku: String?,
    @SerializedName("smartSerialNumber") val serialNumber: String?
)

data class SharkProductType(val id: String?, val name: String?, val products: List<Product>?)

data class Product(val name: String?, val sku: String?)

fun Customer.toEcommerceProfile(): EcommerceProfile {
    val address = EcommerceAddress(
        addressLine1 = address?.addressLine1,
        addressLine2 = address?.addressLine2,
        city = address?.city,
        state = address?.state,
        postalCode = address?.postalCode,
        countryCode = address?.countryCode,
        firstName = address?.firstName,
        lastName = address?.lastName,
        id = address?.id
    )

    return EcommerceProfile(
        firstName = firstName.getOrEmpty(),
        lastName = lastName.getOrEmpty(),
        customerNo = customerNo.getOrEmpty(),
        address = address,
        phoneNumber = phoneHome.getOrEmpty()
    )
}