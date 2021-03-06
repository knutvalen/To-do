package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.utils.AuthenticationState
import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel: AuthenticationViewModel by viewModels()

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAuthenticationBinding>(
            this,
            R.layout.activity_authentication
        )
        binding.viewModel = viewModel

        viewModel.eventStartAuthentication.observe(this) {
            if (it) {
                viewModel.onStartAuthenticationComplete()
                startAuthentication()
            }
        }

        viewModel.authenticationState.observe(this) { authenticationState ->
            Timber.i("authenticationState: $authenticationState")

            if (authenticationState == AuthenticationState.AUTHENTICATED) {
                val intent = Intent(this, RemindersActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
            }
        }


    }

    private fun startAuthentication() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers).build()

        startActivityForResult(intent, SIGN_IN_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != SIGN_IN_RESULT_CODE) {
            return
        }

        if (resultCode == Activity.RESULT_OK) {
            Timber.i("Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}")
        } else {
            Timber.i("Sign in unsuccessful ${IdpResponse.fromResultIntent(data)?.error?.errorCode}")
        }
    }

}
