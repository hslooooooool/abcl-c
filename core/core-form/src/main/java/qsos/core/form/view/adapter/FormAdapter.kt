package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormFileType
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormItemType
import qsos.core.form.dbComplete
import qsos.core.form.view.hodler.*
import qsos.core.form.view.widget.dialog.BottomDialogUtils
import qsos.core.form.view.widget.dialog.OnDateListener
import qsos.core.form.view.widget.dialog.Operation
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 表单列表容器
 */
@SuppressLint("CheckResult")
class FormAdapter(
        formItems: ArrayList<FormItem>,
        private val mJob: CoroutineContext = Dispatchers.Main + Job()
) : BaseAdapter<FormItem>(formItems) {

    interface OnFileListener {
        fun getFile(type: FormFileType, position: Int)
    }

    var fileListener: OnFileListener? = null

    private var backListener: OnTListener<Boolean>? = null

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormItem> {
        when (viewType) {
            /**文本*/
            R.layout.form_item_text -> return FormItemTextHolder(view, this)
            /**输入*/
            R.layout.form_item_input -> return FormItemInputHolder(view, this)
            /**选项*/
            R.layout.form_item_check -> return FormItemCheckHolder(view, this)
            /**时间*/
            R.layout.form_item_time -> return FormItemTimeHolder(view, this)
            /**人员*/
            R.layout.form_item_users -> return FormItemUserHolder(view, this)
            /**附件*/
            R.layout.form_item_file -> return FormItemFileHolder(view, this)
            /**其它*/
            else -> return FormItemTextHolder(view, this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (data[position].valueType) {
            FormItemType.TEXT.tag -> return R.layout.form_item_text
            FormItemType.INPUT.tag -> return R.layout.form_item_input
            FormItemType.CHOOSE.tag -> return R.layout.form_item_check
            FormItemType.TIME.tag -> return R.layout.form_item_time
            FormItemType.USER.tag -> return R.layout.form_item_users
            FormItemType.FILE.tag -> return R.layout.form_item_file
            FormItemType.LOCATION.tag -> return R.layout.form_item_location
        }
        return R.layout.form_item_text
    }

    override fun getLayoutId(viewType: Int): Int = viewType

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        if (!data[position].editable) {
            return
        }
        when (view.id) {
            /**表单项提示*/
            R.id.form_item_title -> {
                data[position].notice?.let { ToastUtils.showToastLong(mContext, it) }
            }
            /**选择时间*/
            R.id.item_form_time -> {
                chooseTime(view, position)
            }
            /**选择人员*/
            R.id.tv_item_form_users_size -> {
                ARouter.getInstance().build(FormPath.FORM_ITEM_USERS)
                        .withLong(FormPath.FORM_ITEM_ID, data[position].id!!)
                        .navigation()
            }
            /**选择选项*/
            R.id.form_item_check -> {
                chooseCheck(position)
            }
            /**选择附件*/
            R.id.form_item_file_take_photo, R.id.form_item_file_take_album,
            R.id.form_item_file_take_video, R.id.form_item_file_take_audio,
            R.id.form_item_file_take_file -> {
                checkTakeFileLimit(view, position)
            }
            else -> {

            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}

    private fun chooseTime(view: View, position: Int) {
        val values = data[position].formItemValue!!.values!!
        val size = values.size
        var timeLimitMin: Date? = null
        data[position].formItemValue!!.value?.time?.timeLimitMin?.let {
            timeLimitMin = Date(it)
        }
        var timeLimitMax: Date? = null
        data[position].formItemValue!!.value?.time?.timeLimitMax?.let {
            timeLimitMax = Date(it)
        }
        if (size == 1) {
            val dateType = values[0].limitType

            var showDay = true
            if ("yyyy-MM-dd HH:mm" == dateType) {
                showDay = true
            } else if ("yyyy-MM-dd" == dateType) {
                showDay = true
            }
            backListener?.back(false)
            BottomDialogUtils.showRealDateChoseView(
                    view.context, "yyyy-MM-dd HH:mm" == dateType, showDay,
                    timeLimitMin, timeLimitMax, Date(values[0].time!!.timeStart),
                    object : OnDateListener {
                        override fun setDate(type: Int?, date: Date?) {
                            backListener?.back(true)
                            if (date != null) {
                                data[position].formItemValue!!.values!![0].time!!.timeStart = date.time
                                updateFormItemValueByPosition(position)
                            }
                        }
                    })
        } else if (size == 2) {
            val dateType1 = values[0].limitType
            val dateType2 = values[1].limitType
            var showDay1 = true
            if ("yyyy-MM-dd HH:mm" == dateType1) {
                showDay1 = true
            } else if ("yyyy-MM-dd" == dateType1) {
                showDay1 = true
            }
            var showDay2 = true
            if ("yyyy-MM-dd HH:mm" == dateType1) {
                showDay2 = true
            } else if ("yyyy-MM-dd" == dateType1) {
                showDay2 = true
            }
            backListener?.back(false)
            BottomDialogUtils.showRealDateChoseView(view.context,
                    "yyyy-MM-dd HH:mm" == dateType1, showDay1,
                    timeLimitMin, timeLimitMax, Date(values[0].time!!.timeStart),
                    object : OnDateListener {
                        override fun setDate(type: Int?, date: Date?) {
                            date?.let {
                                data[position].formItemValue!!.values!![0].time!!.timeStart = it.time
                                BottomDialogUtils.showRealDateChoseView(
                                        view.context, "yyyy-MM-dd HH:mm" == dateType2, showDay2,
                                        Date(values[0].time!!.timeStart), null, Date(values[1].time!!.timeStart),
                                        object : OnDateListener {
                                            override fun setDate(type: Int?, date: Date?) {
                                                backListener?.back(true)
                                                if (date != null) {
                                                    data[position].formItemValue!!.values!![1].time!!.timeStart = date.time
                                                    updateFormItemValueByPosition(position)
                                                }
                                            }
                                        })
                            }
                        }
                    }
            )
        }
    }

    private fun chooseCheck(position: Int) {
        /**单选*/
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
                        updateFormItemValueByPosition(position)
                    } else {
                        notifyItemChanged(position)
                    }
                }
            })
        } else {
            /**多选*/
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
                        updateFormItemValueByPosition(position)
                    } else {
                        notifyItemChanged(position)
                    }
                }
            })
        }
    }

    private fun checkTakeFileLimit(view: View, position: Int) {
        val limitMax = data[position].formItemValue!!.limitMax
        val valueSize: Int? = data[position].formItemValue!!.values?.size ?: 0
        if (limitMax != null && valueSize ?: 0 >= limitMax) {
            ToastUtils.showToast(view.context, "已达到添加数量限制")
        } else {
            fileListener?.getFile(when (view.id) {
                /**拍照*/
                R.id.form_item_file_take_photo -> FormFileType.CAMERA
                /**相册*/
                R.id.form_item_file_take_album -> FormFileType.ALBUM
                /**视频*/
                R.id.form_item_file_take_video -> FormFileType.VIDEO
                /**语音*/
                R.id.form_item_file_take_audio -> FormFileType.AUDIO
                /**文件*/
                else -> FormFileType.FILE
            }, position)
        }
    }

    private fun updateFormItemValueByPosition(position: Int) {
        CoroutineScope(mJob).dbComplete {
            db = { FormDatabase.getInstance().formItemValueDao.update(data[position].formItemValue!!.values!!) }
            onSuccess = { notifyItemChanged(position) }
            onFail = {
                ToastUtils.showToast(mContext, "更新失败")
                notifyItemChanged(position)
            }
        }
    }
}
