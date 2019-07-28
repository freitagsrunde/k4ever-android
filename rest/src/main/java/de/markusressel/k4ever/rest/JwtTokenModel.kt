package de.markusressel.k4ever.rest

import java.util.*

data class JwtTokenModel(val code: String, val expire: Date, val token: String)