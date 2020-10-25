package com.kchen52.yetanothertranslinkapp.buslist

import android.app.Activity
import android.app.Service
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kchen52.yetanothertranslinkapp.BusListRecyclerViewAdapter
import com.kchen52.yetanothertranslinkapp.R
import com.kchen52.yetanothertranslinkapp.data.BusListBus
import com.kchen52.yetanothertranslinkapp.map.MapConstants
import com.kchen52.yetanothertranslinkapp.map.MapsActivityViewModel
import com.kchen52.yetanothertranslinkapp.map.MapsActivityViewModelFactory
import kotlinx.android.synthetic.main.activity_bus_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class BusListActivity: AppCompatActivity() {
    // TODO: Use a separate shared prefs name for this activity?
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private val viewModel: BusListViewModel by viewModels { BusListViewModelFactory(applicationContext, coroutineScope) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_list)
        val busList = mutableListOf<BusListBus>()
        busListRecyclerView.layoutManager = LinearLayoutManager(this)
        busListRecyclerView.adapter = BusListRecyclerViewAdapter(busList)

        coroutineScope.launch {
            viewModel.getRequestedBuses().collect {
                withContext(Dispatchers.Main) {
                    busList.clear()
                    busList.addAll(it)
                    // TODO: Don't recreate this every time?
                    //busListRecyclerView.adapter?.notifyDataSetChanged()
                    busListRecyclerView.adapter = BusListRecyclerViewAdapter(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            openSearchBar()
        }
        return true
    }

    private fun openSearchBar() {
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true);
            it.setCustomView(R.layout.search_bar);

            val searchEditText = it.customView.findViewById(R.id.etSearch) as EditText
            searchEditText.hint = "Input text here";
            //searchEditText.addTextChangedListener(new SearchWatcher());
            searchEditText.requestFocus();

            val inputMethodManager = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(searchEditText, 0);
        }
    }

}