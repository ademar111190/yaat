package ademar.template.page.stocks.tile

import ademar.template.R
import ademar.template.page.stocks.tile.Contract.Command
import android.view.View
import androidx.appcompat.widget.PopupMenu
import io.reactivex.rxjava3.subjects.Subject

class StockTilePopup(
    var symbol: String,
    private val output: Subject<Command>,
) : View.OnClickListener {

    override fun onClick(view: View?) {
        if (view == null) return
        val menu = PopupMenu(view.context, view)
        menu.inflate(R.menu.stock_tile_menu)
        menu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.stock_tile_menu_delete -> output.onNext(Command.Delete(symbol))
                else -> null
            } != null
        }
        menu.show()
    }

}
