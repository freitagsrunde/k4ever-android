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

package de.markusressel.k4ever.dagger.module

import android.content.Context
import dagger.Module
import dagger.Provides
import de.markusressel.k4ever.data.persistence.MyObjectBox
import io.objectbox.BoxStore
import javax.inject.Singleton

/**
 * Created by Markus on 30.01.2018.
 */
@Module
abstract class PersistenceBindingsModule {

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        internal fun provideBoxStore(context: Context): BoxStore {
            return MyObjectBox.builder().androidContext(context).build()
        }

    }

}