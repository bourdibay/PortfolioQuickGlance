package com.bourdi_bay.portfolioquickglance

import org.jsoup.Jsoup
import java.io.IOException
import java.io.InputStream

data class Quotation(val name: String) {
    var opening: String = ""
    var prevClosing: String = ""
    var highest: String = ""
    var lowest: String = ""
}

data class StockData(val name: String) {
    lateinit var currency: String
    lateinit var lastPrice: String
    lateinit var variation: String
    lateinit var isin: String
    val quotation = Quotation(name)
}

class BoursoramaScraper private constructor(private val url: String) {

    companion object {
        fun fromStream(stream: InputStream): BoursoramaScraper {
            val scraper = BoursoramaScraper("")
            scraper.htmlDocument = Jsoup.parse(String(stream.readBytes()))
            scraper.stockData = scraper.fillStockData()
            return scraper
        }

        fun fromISIN(isin: String): BoursoramaScraper {
            return BoursoramaScraper("https://www.boursorama.com/recherche/$isin")
        }

        fun fromUrl(url: String): BoursoramaScraper {
            return BoursoramaScraper(url)
        }
    }

    var stockData: StockData? = null
    private var htmlDocument: org.jsoup.nodes.Document? = null

    fun load() {
        htmlDocument = null
        try {
            htmlDocument = Jsoup.connect(url).get()
        } catch (e: IOException) {
        }
        stockData = fillStockData()
    }

    fun isLoaded() = htmlDocument != null

    fun isValid(): Boolean {
        val finalLink = getRedirectedLink()
        return isLoaded() && stockData != null && !finalLink.startsWith("https://www.boursorama.com/recherche/")
    }

    fun getRedirectedLink(): String = htmlDocument?.location() ?: url

    private fun fillStockData(): StockData? {

        if (htmlDocument == null)
            return null

        val divCompanyBody = htmlDocument!!.select("div.c-faceplate__body")

        if (divCompanyBody.isEmpty())
            return null

        // name
        val divCompanyFaceplate = divCompanyBody.select("div.c-faceplate__company")
        val name = divCompanyFaceplate.select("a.c-faceplate__company-link").text()
        val stock = StockData(name)

        // last price
        val divLastPrice = divCompanyFaceplate.select("div.c-faceplate__price")
        stock.lastPrice = divLastPrice.select("span.c-instrument.c-instrument--last").text()
        stock.currency = divLastPrice.select("span.c-faceplate__price-currency").text()

        // variation
        val divFluctuation = divCompanyFaceplate.select("div.c-faceplate__fluctuation")
        stock.variation = divFluctuation.select("span.c-instrument.c-instrument--variation").text()

        stock.isin = divCompanyBody.select("h2.c-faceplate__isin").text()

        // quotation
        val divDataFaceplate = divCompanyBody.select("div.c-faceplate__data")
        val divQuotations = divDataFaceplate.select("div.c-faceplate__quotation.c-faceplate__quotation--margin-bottom")

        if (divQuotations.isEmpty())
            return stock

        val firstQuotationDiv = divQuotations[0]
        stock.quotation.opening = firstQuotationDiv.select("span.c-instrument.c-instrument--open").text()
        stock.quotation.prevClosing = firstQuotationDiv.select("span.c-instrument.c-instrument--previousclose").text()
        stock.quotation.highest = firstQuotationDiv.select("span.c-instrument.c-instrument--high").text()
        stock.quotation.lowest = firstQuotationDiv.select("span.c-instrument.c-instrument--low").text()

        return stock
    }
}
