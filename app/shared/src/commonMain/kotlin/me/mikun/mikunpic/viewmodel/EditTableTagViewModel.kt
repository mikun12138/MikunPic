package me.mikun.mikunpic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mikun.mikunpic.client.Client
import me.mikun.mikunpic.dto.data.api.OhMyRouting.Manage.Pic.Random.TagFilter

class EditTableTagViewModel : ViewModel() {
    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags = _tags.asStateFlow()

    private val _tagsSelected = MutableStateFlow<List<String>>(emptyList())
    val tagsSelected = _tagsSelected.asStateFlow()

    private val _imageShowing = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val imageShowing = _imageShowing.asStateFlow()

    fun updateTags() {
        viewModelScope.launch {
            try {
                _tags.value = Client.searchTag(
                    count = Int.MAX_VALUE
                )?.let {
                    it.tags
                } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleTagsSelected(tag: String) {
        _tagsSelected.update { selected ->
            if (tag in selected) {
                selected - tag
            } else {
                selected + tag
            }
        }
    }

    fun flashTagsSelected() {
        _tagsSelected.update { selected ->
            selected.filter { it in tags.value }
        }
    }

    fun updateImageShowing(
        storageLabel: String,
    ) {
        viewModelScope.launch {
            try {
                _imageShowing.value = Client.randomPic(
                    count = 10,
                    tagFilter = tagsSelected.value.takeIf { it.isNotEmpty() }?.let {
                        TagFilter.All(it)
                    } ?: TagFilter.Any,
                    storageLabels = listOf(storageLabel)
                )?.label2Pics.orEmpty().flatMap { (storageLabel, pics) ->
                    pics.map { storageLabel to it.id }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
