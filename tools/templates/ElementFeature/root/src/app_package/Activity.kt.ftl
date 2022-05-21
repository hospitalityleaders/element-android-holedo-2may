package ${escapeKotlinIdentifiers(packageName)}

import android.content.Context
import android.content.Intent
import com.google.android.material.appbar.MaterialToolbar
import com.holedo.app.R
import com.holedo.app.core.extensions.addFragment
import com.holedo.app.core.platform.ToolbarConfigurable
import com.holedo.app.core.platform.VectorBaseActivity

//TODO add this activity to manifest
class ${activityClass} : VectorBaseActivity(), ToolbarConfigurable {

    companion object {
	
		<#if createFragmentArgs>
		private const val EXTRA_FRAGMENT_ARGS = "EXTRA_FRAGMENT_ARGS"
		
		fun newIntent(context: Context, args: ${fragmentArgsClass}): Intent {
		     return Intent(context, ${activityClass}::class.java).apply {
		         putExtra(EXTRA_FRAGMENT_ARGS, args)
		      }
		}
		<#else>
        fun newIntent(context: Context): Intent {
            return Intent(context, ${activityClass}::class.java)
        }
		</#if>
    }

    override fun getLayoutRes() = R.layout.activity_simple

    override fun initUiAndData() {
        if (isFirstCreation()) {
			<#if createFragmentArgs>
			val fragmentArgs: ${fragmentArgsClass} = intent?.extras?.getParcelable(EXTRA_FRAGMENT_ARGS)
                                                   ?: return
            addFragment(views.simpleFragmentContainer.id, ${fragmentClass}::class.java, fragmentArgs)
			<#else>
			addFragment(views.simpleFragmentContainer.id, ${fragmentClass}::class.java)
			</#if>
        }
    }

    override fun configure(toolbar: MaterialToolbar) {
        configureToolbar(toolbar)
    }

}
