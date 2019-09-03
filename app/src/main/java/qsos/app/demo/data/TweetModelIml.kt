package qsos.app.demo.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.lib.netservice.data.BaseHttpLiveData
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 推特数据 Model
 * TweetModelIml（ViewModel）在Activity不变的情况下，全局唯一实例
 */
class TweetModelIml : ITweetModel, ViewModel() {

    /**创建协程CoroutineContext，绑定生命周期*/
    override val mJob: CoroutineContext = Dispatchers.Main + Job()

    private val mTweetRepository: TweetRepository = TweetRepository(mJob)

    override fun mList(): BaseHttpLiveData<List<EmployeeBeen>> {
        return this.mTweetRepository.mDataTweetList
    }

    override fun mOne(): BaseHttpLiveData<EmployeeBeen> {
        return this.mTweetRepository.mDataUserInfo
    }

    override fun getOne() {
        mTweetRepository.getOne()
    }

    override fun getList() {
        mTweetRepository.getList()
    }

    override fun addOne(success: () -> Unit, fail: (msg: String) -> Unit) {
        mTweetRepository.addOne(success, fail)
    }

    override fun delete(success: () -> Unit, fail: (msg: String) -> Unit) {
        mTweetRepository.delete(success, fail)
    }

    override fun clear(success: (msg: String?) -> Unit, fail: (msg: String) -> Unit) {
        mTweetRepository.clear(success, fail)
    }

    override fun put(em: EmployeeBeen, success: (em: EmployeeBeen) -> Unit, fail: (msg: String) -> Unit) {
        mTweetRepository.put(em, success, fail)
    }

    override fun onCleared() {
        mJob.cancel()
        super.onCleared()
    }
}