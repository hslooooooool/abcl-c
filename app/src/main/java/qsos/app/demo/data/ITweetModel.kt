package qsos.app.demo.data

import qsos.lib.netservice.IBaseModel
import qsos.lib.netservice.data.BaseHttpLiveData

interface ITweetModel : IBaseModel, ITweetRepo {

    fun mOne(): BaseHttpLiveData<EmployeeBeen>

    fun mList(): BaseHttpLiveData<List<EmployeeBeen>>
}

interface ITweetRepo {

    fun getOne()

    fun getList()

    fun addOne(success: () -> Unit, fail: (msg: String) -> Unit = {})

    fun delete(success: () -> Unit, fail: (msg: String) -> Unit)
}