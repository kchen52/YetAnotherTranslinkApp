package com.kchen52.yetanothertranslinkapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kchen52.yetanothertranslinkapp.data.BusListBus

class BusListRecyclerViewAdapter(private val busList: List<BusListBus>) :
    RecyclerView.Adapter<BusListRecyclerViewAdapter.BusListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BusListViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: BusListViewHolder, position: Int) {
        val bus = busList.getOrNull(position) ?: BusListBus("Invalid data", false)
        holder.bind(bus)
    }

    override fun getItemCount() = busList.size

    class BusListViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.bus_row, parent, false)) {
        private var routeName: TextView? = null
        private var checkBox: CheckBox? = null

        init {
            routeName = itemView.findViewById(R.id.busRowTextView)
            checkBox = itemView.findViewById(R.id.busRowCheckBox)
        }

        fun bind(busListBus: BusListBus) {
            routeName?.text = busListBus.name
            checkBox?.isChecked = busListBus.checked
        }
    }
}
