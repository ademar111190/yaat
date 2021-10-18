package ademar.template.page.stocksearch

import android.app.Activity
import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.ref.WeakReference
import javax.inject.Inject

@ActivityScoped
class StockSearchNavigator @Inject constructor(
    @ActivityContext context: Context,
) {

    private val activityRef = WeakReference(context as Activity)

    fun close() {
        activityRef.get()?.finish()
    }

}
