package com.lock.locksmith.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.base.BaseDataListState
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.repository.passport.IPassportRepository
import com.lock.locksmith.viewmodel.BaseViewModel.ErrorEvent.InitPassportError
import com.lock.result.Error
import com.lock.state.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
@HiltViewModel
class PassportViewModel @Inject constructor (private val passportRepository: IPassportRepository): BaseViewModel() {




    fun loadPassport() = passportRepository.loadPassport()

    fun handlerEvent(event: PassportEvent) {
        viewModelScope.launch {
            _state.value = State.Loading
            delay(1000)
            when(event) {
                is PassportEvent.InitPassportEvent -> {
                    passportRepository.loadPassport().onSuccess {
                        _state.value = State.Result("init passport success")
                    }.onError {
                        createPassport()
                    }
                }
            }
        }
    }

    private fun createPassport() {
        try {
            passportRepository.createPassport().onSuccess {
                _state.value = State.Result("create passport success")
            }.onError {
                _state.value = State.Failure("create passport failure")
                _errorEvents.value = Event(InitPassportError(Error.GenericError("create passport fail")))
            }

        }catch (e: Exception) {
            _state.value = State.Failure("create passport failure")
            _errorEvents.value = Event(InitPassportError(Error.GenericError(e.message ?: "init passport fail")))
        }
    }


    private fun loadItemData() {

    }

    public sealed class PassportEvent() {

        public object InitPassportEvent : PassportEvent()

    }

}