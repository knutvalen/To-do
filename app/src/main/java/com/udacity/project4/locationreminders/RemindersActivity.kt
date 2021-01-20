package com.udacity.project4.locationreminders

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R
import kotlinx.android.synthetic.main.activity_reminders.*
import timber.log.Timber

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    private var isAtStartDestination = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        val navController = findNavController(R.id.nav_host_fragment)

        onBackPressedDispatcher.addCallback(this) {
            Timber.i("isAtStartDestination is $isAtStartDestination")

            if (isAtStartDestination) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                navController.popBackStack()
            }
        }

        navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, _ ->
            isAtStartDestination = navDestination.id == navController.graph.startDestination
            Timber.i("isAtStartDestination set to $isAtStartDestination")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
