package com.jychy.aqidemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jychy.aqidemo.adapter.MainAdapter
import com.jychy.aqidemo.databinding.ActivityMainBinding
import com.jychy.aqidemo.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AQIDemo-MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var searchAdapter: MainAdapter
    private lateinit var verticalAdapter: MainAdapter
    private lateinit var horizontalAdapter: MainAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setup data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this

        searchAdapter = MainAdapter(viewModel, MainAdapter.RECYCLER_VIEW_TYPE_SEARCH)
        verticalAdapter = MainAdapter(viewModel, MainAdapter.RECYCLER_VIEW_TYPE_VERTICAL)
        horizontalAdapter = MainAdapter(viewModel, MainAdapter.RECYCLER_VIEW_TYPE_HORIZONTAL)

        binding.searchView.adapter = searchAdapter
        binding.verticalRecord.adapter = verticalAdapter
        binding.horizontalRecord.adapter = horizontalAdapter

        binding.verticalRecord.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                (binding.verticalRecord.layoutManager as LinearLayoutManager).orientation
            )
        )
        // observe main recycler views
        viewModel.liveData.observe(this) {
            Log.i(TAG, "notifyDataSetChanged")
            horizontalAdapter.notifyDataSetChanged()
            verticalAdapter.notifyDataSetChanged()
        }
        // observe search views
        viewModel.searchData.observe(this) {
            searchAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // start query keyword
                viewModel.query(applicationContext, newText)
                return false
            }
        })
        searchItem.setOnActionExpandListener(object : OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                // search view focused, show search view
                binding.searchView.visibility = View.VISIBLE
                binding.horizontalRecord.visibility = View.GONE
                binding.verticalRecord.visibility = View.GONE

                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                // search view unfocused, show main recycler views
                binding.searchView.visibility = View.GONE
                binding.horizontalRecord.visibility = View.VISIBLE
                binding.verticalRecord.visibility = View.VISIBLE

                return true
            }

        })
        return true
    }
}