package ademar.template.page.stocksearch

import ademar.template.R
import ademar.template.page.stocksearch.Contract.Item
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StockSearchViewHolder(
    view: View,
    private val listener: (Item) -> Unit,
) : RecyclerView.ViewHolder(view) {

    private var lastItem: Item? = null

    init {
        view.findViewById<View>(R.id.root).setOnClickListener {
            lastItem?.let(listener)
        }
    }

    fun bind(item: Item) {
        lastItem = item
        itemView.findViewById<TextView>(R.id.name).text = item.name
        itemView.findViewById<TextView>(R.id.symbol).text = item.symbol
        itemView.findViewById<TextView>(R.id.type).text = item.type
    }

}
