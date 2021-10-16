package ademar.template.page.stocksearch

import ademar.template.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StockSearchViewHolder(
    view: View,
) : RecyclerView.ViewHolder(view) {

    fun bind(item: Contract.Item) {
        itemView.findViewById<TextView>(R.id.name).text = item.name
        itemView.findViewById<TextView>(R.id.symbol).text = item.symbol
        itemView.findViewById<TextView>(R.id.type).text = item.type
    }

}
