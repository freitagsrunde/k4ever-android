package de.markusressel.k4ever.data.persistence.user

import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPersistenceManager @Inject constructor() :
        PersistenceManagerBase<UserEntity>(UserEntity::class)