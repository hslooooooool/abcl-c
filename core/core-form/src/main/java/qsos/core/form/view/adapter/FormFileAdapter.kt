package qsos.core.form.view.adapter

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.Value
import qsos.core.form.dbComplete
import qsos.core.form.view.hodler.FormFileHolder
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 文件列表容器
 */
class FormFileAdapter(files: ArrayList<Value>)
    : BaseAdapter<Value>(files) {
    private val mJob = Dispatchers.Main + Job()

    override fun getHolder(view: View, viewType: Int): BaseHolder<Value> {
        return FormFileHolder(view, this)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.form_item_file_item
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.iv_item_form_file_delete -> {
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
