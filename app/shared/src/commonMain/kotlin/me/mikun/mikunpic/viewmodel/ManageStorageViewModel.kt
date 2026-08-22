package me.mikun.mikunpic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.Storage

class ManageStorageViewModel : ViewModel() {
    private val _storages = MutableStateFlow<List<Storage>>(emptyList())
    val storages = _storages.asStateFlow()

    init {
        flashStorages()
    }

    fun flashStorages() {
        viewModelScope.launch {
            try {
                val response = Client.fetchStorages()
                _storages.value = response?.storages ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
