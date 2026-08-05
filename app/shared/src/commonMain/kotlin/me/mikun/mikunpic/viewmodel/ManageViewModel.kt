package me.mikun.mikunpic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageViewModel: ViewModel() {
    private val _currentStorageLabel = MutableStateFlow("")
    val currentStorageLabel = _currentStorageLabel.asStateFlow()

    private val _isLockStorage = MutableStateFlow(false)
    val isLockStorage = _isLockStorage.asStateFlow()

    fun switchStorage(
        storageLabel: String
    ) {
        if (isLockStorage.value) {
            return
        }

        viewModelScope.launch {
            _currentStorageLabel.value = storageLabel
        }
    }

    fun lockStorage(
        flag: Boolean
    ) {
        _isLockStorage.value = flag
    }





}