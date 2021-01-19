package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
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
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

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
    }

    private fun startAuthentication() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val intentBuilder = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)

        startActivityForResult(intentBuilder.build(), SIGN_IN_RESULT_CODE)
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
