package com.bourdi_bay.portfolioquickglance

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import java.util.concurrent.atomic.AtomicInteger

class BoursoramaLoader(
    private val viewAdapter: MyAdapter,
    private val progressBar: ProgressBar,
    private val onLoadingDone: () -> Unit
) {
    private var remainingUrls = AtomicInteger(0)

    private fun initializeProgressBar() {
        val nbUrls = remainingUrls.get()
        progressBar.progress = 0
        progressBar.max = nbUrls
        if (nbUrls > 0)
            progressBar.visibility = View.VISIBLE
    }

    private fun updateProgressBar() {
        progressBar.progress++
        if (isLoaded()) {
            progressBar.visibility = View.GONE
        }
    }

    fun load() {
        val urls = SettingsIO(viewAdapter.context).load()
        remainingUrls.set(urls.size)
        initializeProgressBar()

        if (urls.size > 0) {
            // Erase and add all the scrapers
            viewAdapter.scrapers.clear()
            urls.forEach { url ->
                viewAdapter.scrapers.add(BoursoramaScraper.fromUrl(url))
            }

            // Reload all of them
            for (i in 0 until viewAdapter.scrapers.size()) {
                val scraper = viewAdapter.scrapers.get(i)
                BoursoramaScraperTaskLoadUrls {
                    remainingUrls.decrementAndGet()
                    updateProgressBar()
                    viewAdapter.notifyItemChanged(i)

                    if (!scraper.isLoaded()) {
                        Toast.makeText(
                            viewAdapter.context,
                            "Url " + scraper.getRedirectedLink() + " cannot be loaded",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // last loaded scraper
                    if (isLoaded()) {
                        onLoadingDone()
                    }
                }.execute(scraper)
            }
        } else {
            onLoadingDone()
        }
    }

    private fun isLoaded(): Boolean {
        return remainingUrls.get() <= 0
    }
}