package com.bourdi_bay.portfolioquickglance

import android.os.AsyncTask
import android.widget.Toast

class BoursoramaScraperTaskFromStock(private val viewAdapter: MyAdapter, private val onPostExecute: () -> Any?) :
    AsyncTask<String, Void, BoursoramaScraper>() {

    override fun doInBackground(vararg params: String): BoursoramaScraper {
        val scraper = BoursoramaScraper.fromStock(params[0])
        scraper.load()
        return scraper
    }

    override fun onPostExecute(scraper: BoursoramaScraper) {

        if (scraper.isLoaded() && !scraper.isValid()) {
            Toast.makeText(viewAdapter.context, "Invalid EPA code", Toast.LENGTH_SHORT).show()
        } else {
            synchronized(viewAdapter.scrapers) {
                viewAdapter.scrapers.add(scraper)
            }
            if (!scraper.isLoaded()) {
                Toast.makeText(
                    viewAdapter.context,
                    "Url " + scraper.getRedirectedLink() + " cannot be loaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        onPostExecute()
    }
}

class BoursoramaScraperTaskLoadUrls(private val postExecute: () -> Unit) :
    AsyncTask<BoursoramaScraper, Void, Unit>() {

    override fun doInBackground(vararg params: BoursoramaScraper) {
        params[0].load()
    }

    override fun onPostExecute(e: Unit) {
        postExecute()
    }
}
