package com.lock.locksmith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lock.result.Error
import com.lock.state.Event
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
open class BaseViewModel: ViewModel() {


    /**
     * Used to update and emit error events.
     */
    protected  open val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()

    /**
     * Emits error events.
     */
    public val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents


    /**
     * current saving info state
     */
    protected open val _state: MutableLiveData<State> = MutableLiveData()

    /**
     * current saving info state
     */
    public val state: LiveData<State> = _state



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

        /**
         * Init passport error
         */
        public data class InitPassportError(override val error: Error) : ErrorEvent(error)

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

        /**
         * Signifies that the error occurred.
         */
        public data class Failure(val errorMessage: String) : State()

    }
}