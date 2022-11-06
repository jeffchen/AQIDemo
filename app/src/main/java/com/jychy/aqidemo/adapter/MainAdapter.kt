package com.jychy.aqidemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jychy.aqidemo.data.Record
import com.jychy.aqidemo.databinding.ItemEmptyBinding
import com.jychy.aqidemo.databinding.ItemHorizontalBinding
import com.jychy.aqidemo.databinding.ItemVerticalBinding
import com.jychy.aqidemo.viewmodel.MainViewModel

class MainAdapter(private val viewModel: MainViewModel, private val recyclerViewType: Int = 0) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "AQIDemo-MainAdapter"
        // type of recycler view item
        const val ITEM_VIEW_TYPE_EMPTY = 0;
        const val ITEM_VIEW_TYPE_NORMAL = 1;
        // type of recycler view
        const val RECYCLER_VIEW_TYPE_HORIZONTAL = 1;
        const val RECYCLER_VIEW_TYPE_VERTICAL = 2;
        const val RECYCLER_VIEW_TYPE_SEARCH = 3;
    }

    private var list: MutableLiveData<List<Record>> = when (recyclerViewType) {
        RECYCLER_VIEW_TYPE_HORIZONTAL -> viewModel.horizontalData
        RECYCLER_VIEW_TYPE_VERTICAL -> viewModel.verticalData
        RECYCLER_VIEW_TYPE_SEARCH -> viewModel.searchData
        else -> viewModel.liveData
    }

    override fun getItemViewType(position: Int): Int {
        // only search view need to show empty item
        return if (list.value?.isEmpty() != false && recyclerViewType == RECYCLER_VIEW_TYPE_SEARCH) {
            ITEM_VIEW_TYPE_EMPTY
        } else {
            ITEM_VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): RecyclerView.ViewHolder {
        if (itemViewType == ITEM_VIEW_TYPE_EMPTY) {
            return EmptyViewHolder.from(parent)
        }

        return when (recyclerViewType) {
            RECYCLER_VIEW_TYPE_HORIZONTAL -> HorizontalViewHolder.from(parent)
            RECYCLER_VIEW_TYPE_VERTICAL -> VerticalViewHolder.from(parent)
            RECYCLER_VIEW_TYPE_SEARCH -> VerticalViewHolder.from(parent)
            else -> VerticalViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> holder.bind(viewModel)
            is HorizontalViewHolder -> holder.bind(viewModel, list.value!![position])
            is VerticalViewHolder -> holder.bind(viewModel, list.value!![position])
        }
    }

    override fun getItemCount(): Int {
        val count = list.value?.count() ?: 0
        // if view type is search view, should returning item count 1 to show empty view
        return if (count == 0 && recyclerViewType == RECYCLER_VIEW_TYPE_SEARCH) 1 else count
    }

    open class EmptyViewHolder protected constructor(private val binding: ItemEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: MainViewModel) {
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmptyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEmptyBinding.inflate(layoutInflater, parent, false)

                return EmptyViewHolder(binding)
            }
        }
    }

    open class HorizontalViewHolder protected constructor(private val binding: ItemHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: MainViewModel, record: Record) {
            binding.viewModel = viewModel
            binding.record = record
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HorizontalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHorizontalBinding.inflate(layoutInflater, parent, false)

                return HorizontalViewHolder(binding)
            }
        }
    }

    open class VerticalViewHolder protected constructor(private val binding: ItemVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: MainViewModel, record: Record) {
            binding.viewModel = viewModel
            binding.record = record
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): VerticalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemVerticalBinding.inflate(layoutInflater, parent, false)

                return VerticalViewHolder(binding)
            }
        }
    }
}