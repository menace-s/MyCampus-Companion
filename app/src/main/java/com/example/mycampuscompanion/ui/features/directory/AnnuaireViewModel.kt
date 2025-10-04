package com.example.mycampuscompanion.ui.features.directory

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mycampuscompanion.data.AnnuaireRepository
import com.example.mycampuscompanion.data.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnuaireViewModel(private val repository: AnnuaireRepository) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    init {
        viewModelScope.launch {
            _contacts.value = repository.getContacts()
        }
    }
}

object AnnuaireViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        if (modelClass.isAssignableFrom(AnnuaireViewModel::class.java)) {
            val repository = AnnuaireRepository(application.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return AnnuaireViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}