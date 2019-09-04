package qsos.core.form.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.View
import com.google.gson.Gson
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.form.view.hodler.FormUsersHolder
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 表单用户单选列表容器
 */
class SingleUsersAdapter(users: ArrayList<FormUserEntity>) : BaseAdapter<FormUserEntity>(users) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormUserEntity> {
        return FormUsersHolder(view, this)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.form_user
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.form_user_ll -> {
                if (data[position].userId == null) {
                    ToastUtils.showToast(mContext, "人员ID不存在，选择失败")
                } else {
                    val intent = Intent()
                    intent.putExtra("userJson", Gson().toJson(data[position]))
                    (mContext as Activity).setResult(FormPath.choseUserCode, intent)
                    (mContext as Activity).finish()
                }
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}
}
