package ademar.template.page.stocks

import ademar.template.page.stocks.tile.StockTileView
import androidx.recyclerview.widget.RecyclerView

class StockViewHolder(
    private val tile: StockTileView,
) : RecyclerView.ViewHolder(tile) {

    fun bind(symbol: String) {
        tile.bind(symbol)
    }

}
