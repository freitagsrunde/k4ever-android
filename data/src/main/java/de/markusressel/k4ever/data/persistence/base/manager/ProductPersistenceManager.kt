package de.markusressel.k4ever.data.persistence.base.manager

import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.base.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductPersistenceManager @Inject constructor() :
        PersistenceManagerBase<ProductEntity>(ProductEntity::class)