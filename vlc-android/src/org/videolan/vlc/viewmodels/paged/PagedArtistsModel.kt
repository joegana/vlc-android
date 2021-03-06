package org.videolan.vlc.viewmodels.paged

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import org.videolan.medialibrary.Medialibrary
import org.videolan.medialibrary.media.Artist
import org.videolan.vlc.util.EmptyMLCallbacks
import org.videolan.vlc.util.Settings


class PagedArtistsModel(context: Context, private var showAll: Boolean = false): MLPagedModel<Artist>(context), Medialibrary.ArtistsCb by EmptyMLCallbacks {

    init {
        sort = Settings.getInstance(context).getInt(sortKey, Medialibrary.SORT_ALPHA)
        desc = Settings.getInstance(context).getBoolean("${sortKey}_desc", false)
    }

    override fun onArtistsAdded() {
        refresh()
    }

    fun showAll(show: Boolean) {
        showAll = show
    }

    override fun getAll() = medialibrary.getArtists(showAll, sort, desc)

    override fun getPage(loadSize: Int, startposition: Int): Array<Artist> {
        return if (filter == null) medialibrary.getPagedArtists(showAll, sort, desc, loadSize, startposition)
        else medialibrary.searchArtist(filter, sort, desc, loadSize, startposition)
    }

    override fun getTotalCount() = if (filter == null) medialibrary.getArtistsCount(showAll)
    else medialibrary.getArtistsCount(filter)

    override fun onMedialibraryReady() {
        super.onMedialibraryReady()
        medialibrary.addArtistsCb(this)
    }

    override fun onCleared() {
        super.onCleared()
        medialibrary.removeArtistsCb(this)
    }

    class Factory(private val context: Context, private val showAll: Boolean): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PagedArtistsModel(context, showAll) as T
        }
    }
}