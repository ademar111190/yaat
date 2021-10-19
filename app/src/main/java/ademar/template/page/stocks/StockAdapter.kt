package ademar.template.page.stocks

import ademar.template.page.stocks.tile.StockTileView
import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StockAdapter : RecyclerView.Adapter<StockViewHolder>() {

    private val data = mutableListOf<String>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        return StockViewHolder(StockTileView(parent.context))
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].hashCode().toLong()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Contract.Item>) {
        data.clear()
        data.addAll(items.map { it.symbol })
        notifyDataSetChanged()
    }

}
