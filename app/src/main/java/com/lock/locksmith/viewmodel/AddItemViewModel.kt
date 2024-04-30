package com.lock.locksmith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.repository.additem.IAddItemRepository
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddPasswordEvent
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddSecureNoteEvent
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
class AddItemViewModel @Inject constructor(private val repository: IAddItemRepository): BaseViewModel() {

    private val _itemData = MutableLiveData<AddItemData>()
    var itemData : LiveData<AddItemData> = _itemData


    init {
        /*viewModelScope.launch(Dispatchers.IO) {
            fetchItemData()
        }*/
    }

    fun fetchItemData() {
        repository.getItemData().onSuccess {
            _itemData.postValue(it)
        }
    }


    fun handleEvent(event: AddItemEvent) {
        _state.value = State.Loading
        viewModelScope.launch(Dispatchers.IO) {
            when(event) {
                is AddPasswordEvent -> {
                    repository.saveItemData(event.itemData).onSuccess {
                        _state.postValue(State.Result(""))
                    }.onError {
                        _state.postValue(State.Failure(it.message))
                    }
                }

                is AddSecureNoteEvent -> TODO()
            }
        }

    }




    public sealed class AddItemEvent(open val itemData: BaseData) {

        public class AddPasswordEvent(override val itemData: PasswordData) : AddItemEvent(itemData)

        public class AddSecureNoteEvent(override val itemData: BaseData) : AddItemEvent(itemData)

    }


}