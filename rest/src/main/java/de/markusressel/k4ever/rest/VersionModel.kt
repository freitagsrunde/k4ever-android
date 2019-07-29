package de.markusressel.k4ever.rest

import java.util.*

data class VersionModel(
        val version: String,
        val branch: String,
        val commit: String,
        val build_time: Date)