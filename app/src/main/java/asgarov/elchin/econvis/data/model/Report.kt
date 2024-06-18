package asgarov.elchin.econvis.data.model

data class Report(
    val id: Long,
    val country: Country,
    val year: Year,
    val indicator: Indicator,
    val data: String
)
