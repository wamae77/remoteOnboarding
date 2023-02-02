package com.cll.featurefacecapture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cll.database.entities.EntityPersonsImages
import com.cll.database.repositories.PersonsImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class FaceFragmentUiState(
    val image: EntityPersonsImages? = null, val loading: Boolean = false, var message: Int? = null
)

@HiltViewModel
class FaceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val personsImagesRepository: PersonsImagesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceFragmentUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserId()?.let {
                personsImagesRepository.getImageByTpe(EntityPersonsImages.IMAGE_TYPES.SELFIE.name)
                    .collect { img ->
                        _uiState.update {
                            it.copy(
                                image = img
                            )
                        }
                    }
            }
        }
    }

    fun updateSelfieImage(filename: String) {
        viewModelScope.launch {
            val image = uiState.value.image
            if (image != null) {
                personsImagesRepository.deleteImageById(image.uId)
            }
            getUserId()?.let {id->
                personsImagesRepository.insertImage(
                    EntityPersonsImages(
                        0, filename, EntityPersonsImages.IMAGE_TYPES.SELFIE.name, id.toLong()
                    )
                )
            }
        }
    }

    private fun getUserId(): Int? {
        return savedStateHandle[USER_ID]
    }

    companion object {
        const val USER_ID = "userId"
    }
}