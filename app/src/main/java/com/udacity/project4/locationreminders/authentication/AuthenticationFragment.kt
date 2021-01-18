package com.udacity.project4.locationreminders.authentication

import com.udacity.project4.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationFragment: BaseFragment() {
    override val _viewModel: AuthenticationViewModel by viewModel()

}