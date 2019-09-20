package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.db
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormItemType
import qsos.core.form.db.entity.Value
import qsos.core.form.dbComplete
import qsos.core.form.utils.FormConfigHelper
import qsos.core.form.view.hodler.*
import qsos.core.form.view.widget.dialog.BottomDialogUtils
import qsos.core.form.view.widget.dialog.OnDateListener
import qsos.core.form.view.widget.dialog.Operation
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import timber.log.Timber
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 表单列表容器
 */
@SuppressLint("CheckResult")
class FormAdapter(
        formItems: ArrayList<FormItem>,
        private val mJob: CoroutineContext
) : BaseAdapter<FormItem>(formItems) {

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
            R.layout.form_item_file -> return FormItemFileHolder(view, mJob, this)
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
            R.id.form_item_file_take_camera, R.id.form_item_file_take_album,
            R.id.form_item_file_take_video, R.id.form_item_file_take_audio,
            R.id.form_item_file_take_file -> {
                takeFile(view, position)
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
            BottomDialogUtils.showRealDateChoseView(
                    view.context, "yyyy-MM-dd HH:mm" == dateType, showDay,
                    timeLimitMin, timeLimitMax, Date(values[0].time!!.timeStart),
                    object : OnDateListener {
                        override fun setDate(type: Int?, date: Date?) {
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
            BottomDialogUtils.setBottomChoseListView(mContext, operations, object : OnTListener<Operation> {
                override fun back(t: Operation) {
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
            BottomDialogUtils.setBottomSelectListView(mContext, data[position].title, operations, object : OnTListener<List<Operation>> {
                override fun back(t: List<Operation>) {
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

    /**文件选取*/
    private fun takeFile(view: View, position: Int) {
        val limitMax = data[position].formItemValue!!.limitMax
        val valueSize: Int = data[position].formItemValue!!.values?.size ?: 0
        /**可选文件数*/
        var canTakeSize = 0
        limitMax?.let { canTakeSize = limitMax - valueSize }
        if (canTakeSize < 1) {
            ToastUtils.showToast(view.context, "已达到添加数量限制")
        } else {
            var size: Int = valueSize
            when (view.id) {
                R.id.form_item_file_take_camera -> FormConfigHelper.takeCamera {
                    Timber.tag("表单拍照获取结果").i(Gson().toJson(it))
                    val v = Value.newFile(it, formItemId = data[position].id)
                    v.position = size++
                    addFormItemValueByPosition(position, v)
                }
                R.id.form_item_file_take_album -> FormConfigHelper.takeGallery(canTakeSize) {
                    Timber.tag("表单图库获取结果").i(Gson().toJson(it))
                    it.forEach { file ->
                        val v = Value.newFile(file, formItemId = data[position].id)
                        v.position = size++
                        addFormItemValueByPosition(position, v)
                    }
                }
                R.id.form_item_file_take_video -> FormConfigHelper.takeVideo(canTakeSize) {
                    Timber.tag("表单视频获取结果").i(Gson().toJson(it))
                    it.forEach { file ->
                        val v = Value.newFile(file, formItemId = data[position].id)
                        v.position = size++
                        addFormItemValueByPosition(position, v)
                    }
                }
                R.id.form_item_file_take_audio -> FormConfigHelper.takeAudio {
                    Timber.tag("表单音频获取结果").i(Gson().toJson(it))
                    val v = Value.newFile(it, formItemId = data[position].id)
                    v.position = size++
                    addFormItemValueByPosition(position, v)
                }
                R.id.form_item_file_take_file -> {
                    FormConfigHelper.takeFile(canTakeSize, data[position].formItemValue!!.limitTypeList!!) {
                        Timber.tag("表单文件获取结果").i(Gson().toJson(it))
                        it.forEach { file ->
                            val v = Value.newFile(file, formItemId = data[position].id)
                            v.position = size++
                            addFormItemValueByPosition(position, v)
                        }
                    }
                }
            }
        }
    }

    /**添加表单列表项值到对应列表项*/
    private fun addFormItemValueByPosition(position: Int, value: Value) {
        CoroutineScope(mJob).db<Long> {
            db = { FormDatabase.getInstance().formItemValueDao.insert(value) }
            onSuccess = {
                it?.let {
                    value.id = it
                    data[position].formItemValue!!.values!!.add(value)
                    data[position].formItemValue!!.values!!.sortBy { v -> v.position }
                    notifyItemChanged(position)
                }
            }
            onFail = {
                ToastUtils.showToast(mContext, "更新失败")
                notifyItemChanged(position)
            }
        }
    }

    /**更新对应表单列表项的所有值*/
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
