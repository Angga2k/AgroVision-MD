package com.dicoding.agrovision.ui.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData


class HomapageViewModel : ViewModel() {

    val toastMessage = MutableLiveData<String>()

    fun onIconPersonClicked() {
        toastMessage.value = "Profil akan ditampilkan!"
    }

    fun onAlbumButtonClicked() {
        toastMessage.value = "Buka album..."
    }

    fun onCameraButtonClicked() {
        toastMessage.value = "Buka kamera..."
    }

    fun onCheckButtonClicked() {
        toastMessage.value = "Memulai pemeriksaan..."
    }
}
