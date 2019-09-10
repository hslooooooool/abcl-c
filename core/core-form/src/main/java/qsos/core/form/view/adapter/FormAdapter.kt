package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormItemType
import qsos.core.form.view.hodler.*
import qsos.core.form.view.widget.dialog.BottomDialogUtils
import qsos.core.form.view.widget.dialog.OnDateListener
import qsos.core.form.view.widget.dialog.Operation
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import java.util.*

/**
 * @author : 华清松
 * @notice : 表单列表容器
 */
@SuppressLint("CheckResult")
class FormAdapter(formItems: ArrayList<FormItem>) : BaseAdapter<FormItem>(formItems) {

    interface OnFileListener {
        fun getFile(type: String, position: Int)
    }

    private var fileListener: OnFileListener? = null

    fun setOnFileListener(listener: OnFileListener) {
        this.fileListener = listener
    }

    private var backListener: OnTListener<Boolean>? = null

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormItem> {
        when (viewType) {
            /*文本*/
            R.layout.form_item_text -> return ItemFormTextHolder(view, this)
            /*输入*/
            R.layout.form_item_input -> return ItemFormInputHolder(view, this)
            /*选项*/
            R.layout.form_item_check -> return ItemFormCheckHolder(view, this)
            /*时间*/
            R.layout.form_item_time -> return ItemFormTimeHolder(view, this)
            /*人员*/
            R.layout.form_item_users -> return ItemFormUserHolder(view, this)
            /*附件*/
            R.layout.form_item_file -> return ItemFormFileHolder(view, this)
            /*其它*/
            else -> return ItemFormTextHolder(view, this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (data[position].valueType) {
            /*文本*/
            FormItemType.TEXT.tag -> return R.layout.form_item_text
            /*输入*/
            FormItemType.INPUT.tag -> return R.layout.form_item_input
            /*选项*/
            FormItemType.CHOOSE.tag -> return R.layout.form_item_check
            /*时间*/
            FormItemType.TIME.tag -> return R.layout.form_item_time
            /*人员*/
            FormItemType.USER.tag -> return R.layout.form_item_users
            /*附件*/
            FormItemType.FILE.tag -> return R.layout.form_item_file
            /*位置*/
            FormItemType.LOCATION.tag -> return R.layout.form_item_location
        }
        return R.layout.form_item_text
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        if (!data[position].editable) {
            return
        }
        when (view.id) {
            /**表单项提示*/
            R.id.item_form_key -> {
                //TODO
            }
            /**选择时间*/
            R.id.item_form_time -> {
                chooseTime(view, position)
            }
            /**表单输入项*/
            R.id.item_form_input -> {
                ARouter.getInstance().build(FormPath.FORM_ITEM_INPUT)
                        .withLong("item_id", data[position].id!!)
                        .navigation()
            }
            /**选择人员*/
            R.id.tv_item_form_users_size -> {
                ARouter.getInstance().build(FormPath.FORM_ITEM_USERS)
                        .withLong(FormPath.FORM_ITEM_ID, data[position].id!!)
                        .navigation()
            }
            /**选择选项*/
            R.id.form_item_check -> {
                choose(position)
            }
            /**选择附件*/
            R.id.form_item_file_take_photo, R.id.form_item_file_take_album, R.id.form_item_file_take_video, R.id.form_item_file_take_audio -> {
                checkTakeLimit(view, position)
            }
            else -> {

            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}

    private fun chooseTime(view: View, position: Int) {
        var values = data[position].formItemValue?.values
        values = values ?: arrayListOf()
        val size = values.size
        if (size == 1) {
            val date = values[0].limitType

            var showDay = true
            if ("yyyy-MM-dd HH:mm" == date) {
                showDay = true
            } else if ("yyyy-MM-dd" == date) {
                showDay = true
            }
            backListener?.back(false)
            BottomDialogUtils.showRealDateChoseView(view.context,
                    "yyyy-MM-dd HH:mm" == date, showDay,
                    null, null, Date(values[0].time!!.timeStart), object : OnDateListener {
                override fun setDate(type: Int?, date: Date?) {
                    backListener?.back(true)
                    if (date != null) {
                        data[position].formItemValue!!.values!![0].time!!.timeStart = date.time

                        Completable.fromAction {
                            FormDatabase.getInstance().formItemValueDao.update(data[position].formItemValue!!.values!!)
                        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                {
                                    notifyItemChanged(position)
                                },
                                {
                                    ToastUtils.showToast(mContext, "选择失败")
                                    notifyItemChanged(position)
                                }
                        )
                    }
                }
            }

            )
        } else if (size == 2) {
            val date1 = values[0].limitType
            val date2 = values[1].limitType
            var showDay1 = true
            if ("yyyy-MM-dd HH:mm" == date1) {
                showDay1 = true
            } else if ("yyyy-MM-dd" == date1) {
                showDay1 = true
            }
            var showDay2 = true
            if ("yyyy-MM-dd HH:mm" == date1) {
                showDay2 = true
            } else if ("yyyy-MM-dd" == date1) {
                showDay2 = true
            }
            backListener?.back(false)
            BottomDialogUtils.showRealDateChoseView(view.context,
                    "yyyy-MM-dd HH:mm" == date1, showDay1,
                    null, null, Date(values[0].time!!.timeStart),
                    object : OnDateListener {
                        override fun setDate(type: Int?, date: Date?) {
                            if (date != null) {
                                data[position].formItemValue!!.values!![0].time!!.timeStart = date.time
                                BottomDialogUtils.showRealDateChoseView(view.context, "yyyy-MM-dd HH:mm" == date2,
                                        showDay2,
                                        Date(values[0].time!!.timeStart), null, Date(values[1].time!!.timeStart),
                                        object : OnDateListener {
                                            override fun setDate(type: Int?, date: Date?) {
                                                backListener?.back(true)
                                                if (date != null) {
                                                    data[position].formItemValue!!.values!![1].time!!.timeStart = date.time
                                                    Completable.fromAction {
                                                        FormDatabase.getInstance().formItemValueDao.update(data[position].formItemValue!!.values!!)
                                                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                                            {
                                                                notifyItemChanged(position)
                                                            },
                                                            {
                                                                ToastUtils.showToast(mContext, "选择失败")
                                                                notifyItemChanged(position)
                                                            }
                                                    )
                                                }
                                            }
                                        })
                            }
                        }
                    }
            )
        }

    }

    private fun choose(position: Int) {
        // 单选
        if (data[position].formItemValue!!.limitMax == 1) {
            val operations = arrayListOf<Operation>()
            val values = data[position].formItemValue!!.values
            values!!.forEach {
                val operation = Operation()
                operation.key = it.check!!.ckName
                operation.value = it.id
                operation.isCheck = it.check!!.ckChecked
                operations.add(operation)
            }
            backListener?.back(false)
            BottomDialogUtils.setBottomChoseListView(mContext, operations, object : OnTListener<Operation> {
                override fun back(t: Operation) {
                    backListener?.back(true)
                    data[position].formItemValue!!.values!!.forEach {
                        it.check!!.ckChecked = it.id == t.value
                    }
                    if (data[position].formItemValue!!.values!!.isNotEmpty()) {
                        Completable.fromAction {
                            FormDatabase.getInstance().formItemValueDao.update(data[position].formItemValue!!.values!!)
                        }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            notifyItemChanged(position)
                                        },
                                        {
                                            ToastUtils.showToast(mContext, "选择失败")
                                            notifyItemChanged(position)
                                        }
                                )
                    } else {
                        notifyItemChanged(position)
                    }
                }
            })
        } else {
            // 多选
            val operations = arrayListOf<Operation>()
            val values = data[position].formItemValue!!.values
            values!!.forEach {
                val operation = Operation()
                operation.key = it.check!!.ckName
                operation.value = it.id
                operation.isCheck = it.check!!.ckChecked
                operations.add(operation)
            }
            backListener?.back(false)
            BottomDialogUtils.setBottomSelectListView(mContext, data[position].title, operations, object : OnTListener<List<Operation>> {
                override fun back(t: List<Operation>) {
                    backListener?.back(true)
                    data[position].formItemValue!!.values!!.forEach { value ->
                        t.forEach {
                            if (value.id == it.value) {
                                value.check!!.ckChecked = it.isCheck
                            }
                        }
                    }
                    if (data[position].formItemValue!!.values!!.isNotEmpty()) {
                        Completable.fromAction {
                            FormDatabase.getInstance().formItemValueDao.update(data[position].formItemValue!!.values!!)
                        }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            notifyItemChanged(position)
                                        },
                                        {
                                            ToastUtils.showToast(mContext, "选择失败")
                                            notifyItemChanged(position)
                                        }
                                )
                    } else {
                        notifyItemChanged(position)
                    }
                }
            })
        }
    }

    private fun checkTakeLimit(view: View, position: Int) {
        val limitMax = data[position].formItemValue!!.limitMax
        val valueSize: Int? = data[position].formItemValue!!.values?.size ?: 0
        if (limitMax != null && valueSize ?: 0 >= limitMax) {
            ToastUtils.showToast(view.context, "已达到添加数量限制")
        } else {
            fileListener?.getFile(
                    when (view.id) {
                        /**拍照*/
                        R.id.form_item_file_take_photo -> "PHOTO"
                        /**相册*/
                        R.id.form_item_file_take_album -> "IMAGE"
                        /**视频*/
                        R.id.form_item_file_take_video -> "VIDEO"
                        /**语音*/
                        R.id.form_item_file_take_audio -> "AUDIO"
                        else -> ""
                    },
                    position
            )
        }
    }
}
