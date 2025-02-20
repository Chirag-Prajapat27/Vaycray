package com.halfeaten.vaycray.ui.profile.currency

import android.view.LayoutInflater
import android.view.ViewGroup
import com.halfeaten.vaycray.GetCurrenciesListQuery
import com.halfeaten.vaycray.databinding.ItemLanguageBinding
import com.halfeaten.vaycray.util.binding.BindingAdapters
import com.halfeaten.vaycray.util.onClick

class CurrencyAdapter(private val clickListener: (String) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    var items = emptyList<GetCurrenciesListQuery.Result?>()
    var selectedItem = String()
    var languageName = String()
    var checkedPosition = -1

    fun setData(data: List<GetCurrenciesListQuery.Result?>) {
        items = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLanguageBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CurrencyAdapter.ViewHolder, position: Int) = holder.bind(items[position]!!, position, clickListener)

    inner class ViewHolder(val binding: ItemLanguageBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetCurrenciesListQuery.Result, position: Int, clickListener: (String) -> Unit) {
            if (item.symbol== BindingAdapters.getCurrencySymbol(item.symbol)){
                    binding.text = item.symbol
            } else {
                binding.text = BindingAdapters.getCurrencySymbol(item.symbol)+" "+item.symbol

            }
            if (selectedItem == item.symbol) {
                binding.radiobtnLanguages.isChecked = true
                checkedPosition = position
            } else {
                binding.radiobtnLanguages.isChecked = false
            }
            binding.radiobtnLanguages.onClick {
                item.symbol?.let {
                    selectedItem = it
                    notifyItemChanged(checkedPosition)
                    checkedPosition = position
                }
            }
            binding.executePendingBindings()
        }
    }

}
