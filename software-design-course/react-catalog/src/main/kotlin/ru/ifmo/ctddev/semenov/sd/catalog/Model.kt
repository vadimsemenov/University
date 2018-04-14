package ru.ifmo.ctddev.semenov.sd.catalog

import com.google.gson.annotations.SerializedName
import org.bson.Document

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */


data class User(
        @SerializedName("name") val name: String,
        @SerializedName("email") val email: String,
        @SerializedName("currency") val currency: String
)
data class Product(
        @SerializedName("title") val title: String,
        @SerializedName("description") val description: String,
        @SerializedName("price") val price: Double
)
data class Currency(
        @SerializedName("code") val code: String,
        @SerializedName("multiplier") val multiplier: Double
)

fun User.toDocument(): Document {
    val document = Document()
    document["name"] = name
    document["email"] = email
    document["currency"] = currency
    return document
}

fun Product.toDocument(): Document {
    val document = Document()
    document["title"] = title
    document["description"] = description
    document["price"] = price
    return document
}

fun Currency.toDocument(): Document {
    val document = Document()
    document["code"] = code
    document["multiplier"] = multiplier
    return document
}

fun Document.toUser(): User {
    val name = getString("name")
    val email = getString("email")
    val currency = getString("currency")
    return User(name, email, currency)
}

fun Document.toProduct(): Product {
    val title = getString("title")
    val description = getString("description")
    val price = getDouble("price")
    return Product(title, description, price)
}

fun Document.toCurrency(): Currency {
    val code = getString("code")
    val multiplier = getDouble("multiplier")
    return Currency(code, multiplier)
}

fun Product.withScaledPrice(multiplier: Double): Product =
        Product(title, description, price * multiplier)

internal const val USERS = "users"
internal const val PRODUCTS = "products"
internal const val CURRENCIES = "currency_values"
internal const val EMAIL_KEY = "email"
internal const val CURRENCY_KEY = "currency"
