package ademar.template.page.stocks

import ademar.template.page.stocksearch.StockSearchActivity
import android.app.Activity
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import java.lang.ref.WeakReference
import javax.inject.Inject

@FragmentScoped
class StockNavigator @Inject constructor(
    @ActivityContext context: Context,
) {

    private val activityRef = WeakReference(context as Activity)

    fun openSearch() {
        activityRef.get()?.let { activity ->
            activity.startActivity(Intent(activity, StockSearchActivity::class.java))
        }
    }

}
