package qsos.app.demo.form

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import kotlinx.coroutines.CoroutineScope
import qsos.app.demo.R
import qsos.core.form.dbComplete
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnItemListener
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 表单用户列表容器
 */
class FormUsersAdapter(
        private val mJob: CoroutineContext,
        users: ArrayList<UserEntity>
) : BaseAdapter<UserEntity>(users), OnItemListener<Any?> {

    private var chose = 0
    private var limitMin: Int? = 0
    private var limitMax: Int? = 0
    private var mOnChoseUserNum: OnTListener<Int>? = null

    override fun getHolder(view: View, viewType: Int): BaseHolder<UserEntity> = FormChoseUserItemHolder(view, this)

    override fun getLayoutId(viewType: Int): Int = R.layout.app_form_users_item

    override fun onClick(view: View, position: Int, obj: Any?, long: Boolean) {
        if (!long) {
            when (view.id) {
                R.id.form_user_ll -> {
                    if (limitMax == 1) {
                        /**单选*/
                        CoroutineScope(mJob).dbComplete {
                            db = {
                                UserDatabase.getInstance().userDao.updateAllUncheck()
                                data[position].checked = true
                                UserDatabase.getInstance().userDao.update(data[position])
                            }
                            onSuccess = {
                                chose = 1
                                for ((index, user) in data.withIndex()) {
                                    user.checked = index == position
                                }
                                notifyDataSetChanged()
                                if (chose == limitMax && limitMax == 1) {
                                    (mContext as Activity).finish()
                                }
                            }
                            onFail = {
                                it.printStackTrace()
                                ToastUtils.showToast(mContext, "选择失败")
                            }
                        }
                    } else {
                        /**多选*/
                        if (data[position].checked) {
                            CoroutineScope(mJob).dbComplete {
                                db = {
                                    data[position].checked = false
                                    UserDatabase.getInstance().userDao.update(data[position])
                                }
                                onSuccess = {
                                    chose--
                                    mOnChoseUserNum?.back(chose)
                                    notifyItemChanged(position)
                                }
                                onFail = {
                                    it.printStackTrace()
                                    data[position].checked = true
                                    ToastUtils.showToast(mContext, "选择失败")
                                }
                            }
                        } else {
                            if (chose == limitMax) {
                                ToastUtils.showToast(mContext, "最多选择 $limitMax 人")
                                return
                            }
                            CoroutineScope(mJob).dbComplete {
                                db = {
                                    data[position].checked = true
                                    UserDatabase.getInstance().userDao.update(data[position])
                                }
                                onSuccess = {
                                    chose++
                                    data[position].checked = true
                                    mOnChoseUserNum?.back(chose)
                                    notifyItemChanged(position)
                                }
                                onFail = {
                                    it.printStackTrace()
                                    ToastUtils.showToast(mContext, "选择失败")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setLimit(chose: Int, limitMin: Int?, limitMax: Int?) {
        this.chose = chose
        this.limitMin = limitMin
        this.limitMax = limitMax
    }

    fun setOnChoseListener(onChoseUserNum: OnTListener<Int>) {
        this.mOnChoseUserNum = onChoseUserNum
    }

    /**选中当前列表所有用户或取消当前列表已选用户的选中*/
    @SuppressLint("CheckResult")
    fun changeAllChose(choseAll: Boolean) {
        if (choseAll) {
            CoroutineScope(mJob).dbComplete {
                db = {
                    UserDatabase.getInstance().userDao.updateAllChecked()
                    data.forEach {
                        if (!it.limitEdit && !it.checked) {
                            chose++
                            it.checked = true
                        }
                    }
                }
                onSuccess = {
                    notifyDataSetChanged()
                    mOnChoseUserNum?.back(chose)
                }
                onFail = {
                    it.printStackTrace()
                    ToastUtils.showToast(mContext, "选择失败")
                }
            }
        } else {
            CoroutineScope(mJob).dbComplete {
                db = {
                    UserDatabase.getInstance().userDao.updateAllUncheck()
                    data.forEach {
                        if (!it.limitEdit && it.checked) {
                            chose--
                            it.checked = false
                        }
                    }
                }
                onSuccess = {
                    notifyDataSetChanged()
                    mOnChoseUserNum?.back(chose)
                }
                onFail = {
                    it.printStackTrace()
                    ToastUtils.showToast(mContext, "选择失败")
                }
            }
        }
    }

}
