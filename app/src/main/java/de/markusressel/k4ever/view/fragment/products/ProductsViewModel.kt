package de.markusressel.k4ever.view.fragment.products

import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.view.TypedEntityListViewModel
import io.objectbox.kotlin.query
import io.objectbox.query.Query

class ProductsViewModel : TypedEntityListViewModel<ProductEntity>() {

    override fun createDbQuery(persistenceManager: PersistenceManagerBase<ProductEntity>): Query<ProductEntity> {
        return persistenceManager.getStore().query { }
    }

}