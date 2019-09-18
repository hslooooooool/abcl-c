package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.form.db.entity.FormValueOfUser
import qsos.core.form.db.entity.Value
import qsos.core.form.view.hodler.FormChoseUserItemHolder
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import java.util.*

/**
 * @author : 华清松
 * 表单用户列表容器
 */
class FormUsersAdapter(users: ArrayList<FormUserEntity>) : BaseAdapter<FormUserEntity>(users) {

    private var chose = 0
    private var limitMin: Int? = 0
    private var limitMax: Int? = 0
    private var mOnChoseUserNum: OnTListener<Int>? = null

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
        Completable.fromAction {
            if (choseAll) {
                data.forEach {
                    if (!it.userCb) {
                        chose++
                        it.userCb = true
                        FormDatabase.getInstance().formItemValueDao.insert(
                                Value.newUser(FormValueOfUser(it.userName, it.userPhone, it.userAvatar, it.userId), it.formItemId!!)
                        )
                    }
                }
            } else {
                data.forEach {
                    if (it.userCb) {
                        chose--
                        it.userCb = false
                        FormDatabase.getInstance().formItemValueDao.deleteByFormItemIdAndUserDesc(it.formItemId!!, it.userPhone!!)
                    }
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mOnChoseUserNum?.back(chose)
                    notifyDataSetChanged()
                }
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormUserEntity> {
        return FormChoseUserItemHolder(view, this)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.form_user
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {}

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.form_user_ll -> {
                if (limitMax == 1) {
                    /*单选*/
                    Completable.fromAction {
                        FormDatabase.getInstance().formItemValueDao.deleteByFormItemId(data[position].formItemId)
                        FormDatabase.getInstance().formItemValueDao.insert(
                                Value.newUser(
                                        FormValueOfUser(
                                                data[position].userName,
                                                data[position].userPhone,
                                                data[position].userAvatar,
                                                data[position].userId
                                        ), data[position].formItemId!!
                                )
                        )
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                chose = 1
                                for ((index, user) in data.withIndex()) {
                                    user.userCb = index == position
                                }
                                notifyDataSetChanged()
                                if (chose == limitMax && limitMax == 1) {
                                    (mContext as Activity).finish()
                                }
                            }
                } else {
                    /*多选*/
                    if (data[position].userCb) {
                        Completable.fromAction {
                            FormDatabase.getInstance().formItemValueDao.deleteByFormItemIdAndUserDesc(data[position].formItemId!!, data[position].userPhone!!)
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    chose--
                                    data[position].userCb = false
                                    mOnChoseUserNum?.back(chose)
                                    notifyItemChanged(position)
                                }
                    } else {
                        if (chose == limitMax) {
                            ToastUtils.showToast(mContext, "最多选择 $limitMax 人")
                            return
                        }
                        Completable.fromAction {
                            FormDatabase.getInstance().formItemValueDao.insert(
                                    Value.newUser(
                                            FormValueOfUser(
                                                    data[position].userName,
                                                    data[position].userPhone,
                                                    data[position].userAvatar,
                                                    data[position].userId
                                            ), data[position].formItemId!!
                                    )
                            )
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    chose++
                                    data[position].userCb = true
                                    mOnChoseUserNum?.back(chose)
                                    notifyItemChanged(position)
                                }
                    }
                }
            }
        }
    }

}
