package com.bourdi_bay.portfolioquickglance

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SwipeDeleteDelegate(private val activity: MainActivity, private val adapter: MyAdapter) {

    private var recentlyDeletedItem: BoursoramaScraper? = null

    fun deleteItem(position: Int) {
        synchronized(adapter.scrapers) {
            recentlyDeletedItem = adapter.scrapers[position]
            adapter.scrapers.removeItemAt(position)
            activity.saveBoursoramaUrls()
        }
    }

    private fun undoDelete() {
        if (recentlyDeletedItem != null) {
            synchronized(adapter.scrapers) {
                adapter.scrapers.add(recentlyDeletedItem!!)
                activity.saveBoursoramaUrls()
            }
        }
    }

    fun showUndoSnackbar() {
        val view = activity.findViewById<View>(R.id.mainLayout)
        val snackbar = Snackbar.make(
            view,
            adapter.context.getString(R.string.entryRemoved),
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(adapter.context.getString(R.string.undo)) { undoDelete() }
        snackbar.show()
    }

}