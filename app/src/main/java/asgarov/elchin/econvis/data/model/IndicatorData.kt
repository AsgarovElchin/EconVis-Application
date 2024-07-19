package asgarov.elchin.econvis.data.model

data class IndicatorData(
    val year: Int,
    val value: Double
)

typealias ApiResponse = Map<String, Map<String, String>>