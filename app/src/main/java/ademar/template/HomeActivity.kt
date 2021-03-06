package ademar.template

import ademar.template.page.dashboard.DashboardFragment
import ademar.template.page.settings.SettingsFragment
import ademar.template.page.stocks.StocksFragment
import ademar.template.usecase.InitialPage
import ademar.template.usecase.InitialPagePreference
import ademar.template.widget.Reselectable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var initialPage: InitialPagePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_home)
        setUpNavigation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.isEmpty()) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    private fun setUpNavigation() {
        val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigation.setOnItemReselectedListener { menu ->
            val current = supportFragmentManager.fragments.lastOrNull()
            val itemId = menu.itemId
            if (current is Reselectable) current.onReselected()
            if (current == null && itemId == R.id.navigation_dashboard) {
                val fragment = DashboardFragment()
                supportFragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
                    .addToBackStack("$fragment")
                    .commitAllowingStateLoss()
            }
        }
        navigation.setOnItemSelectedListener { menu ->
            val fragment = when (menu.itemId) {
                R.id.navigation_dashboard -> {
                    supportFragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
                    DashboardFragment()
                }
                R.id.navigation_settings -> SettingsFragment()
                R.id.navigation_stocks -> StocksFragment()
                else -> null
            }
            if (fragment != null) {
                val transaction = supportFragmentManager.beginTransaction()
                if (fragment is DashboardFragment) {
                    transaction.replace(R.id.content, fragment)
                } else {
                    transaction.add(R.id.content, fragment)
                }
                transaction.addToBackStack("$fragment")
                transaction.commitAllowingStateLoss()
            }
            fragment != null
            false
        }
        navigation.selectedItemId = R.id.navigation_dashboard
        supportFragmentManager.addOnBackStackChangedListener {
            val id = when (supportFragmentManager.fragments.lastOrNull()) {
                is DashboardFragment -> R.id.navigation_dashboard
                is SettingsFragment -> R.id.navigation_settings
                is StocksFragment -> R.id.navigation_stocks
                else -> null
            }
            if (id != null) {
                navigation.menu.findItem(id)?.isChecked = true
            }
        }
        val initial = when (intent?.extras?.getString("INITIAL_ACTION", null)) {
            "ademar.template.action.dashboard" -> R.id.navigation_dashboard
            "ademar.template.action.settings" -> R.id.navigation_settings
            "ademar.template.action.stocks" -> R.id.navigation_stocks
            else -> null
        }
        if (initial != null) {
            navigation.selectedItemId = initial
        } else {
            subscriptions.add(
                initialPage.get().map { page ->
                    when (page) {
                        InitialPage.STOCKS -> R.id.navigation_stocks
                        InitialPage.SETTINGS -> R.id.navigation_settings
                        else -> R.id.navigation_dashboard
                    }
                }.subscribe({ id ->
                    navigation.selectedItemId = id
                }, Timber::e)
            )
        }
    }

}
