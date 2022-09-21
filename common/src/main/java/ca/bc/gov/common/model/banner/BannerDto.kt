package ca.bc.gov.common.model.banner

import java.time.Instant

data class BannerDto(
    val title: String,
    val body: String,
    val startDate: Instant,
    val endDate: Instant,
)
