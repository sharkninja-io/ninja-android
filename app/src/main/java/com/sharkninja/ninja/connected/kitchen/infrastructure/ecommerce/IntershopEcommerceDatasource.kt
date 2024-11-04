package com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce

import com.sharkninja.ninja.connected.kitchen.ext.getOrEmpty
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Address
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Customer
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopRepository
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.toEcommerceProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.util.Calendar

class IntershopEcommerceDatasource: EcommerceDatasource {

    private val intershopRepo: IntershopRepository by lazy { IntershopRepository() }

    override suspend fun getProfile(countryCode: String, email: String): Flow<EcommerceProfile?> {
        return intershopRepo.getProfile(countryCode).map {
            it?.toEcommerceProfile()
        }
    }

    override suspend fun createProfile(email: String, countryCode: String, useDev: Boolean): Flow<Unit> {
        return intershopRepo.createProfile(email, countryCode, useDev)
    }

    override suspend fun updateProfile(updatedProfile: EcommerceProfile, countryCode: String): Flow<Unit> {
        var address = Address(
            id = null,
            firstName = updatedProfile.address?.firstName,
            lastName = updatedProfile.address?.lastName,
            addressLine1 = updatedProfile.address?.addressLine1,
            addressLine2 = updatedProfile.address?.addressLine2,
            city = updatedProfile.address?.city,
            state = updatedProfile.address?.state,
            countryCode = updatedProfile.address?.countryCode,
            postalCode = updatedProfile.address?.postalCode
        )
        return intershopRepo.getProfile(countryCode).flatMapConcat { intershopProfile ->

            address = address.copy(
                id = intershopProfile?.address?.id
            )

            val customer = Customer(
                email = updatedProfile.email,
                firstName = updatedProfile.firstName,
                lastName = updatedProfile.lastName,
                phoneHome = updatedProfile.phoneNumber,
                customerNo = intershopProfile?.customerNo
            )

            intershopRepo.updateProfile(customer, countryCode)
        }.flatMapConcat {
            intershopRepo.updateProfileAddress(address, countryCode)
        }
    }

    override suspend fun changeProfileEmailAddress(changeEmail: EcommerceChangeEmail, countryCode: String): Flow<Unit> {
        return intershopRepo.changeProfileEmailAddress(
            email = changeEmail.newEmail,
            customerNo = changeEmail.customerNo.getOrEmpty(),
            countryCode = countryCode,
            firstName = changeEmail.firstName,
            lastName = changeEmail.lastName,
            phoneNumber = changeEmail.phoneNumber
        )
    }

    override suspend fun getProductRegistration(
        countryCode: String,
        email: String,
        serialNumber: String?,
        registrationId: String?,
        customerNo: String?
    ): Flow<EcommerceProductRegistration?> {
        return intershopRepo.getProductRegistration(
            countryCode = countryCode,
            customerNo = customerNo,
            registrationId = registrationId
        ).map {
            val registration = it?.productWarrantyDTO?.find { reg ->
                !reg.serialNumber.isNullOrEmpty()  && reg.serialNumber.equals(serialNumber, true) }
            registration?.let { reg ->
                val createdDate = Calendar.getInstance()

                reg.createdDate?.let { dateLong ->
                    createdDate.timeInMillis = dateLong
                }

                EcommerceProductRegistration(
                    firstName = registration.firstName,
                    lastName = registration.lastName,
                    postalCode = null,
                    email = registration.email,
                    productName = registration.productName,
                    productSku = registration.productSku,
                    serialNumber = registration.serialNumber,
                    purchaseDate = createdDate.time,
                    purchaseLocation = null,
                    purchaseLocationName = registration.sellingLocationName,
                    customerId = registration.customerId,
                    creationDate = createdDate.time,
                    competition = false,
                    offers = registration.offers,
                    city = null,
                    country = registration.regCountry,
                    state = null,
                    customerLocale = registration.customerLocale
                )
            } ?: run {
                null
            }
        }
    }

    override suspend fun registerProduct(
        countryCode: String,
        productRegistration: EcommerceProductRegistration
    ): Flow<Unit> {
        return intershopRepo.registerProduct(countryCode, productRegistration)
    }
}