package de.markusressel.k4ever.data.persistence.base.product

import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductPersistenceManager @Inject constructor() :
        PersistenceManagerBase<ProductEntity>(ProductEntity::class)