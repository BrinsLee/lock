package com.lock.locksmith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.repository.additem.IAddItemRepository
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddPasswordEvent
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddSecureNoteEvent
import com.lock.result.Error
import com.lock.state.Event
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

    /**
     * Used to update and emit error events.
     */
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()

    /**
     * Emits error events.
     */
    public val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    /**
     * current saving info state
     */
    private val _state: MutableLiveData<State> = MutableLiveData()

    /**
     * current saving info state
     */
    public val state: LiveData<State> = _state



    init {
        /*viewModelScope.launch(Dispatchers.IO) {
            fetchItemData()
        }*/
    }

    fun fetchItemData() {
        _itemData.postValue(repository.getItemData())
    }


    fun addItem(event: AddItemEvent) {
        _state.value = State.Loading
        when(event) {
            is AddPasswordEvent -> {
                repository.saveItemData(event.itemData)
            }

            is AddSecureNoteEvent -> TODO()
        }
    }




    public sealed class ErrorEvent(open val error: Error) {

        /**
         * Event for errors upon adding an item.
         */
        public data class AddItemError(override val error: Error) : ErrorEvent(error)

        /**
         * Event for errors upon deleting an item.
         */
        public data class DeleteItemError(override val error: Error) : ErrorEvent(error)

        /**
         * Event for errors upon updating an item.
         */
        public data class UpdateItemError(override val error: Error) : ErrorEvent(error)

    }


    public sealed class AddItemEvent(open val itemData: BaseData) {

        public class AddPasswordEvent(override val itemData: PasswordData) : AddItemEvent(itemData)

        public class AddSecureNoteEvent(override val itemData: BaseData) : AddItemEvent(itemData)

    }

    public sealed class State {
        /**
         * Signifies that is loading.
         */
        public object Loading : State() {
            override fun toString(): String = "Loading"
        }


        /**
         * Signifies that the item have successfully saved.
         *
         * @param message Contains Success information.
         */
        public data class Result(val message: String) : State()

    }
}