package qsos.core.form.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.form_users.*
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.form.view.adapter.SingleUsersAdapter
import qsos.core.form.view.other.FormItemDecoration
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * @description : 表单用户选择
 */
@Route(group = FormPath.FORM, path = FormPath.ITEM_USER)
class UserSingleChoseActivity : AbsFormActivity() {
    /**表单数据实现类*/
    private lateinit var formModelIml: FormModelIml

    @Autowired(name = "item_id")
    @JvmField
    var itemId: Long? = 0

    @Autowired(name = "connect_id")
    @JvmField
    var connectId: String? = ""

    private var item: FormItem? = null
    private var mList = ArrayList<FormUserEntity>()

    private var mAdapter: BaseAdapter<FormUserEntity>? = null
    private var manager: LinearLayoutManager? = null

    override val layoutId: Int = R.layout.form_users
    override val reload: Boolean = false

    override fun initData(savedInstanceState: Bundle?) {
        formModelIml = FormModelIml(FormRepository(mContext))
    }

    override fun initView() {
        mAdapter = SingleUsersAdapter(mList)
        manager = LinearLayoutManager(mContext)
        form_user_rv.layoutManager = manager
        form_user_rv.addItemDecoration(FormItemDecoration())
        form_user_rv.adapter = mAdapter

        form_user_search.setOnClickListener {
            getData()
        }

        formModelIml.formRepo.dbFormItem.observe(this, Observer {
            item = it
            formModelIml.getUsers(connectId, item!!, form_users_et.text.toString())
        })
        formModelIml.formRepo.userList.observe(this, Observer {
            if (it != null) {
                mList.clear()
                mList.addAll(it)
                mAdapter!!.notifyDataSetChanged()
            } else {
                ToastUtils.showToast(this, "查询可选列表失败")
            }
        })

        getData()

    }

    override fun getData() {
        if (itemId != null) {
            formModelIml.getFormItemByDB(itemId!!)
        }
    }

}
