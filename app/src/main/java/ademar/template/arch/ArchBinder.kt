package ademar.template.arch

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchBinder @Inject constructor(
    application: Application,
) : FragmentLifecycleCallbacks(), ActivityLifecycleCallbacks, OnAttachStateChangeListener {

    private val activityBinders = mutableListOf<ActivityBinder>()
    private val fragmentBinders = mutableListOf<FragmentBinder>()
    private val viewBinders = mutableListOf<ViewBinder>()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <Command : Any, State : Any, Model : Any> bind(
        view: ArchView<Model, Command>,
        interactor: ArchInteractor<Command, State>,
        presenter: ArchPresenter<State, Model>,
    ) {
        when (view) {
            is Activity -> activityBinders.add(
                ActivityBinder(
                    view as ArchView<Any, Any>,
                    interactor as ArchInteractor<Any, Any>,
                    presenter as ArchPresenter<Any, Any>,
                )
            )
            is Fragment -> fragmentBinders.add(
                FragmentBinder(
                    view as ArchView<Any, Any>,
                    interactor as ArchInteractor<Any, Any>,
                    presenter as ArchPresenter<Any, Any>,
                )
            )
            is View -> {
                view.addOnAttachStateChangeListener(this)
                viewBinders.add(
                    ViewBinder(
                        WeakReference(view as ArchView<Any, Any>),
                        interactor as ArchInteractor<Any, Any>,
                        presenter as ArchPresenter<Any, Any>,
                    )
                )
                if (view.isAttachedToWindow) {
                    onViewAttachedToWindow(view)
                } else {
                    onViewDetachedFromWindow(view)
                }
            }
        }
    }

    // region Activity

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(this, false)
        }
    }

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) {
        activityBinders.filter { it.activity == activity }.onEach { binder ->
            binder.activity.subscriptions.add(
                binder.presenter.output.subscribe(binder.activity::render, Timber::e)
            )
            binder.presenter.bind(binder.interactor.output)
            binder.interactor.bind(binder.activity.output)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        activityBinders.filter { it.activity == activity }.onEach { binder ->
            binder.presenter.unbind()
            binder.interactor.unbind()
            binder.activity.subscriptions.clear()
        }
    }

    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(this)
        }
        activityBinders.removeAll(activityBinders.filter { it.activity == activity })
    }

    // endregion

    // region fragment

    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) = Unit
    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) = Unit
    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) = Unit
    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) = Unit
    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) = Unit
    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) = Unit
    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) = Unit

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        fragmentBinders.filter { it.fragment == f }.onEach { binder ->
            binder.fragment.subscriptions.add(
                binder.presenter.output.subscribe(binder.fragment::render, Timber::e)
            )
            binder.presenter.bind(binder.interactor.output)
            binder.interactor.bind(binder.fragment.output)
        }
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        fragmentBinders.filter { it.fragment == f }.onEach { binder ->
            binder.presenter.unbind()
            binder.interactor.unbind()
            binder.fragment.subscriptions.clear()
        }
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) = Unit
    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) = Unit
    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) = Unit

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        fragmentBinders.removeAll(fragmentBinders.filter { it.fragment == f })
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) = Unit

    // endregion

    // region view

    override fun onViewAttachedToWindow(view: View?) {
        if (view == null) return
        viewBinders.filter { it.view.get() == view }.onEach { binder ->
            val (viewRef, interactor, presenter) = binder
            val archView = viewRef.get() ?: return@onEach
            archView.subscriptions.add(presenter.output.subscribe(archView::render, Timber::e))
            presenter.bind(interactor.output)
            interactor.bind(archView.output)
        }
    }

    override fun onViewDetachedFromWindow(view: View?) {
        if (view == null) return
        viewBinders.filter { it.view.get() == view }.onEach { binder ->
            val (viewRef, interactor, presenter) = binder
            val archView = viewRef.get() ?: return@onEach
            presenter.unbind()
            interactor.unbind()
            archView.subscriptions.clear()
        }
        viewBinders.removeAll(viewBinders.filter { it.view.get() == null })
    }

    // endregion

    private data class ActivityBinder(
        val activity: ArchView<Any, Any>,
        val interactor: ArchInteractor<Any, Any>,
        val presenter: ArchPresenter<Any, Any>,
    )

    private data class FragmentBinder(
        val fragment: ArchView<Any, Any>,
        val interactor: ArchInteractor<Any, Any>,
        val presenter: ArchPresenter<Any, Any>,
    )

    private data class ViewBinder(
        val view: WeakReference<ArchView<Any, Any>>,
        val interactor: ArchInteractor<Any, Any>,
        val presenter: ArchPresenter<Any, Any>,
    )

}
