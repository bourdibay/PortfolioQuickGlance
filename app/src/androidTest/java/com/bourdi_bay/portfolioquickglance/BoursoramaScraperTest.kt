package com.bourdi_bay.portfolioquickglance

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoursoramaScraperTest {

    val context: Context = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun retrieveTrackerFromAssets() {

        val stream = context.resources.assets.open("cw8.html")

        val scraper = BoursoramaScraper.fromStream(stream)

        val stockData = scraper.stockData

        Assert.assertEquals("AMUNDI ETF MSCI WR", stockData?.name)
        Assert.assertEquals("EUR", stockData?.currency)
        Assert.assertEquals("LU1681043599 - Amundi Luxembourg S.A.", stockData?.isin)
        Assert.assertEquals("282.0954", stockData?.lastPrice)
        Assert.assertEquals("-0.86%", stockData?.variation)

        Assert.assertEquals("280.1900", stockData?.quotation?.opening)
        Assert.assertEquals("284.5486", stockData?.quotation?.prevClosing)
        Assert.assertEquals("279.5515", stockData?.quotation?.lowest)
        Assert.assertEquals("282.2200", stockData?.quotation?.highest)
    }

    @Test
    fun retrieveCAC40StockFromWebsite() {
        val scraper = BoursoramaScraper.fromStock("gle")
        scraper.load()
        Assert.assertEquals("https://www.boursorama.com/cours/1rPGLE/", scraper.getRedirectedLink())
    }

    @Test
    fun wrongStockEntered() {
        val scraper = BoursoramaScraper.fromStock("hellllllo")
        scraper.load()
        Assert.assertFalse(scraper.isValid())
    }
}
