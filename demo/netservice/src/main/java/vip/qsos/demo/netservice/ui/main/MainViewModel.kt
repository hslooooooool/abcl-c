package vip.qsos.demo.netservice.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import qsos.lib.base.base.BaseApplication
import vip.qsos.demo.netservice.data.main.UserInfo
import vip.qsos.demo.netservice.data.main.UserService

class MainViewModel : ViewModel() {

    private val _userInfo = MutableLiveData<UserInfo>()

    val userInfo: LiveData<UserInfo>
        get() = _userInfo

    fun loadUserInfo() = viewModelScope.launch {
        UserService.appContext = BaseApplication.appContext
        val user = UserService.INSTANCE.getUserInfo()
        when (user.code) {
            200 -> {

            }
            else -> {
                _userInfo.postValue(user.data)
            }
        }
    }

}