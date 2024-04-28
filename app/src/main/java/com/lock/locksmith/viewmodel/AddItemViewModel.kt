package com.lock.locksmith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.repository.additem.IAddItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
@HiltViewModel
class AddItemViewModel @Inject constructor(private val repository: IAddItemRepository): ViewModel() {

    private val _itemData = MutableLiveData<AddItemData>()
    var itemData : LiveData<AddItemData> = _itemData

    init {
        /*viewModelScope.launch(Dispatchers.IO) {
            fetchItemData()
        }*/
    }

    suspend fun fetchItemData() {
        _itemData.postValue(repository.getItemData())
    }
}