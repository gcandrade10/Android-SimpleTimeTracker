package com.example.util.simpletimetracker.feature_running_records.adapter.recordType

import android.view.ViewGroup
import com.example.util.simpletimetracker.core.adapter.BaseRecyclerAdapterDelegate
import com.example.util.simpletimetracker.core.adapter.BaseRecyclerViewHolder
import com.example.util.simpletimetracker.core.adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_running_records.R
import com.example.util.simpletimetracker.feature_running_records.viewData.RecordTypeAddViewData
import kotlinx.android.synthetic.main.item_record_type_add_layout.view.*

class RecordTypeAddAdapterDelegate(
    private val onItemClick: (() -> Unit)
) : BaseRecyclerAdapterDelegate() {

    override fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerViewHolder =
        RunningRecordsViewHolder(parent)

    inner class RunningRecordsViewHolder(parent: ViewGroup) :
        BaseRecyclerViewHolder(parent, R.layout.item_record_type_add_layout) {

        override fun bind(item: ViewHolderType) = with(itemView) {
            item as RecordTypeAddViewData

            viewRecordTypeAddItem.setOnClickListener {
                onItemClick.invoke()
            }
        }
    }
}