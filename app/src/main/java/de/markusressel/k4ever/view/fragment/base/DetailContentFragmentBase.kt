/*
 * Copyright (C) 2018 Markus Ressel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.k4ever.view.fragment.base

import android.os.Bundle
import android.view.View
import com.github.florent37.materialviewpager.MaterialViewPagerHelper
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import kotlinx.android.synthetic.main.layout__item_detail__product.*

/**
 * Created by Markus on 15.02.2018.
 */
abstract class DetailContentFragmentBase<EntityType : Any> : DaggerSupportFragmentBase() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MaterialViewPagerHelper.registerScrollView(activity, scrollView)
    }

    /**
     * Get the persistence handler for this view
     */
    protected abstract fun getPersistenceHandler(): PersistenceManagerBase<EntityType>

    /**
     * Get the entity to edit from persistence
     */
    protected fun getEntityFromPersistence(): EntityType {
        val args = arguments ?: throw IllegalStateException("Arguments must not be null!")
        val entityId: Long = args.getLong(DetailFragmentBase.KEY_ITEM_ID)
        return getPersistenceHandler().getStore().get(entityId)
    }

    /**
     * Store a modified version of the entity in persistence
     */
    protected fun storeModifiedEntity(modifiedEntity: EntityType) {
        getPersistenceHandler().getStore().put(modifiedEntity)
    }

}