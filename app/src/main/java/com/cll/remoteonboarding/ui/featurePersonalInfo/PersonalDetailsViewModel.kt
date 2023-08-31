package com.cll.remoteonboarding.ui.featurePersonalInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cll.database.repositories.PersonsImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PersonalDetailsViewModel @Inject constructor(
    val p: PersonsImagesRepository
) : ViewModel() {
    companion object {
        const val TAG = "PersonalDetailsViewModeL"
    }

    var name = MutableStateFlow("")
        private set

    var idNumber = MutableStateFlow("")

    var serialNumber = MutableStateFlow("")
        private set

    var dob = MutableStateFlow("")
        private set

    var gender = MutableStateFlow("")
        private set

    var docType = MutableStateFlow("")
        private set

    var nationality = MutableStateFlow("")
        private set


    fun print() {
        Log.i(TAG, "print: ${name.value}")
        Log.i(TAG, "print: ${nationality.value}")

    }
}