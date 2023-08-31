package com.cll.remoteonboarding.ui.featureFingerPrint

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cll.database.repositories.FingerPrintRepository
import com.cll.remoteonboarding.R
import com.cll.remoteonboarding.di.DefaultDispatcher
import com.cll.remoteonboarding.model.FingerPrint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class FingerPrintUiState(
    val list: List<FingerPrint>? = null
)

@HiltViewModel
class FragmentFingerPrintViewModel @Inject constructor(
    private val fingerPrintRepo: FingerPrintRepository, @ApplicationContext val context: Context,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(FingerPrintUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fingerPrintRepo.getPrints.collect {
                val p = it.map { i ->
                    i.toFingerPrint(getPositionIndex(i.nistPosCode))
                }
                _uiState.update { ui -> ui.copy(list = p) }
            }
        }
    }

    private fun getPositionIndex(pos: Int): String {
        val array = context.resources.getStringArray(R.array.fingerPositionIndex)
        return when (pos) {
            13 -> {
                array[1]
            }

            6 -> {
                array[2]
            }

            1 -> {
                array[3]
            }

            15 -> {
                array[4]
            }

            7 -> {
                array[5]
            }

            2 -> {
                array[6]
            }

            43 -> {
                array[7]
            }

            40 -> {
                array[8]
            }

            else -> {
                array[0]
            }
        }
    }

    fun addFingerPrints(fingerPrints: ArrayList<com.cll.FingerPrintModule.utils.FingerPrint>) {
        val prints = fingerPrints.map {
            com.cll.database.entities.EntityFingerPrint(
                0,
                userId = 0,
                imagePath = it.imagePath,
                nistPosCode = it.nistPosCode,
                quality = it.quality
            )
        }
        viewModelScope.launch {
            withContext(defaultDispatcher) {
                fingerPrintRepo.deleteByNistPosition(
                    prints[0].nistPosCode
                )
            }

            prints.map {
                fingerPrintRepo.insertFingerPrint(it)
            }
        }
    }
}


fun com.cll.database.entities.EntityFingerPrint.toFingerPrint(pos: String) = FingerPrint(
    id, imagePath, pos, quality
)

