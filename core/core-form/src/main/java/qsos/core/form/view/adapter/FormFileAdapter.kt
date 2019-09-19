package qsos.core.form.view.adapter

import android.view.View
import kotlinx.coroutines.CoroutineScope
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.Value
import qsos.core.form.dbComplete
import qsos.core.form.utils.FormConfigHelper
import qsos.core.form.view.hodler.FormItemFileItemHolder
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.utils.ToastUtils
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 文件列表容器
 */
class FormFileAdapter(
        files: ArrayList<Value>,
        private val mJob: CoroutineContext
) : BaseAdapter<Value>(files) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<Value> = FormItemFileItemHolder(view, this)

    override fun getLayoutId(viewType: Int): Int = R.layout.form_item_file_item

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.iv_item_form_file_icon -> {
                /**预览*/
                FormConfigHelper.previewFile(position, data.map { it.file!! })
            }
            R.id.iv_item_form_file_delete -> {
                /**删除*/
                if (!data[position].limitEdit) {
                    CoroutineScope(mJob).dbComplete {
                        db = { FormDatabase.getInstance().formItemValueDao.delete(data[position]) }
                        onSuccess = {
                            data.removeAt(position)
                            notifyDataSetChanged()
                        }
                        onFail = {
                            ToastUtils.showToastLong(mContext, "删除失败 ${it.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}
}
