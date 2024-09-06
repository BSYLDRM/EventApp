package com.example.eventapp.service.dataclass

import com.google.gson.annotations.SerializedName

data class EventsResponse(
    @SerializedName("_embedded")
    val embedded: EventsEmbedded
)

data class EventsEmbedded(
    @SerializedName("events")
    val events: List<Event>
)

data class Event(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: EventType,
    @SerializedName("id")
    val id: String,
    @SerializedName("test")
    val test: Boolean,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("dates")
    val dates: Dates,
    @SerializedName("classifications")
    val classifications: List<Classification>,
    @SerializedName("_embedded")
    val embedded: EventEmbedded
)

data class Classification(
    @SerializedName("primary")
    val primary: Boolean,
    @SerializedName("segment")
    val segment: Genre,
    @SerializedName("genre")
    val genre: Genre,
    @SerializedName("subGenre")
    val subGenre: Genre,
    @SerializedName("type")
    val type: Genre,
    @SerializedName("subType")
    val subType: Genre,
    @SerializedName("family")
    val family: Boolean
)

data class Genre(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

data class Dates(
    @SerializedName("start")
    val start: Start,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("spanMultipleDays")
    val spanMultipleDays: Boolean
)

data class Start(
    @SerializedName("localDate")
    val localDate: String,
    @SerializedName("localTime")
    val localTime: String,
    @SerializedName("dateTime")
    val dateTime: String,
    @SerializedName("dateTBD")
    val dateTBD: Boolean,
    @SerializedName("dateTBA")
    val dateTBA: Boolean,
    @SerializedName("timeTBA")
    val timeTBA: Boolean,
    @SerializedName("noSpecificTime")
    val noSpecificTime: Boolean
)

data class EventEmbedded(
    @SerializedName("venues")
    val venues: List<Venue>,
    @SerializedName("attractions")
    val attractions: List<Attraction>
)

data class Attraction(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: AttractionType,
    @SerializedName("id")
    val id: String,
    @SerializedName("test")
    val test: Boolean,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("classifications")
    val classifications: List<Classification>
)

data class Image(
    @SerializedName("url")
    val url: String,
    @SerializedName("ratio")
val ratio: String,
@SerializedName("width")
val width: Int
)

enum class AttractionType(val value: String) {
    @SerializedName("attraction")
    ATTRACTION("attraction")
}

data class Venue(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: VenueType,
    @SerializedName("id")
    val id: String,
    @SerializedName("test")
    val test: Boolean,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: List<Image>? = null,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("city")
    val city: City,
    @SerializedName("state")
    val state: State,
    @SerializedName("country")
    val country: Country,
    @SerializedName("address")
    val address: Address,
    @SerializedName("location")
    val location: Location,
    @SerializedName("markets")
    val markets: List<Genre>
)

data class Address(
    @SerializedName("line1")
    val line1: String
)

data class City(
    @SerializedName("name")
    val name: String
)

data class Country(
    @SerializedName("name")
    val name: CountryName,
    @SerializedName("countryCode")
    val countryCode: CountryCode
)

enum class CountryCode(val value: String) {
    @SerializedName("US")
    US("US")
}

enum class CountryName(val value: String) {
    @SerializedName("United States Of America")
    UNITED_STATES_OF_AMERICA("United States Of America")
}

data class Location(
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("latitude")
    val latitude: String
)

data class State(
    @SerializedName("name")
    val name: String,
    @SerializedName("stateCode")
    val stateCode: String
)

enum class VenueType(val value: String) {
    @SerializedName("venue")
    VENUE("venue")
}

enum class EventType(val value: String) {
    @SerializedName("event")
    EVENT("event")
}