package qsos.app.demo.tweet

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

    fun clear(success: (msg: String?) -> Unit, fail: (msg: String) -> Unit)

    fun put(em: EmployeeBeen, success: (em: EmployeeBeen) -> Unit, fail: (msg: String) -> Unit)
}