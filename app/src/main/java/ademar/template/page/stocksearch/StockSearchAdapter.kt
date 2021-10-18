package ademar.template.page.stocksearch

import ademar.template.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StockSearchAdapter(
    private val listener: (Contract.Item) -> Unit,
) : RecyclerView.Adapter<StockSearchViewHolder>() {

    private val data = mutableListOf<Contract.Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockSearchViewHolder {
        return StockSearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_stock_search, parent, false),
            listener,
        )
    }

    override fun onBindViewHolder(holder: StockSearchViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Contract.Item>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

}
