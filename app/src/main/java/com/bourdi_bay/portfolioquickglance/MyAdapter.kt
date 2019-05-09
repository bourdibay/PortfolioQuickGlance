package com.bourdi_bay.portfolioquickglance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList

class MyAdapter(val context: Context) : RecyclerView.Adapter<MyAdapter.DataObjectHolder>() {

    val scrapers = ScraperList()

    inner class ScraperList : SortedList<BoursoramaScraper>(BoursoramaScraper::class.java, object :
        SortedList.Callback<BoursoramaScraper>() {
        override fun areItemsTheSame(item1: BoursoramaScraper?, item2: BoursoramaScraper?): Boolean {
            return areContentsTheSame(item1, item2)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun compare(o1: BoursoramaScraper?, o2: BoursoramaScraper?): Int {
            return compareValues(o1?.stockData?.name, o2?.stockData?.name)
        }

        override fun areContentsTheSame(oldItem: BoursoramaScraper?, newItem: BoursoramaScraper?): Boolean {
            return oldItem?.stockData?.name == newItem?.stockData?.name && oldItem?.getRedirectedLink() == newItem?.getRedirectedLink()
        }
    }) {
        fun sort() {
            beginBatchedUpdates()

            val copy = (size() - 1 downTo 0).map { removeItemAt(it) }
            copy.forEach { add(it) }

            endBatchedUpdates()
        }
    }

    inner class DataObjectHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val currency: TextView = itemView.findViewById(R.id.currency)
        val variation: TextView = itemView.findViewById(R.id.variation)
        val isin: TextView = itemView.findViewById(R.id.isin)
        val opening: TextView = itemView.findViewById(R.id.opening)
        val prevClosing: TextView = itemView.findViewById(R.id.prevClosing)
        val highest: TextView = itemView.findViewById(R.id.highest)
        val lowest: TextView = itemView.findViewById(R.id.lowest)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataObjectHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_card, parent, false)
        return DataObjectHolder(view)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
        val scraper = scrapers[position]
        holder.name.text = scraper.stockData?.name ?: scraper.getRedirectedLink()
        holder.price.text = scraper.stockData?.lastPrice
        holder.currency.text = scraper.stockData?.currency
        holder.variation.text = scraper.stockData?.variation
        holder.isin.text = scraper.stockData?.isin

        // Color variation
        holder.variation.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        if (scraper.stockData?.variation?.startsWith("-") == true) {
            holder.variation.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
        } else {
            holder.variation.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
        }

        holder.opening.text = scraper.stockData?.quotation?.opening
        holder.prevClosing.text = scraper.stockData?.quotation?.prevClosing
        holder.highest.text = scraper.stockData?.quotation?.highest
        holder.lowest.text = scraper.stockData?.quotation?.lowest
    }

    override fun getItemCount() = scrapers.size()
}
