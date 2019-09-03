package qsos.app.demo.data

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.data.BaseHttpLiveData
import qsos.lib.netservice.expand.retrofitByDef
import qsos.lib.netservice.expand.retrofitWithLiveDataByDef
import retrofit2.HttpException
import retrofit2.await
import vip.qsos.exception.GlobalException
import vip.qsos.exception.GlobalExceptionHelper
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 聊天数据获取
 * TweetRepository 内部中 MutableLiveData 的对象必须为 val 不可变对象，防止外部篡改，外部仅观察数据。
 */
@SuppressLint("CheckResult")
class TweetRepository(
        private val mCoroutineContext: CoroutineContext
) : ITweetRepo {

    val mDataTweetList: BaseHttpLiveData<List<EmployeeBeen>> = BaseHttpLiveData()

    val mDataUserInfo: BaseHttpLiveData<EmployeeBeen> = BaseHttpLiveData()

    override fun getOne() {
        CoroutineScope(mCoroutineContext).retrofitWithLiveDataByDef<EmployeeBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).one()
            data = mDataUserInfo
        }
    }

    override fun getList() {
        CoroutineScope(mCoroutineContext).retrofitWithLiveDataByDef<List<EmployeeBeen>> {
            api = ApiEngine.createService(ApiTweet::class.java).list()
            data = mDataTweetList
        }
    }

    override fun addOne(success: () -> Unit, fail: (msg: String) -> Unit) {
        CoroutineScope(mCoroutineContext).retrofitByDef<EmployeeBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).add()
            onSuccess {
                success()
            }
            onFailed { code, error ->
                Observable.just(error).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    fail(error)
                }
                GlobalExceptionHelper.caughtException(GlobalException.ServerException(code, error))
            }
        }
    }

    override fun delete(success: () -> Unit, fail: (msg: String) -> Unit) {
        ApiEngine.createService(ApiTweet::class.java).delete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.code == 200) {
                                success()
                            } else {
                                fail(it.msg ?: "删除失败")
                            }
                        },
                        {
                            GlobalExceptionHelper.caughtException(GlobalException.ServerException(500, it.message
                                    ?: "删除失败"))
                        }
                )
    }

    override fun clear(success: (msg: String?) -> Unit, fail: (msg: String) -> Unit) {
        CoroutineScope(mCoroutineContext).launch {
            val result = ApiEngine.createService(ApiTweet::class.java).clear()
            val list = ApiEngine.createService(ApiTweet::class.java).list().await()
            mDataTweetList.postValue(list)
            if (result.code == 200) success(result.msg) else {
                fail(result.msg ?: "清除失败")
                GlobalExceptionHelper.caughtException(GlobalException.ServerException(result.code, result.msg
                        ?: "清除失败"))
            }
        }
    }

    override fun put(em: EmployeeBeen, success: (em: EmployeeBeen) -> Unit, fail: (msg: String) -> Unit) {
        CoroutineScope(mCoroutineContext).launch(Dispatchers.Main) {
            val sApi = async(Dispatchers.IO) {
                ApiEngine.createService(ApiTweet::class.java).put(em).execute()
            }
            try {
                val response = sApi.await()
                if (response.isSuccessful && response.code() == 200 && response.body()?.data != null) {
                    success(response.body()!!.data!!)
                } else {
                    fail(response.errorBody().toString())
                }
            } catch (http: HttpException) {
                fail(http.message ?: "网络错误")
            } catch (e: Throwable) {
                fail(e.message ?: "请求错误")
            }
        }
    }
}