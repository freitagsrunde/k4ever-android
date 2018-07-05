package de.markusressel.k4ever.data.persistence

interface SearchableListItem {

    /**
     * @return a list of objects that may be compared to a string
     */
    fun getSearchableContent(): List<Any> = emptyList()

}