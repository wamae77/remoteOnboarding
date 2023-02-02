package com.cll.remoteonboarding.data.app_preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.cll.remoteonboarding.data.app_preference.SettingsPreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

public class SettingsPreferenceImpl : SettingsPreference {



}
