package de.markusressel.k4ever.data.persistence.base.user

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class UserEntity(@Id var entityId: Long = 0, val id: Long, val user_name: String, val display_name: String)