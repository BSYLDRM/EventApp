package com.example.eventapp.service.dataclass

data class GeocodingResponse(
    val results: List<Result>,
    val status: String
)
data class Result(
    val address_components: List<AddressComponent>
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)
