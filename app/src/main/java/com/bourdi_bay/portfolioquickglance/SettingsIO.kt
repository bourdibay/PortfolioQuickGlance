package com.bourdi_bay.portfolioquickglance

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class SettingsIO(private val context: Context) {

    val filename = "stock_state.txt"

    fun save(urls: ArrayList<String>) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fd ->
            urls.forEach { url ->
                fd.write(url.toByteArray())
                fd.write("\n".toByteArray())
            }
        }
    }

    fun load(): ArrayList<String> {
        val urls = ArrayList<String>()

        try {
            context.openFileInput(filename).use { fd ->
                val inputStreamReader = InputStreamReader(fd)
                val bufferedReader = BufferedReader(inputStreamReader)
                var url: String? = null

                while ({ url = bufferedReader.readLine(); url }() != null) {
                    urls.add(url!!)
                }
            }
        } catch (_: Exception) {
        }

        return urls
    }

}