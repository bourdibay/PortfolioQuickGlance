package com.bourdi_bay.portfolioquickglance

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var boursoramaLoader: BoursoramaLoader
    private lateinit var swipeDeleteDelegate: SwipeDeleteDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(this)
        swipeDeleteDelegate = SwipeDeleteDelegate(this, viewAdapter)
        boursoramaLoader = BoursoramaLoader(viewAdapter, findViewById(R.id.progress_bar)) {
            // dirty, re-sort all scrapers since the names have changed during the loading
            viewAdapter.scrapers.sort()
            // in case reload is done from SwipeRefreshLayout
            findViewById<SwipeRefreshLayout>(R.id.refreshView).isRefreshing = false
        }

        // Setup recycler view
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        ItemTouchHelper(SwipeToDeleteCallback(this) { position ->
            swipeDeleteDelegate.deleteItem(position)
            swipeDeleteDelegate.showUndoSnackbar()
        }).apply {
            attachToRecyclerView(recyclerView)
        }

        findViewById<SwipeRefreshLayout>(R.id.refreshView).setOnRefreshListener { boursoramaLoader.load() }

        setupFloatingActionButton()

        // Load saved urls
        boursoramaLoader.load()
    }

    fun saveBoursoramaUrls() {
        val urls =
            ArrayList<String>((viewAdapter.scrapers.size() - 1 downTo 0).map { viewAdapter.scrapers[it].getRedirectedLink() })
        SettingsIO(viewAdapter.context).save(urls)
    }

    private fun setupFloatingActionButton() {
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            MaterialDialog(this).show {
                input(hint = "ISIN", allowEmpty = false) { _, text ->
                    val nbUrls = viewAdapter.scrapers.size()

                    BoursoramaScraperTaskFromISIN(viewAdapter) {
                        val newNbUrls = viewAdapter.scrapers.size()
                        if (nbUrls != newNbUrls) {
                            saveBoursoramaUrls()
                        }
                    }.execute(text.toString())

                }
                positiveButton(R.string.submit)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sync -> {
                boursoramaLoader.load()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
