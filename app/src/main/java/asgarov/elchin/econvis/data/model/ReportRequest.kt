package asgarov.elchin.econvis.data.model

data class ReportRequest(
    val countryIds: List<Long>,
    val indicatorIds: List<Long>,
    val yearIds: List<Long>
)
