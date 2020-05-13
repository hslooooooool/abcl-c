package qsos.app.demo.form

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.app_form_users.*
import kotlinx.coroutines.CoroutineScope
import qsos.app.demo.AppPath
import qsos.app.demo.R
import qsos.core.form.FormPath
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.IFormModel
import qsos.core.form.db
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormValueOfUser
import qsos.core.form.db.entity.Value
import qsos.core.form.dbComplete
import qsos.core.form.view.other.FormItemDecoration
import qsos.core.lib.base.AbsDisposeActivity
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.BaseUtils

/**
 * @author : 华清松
 * 表单用户选择
 */
@SuppressLint("SetTextI18n")
@Route(group = AppPath.GROUP, path = AppPath.FORM_ITEM_USERS)
class FormUserChoseActivity : AbsDisposeActivity(R.layout.app_form_users, false) {

    private val mFormModel: IFormModel = FormModelIml()

    @Autowired(name = AppPath.FORM_ITEM_ID)
    @JvmField
    var formItemId: Long? = 0

    private var chose = 0
    private var limitMin: Int? = 0
    private var limitMax: Int? = 0

    private var mFormItem: FormItem? = null
    private var mAdapter: FormUsersAdapter? = null

    /**可选用户列表*/
    private var mList = ArrayList<UserEntity>()

    /**已选用户*/
    private var mCheckedUser = ArrayList<Value>()
    private var manager: LinearLayoutManager? = null

    override fun initData(savedInstanceState: Bundle?) {}

    override fun initView() {
        mAdapter = FormUsersAdapter(mJob, mList)
        mAdapter?.setOnChoseListener(object : OnTListener<Int> {
            override fun back(t: Int) {
                chose = t
                changeChoseUser()
            }
        })
        manager = LinearLayoutManager(mContext)
        form_user_rv.layoutManager = manager
        form_user_rv.addItemDecoration(FormItemDecoration())
        form_user_rv.adapter = mAdapter

        tv_form_user_chose_all.setOnClickListener {
            mAdapter?.changeAllChose(true)
        }
        form_user_chose_cancel.setOnClickListener {
            mAdapter?.changeAllChose(false)
        }

        form_users_et.setOnTouchListener(View.OnTouchListener { _, _ ->
            form_users_et.isFocusable = true
            form_users_et.isFocusableInTouchMode = true
            return@OnTouchListener false
        })

        form_users_et.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                BaseUtils.hideKeyboard(this)
                form_users_et.isFocusable = false
                form_users_et.isFocusableInTouchMode = false
                getData()
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        form_user_chose_sure.setOnClickListener {
            finish()
        }

        initUser()
    }

    override fun getData(loadMore: Boolean) {
        /**获取可选用户列表数据*/
        formItemId?.let {
            CoroutineScope(mJob).db<List<UserEntity>> {
                db = {
                    val search = form_users_et.text.toString().trim()
                    val all: List<UserEntity> = if (TextUtils.isEmpty(search)) {
                        UserDatabase.getInstance().userDao.findAll()
                    } else {
                        UserDatabase.getInstance().userDao.findUserByKey(search)
                    }

                    commitUser(mCheckedUser, all)
                }
                onSuccess = {
                    it?.let {
                        setDate(it)
                    }
                }
            }
        }
    }

    private fun initUser() {
        formItemId?.let {
            CoroutineScope(mJob).dbComplete {
                db = {
                    mFormItem = mFormModel.getFormItem(formItemId!!)
                    mCheckedUser = mFormItem?.formItemValue?.values
                            ?: arrayListOf()
                    chose = mCheckedUser.size
                    limitMin = mFormItem?.formItemValue?.limitMin
                    limitMax = mFormItem?.formItemValue?.limitMax

                    UserDatabase.getInstance().userDao.deleteAll()
                    for (i in 0..100) {
                        UserDatabase.getInstance().userDao.insert(
                                UserEntity(userId = "000$i", userName = "用户$i", userDesc = "1822755555$i", userAvatar = "http://www.qsos.vip/upload/2018/11/ic_launcher20181225044818498.png")
                        )
                    }
                }
                onSuccess = {
                    getData()
                }
            }
        }
    }

    private fun setDate(it: List<UserEntity>) {
        mList.clear()
        mList.addAll(it)

        changeChoseUser()

        mAdapter?.setLimit(chose, limitMin, limitMax)
        mAdapter?.notifyDataSetChanged()
    }

    override fun finish() {
        val intent = Intent()
        intent.putExtra(FormPath.FORM_ITEM_ID, formItemId)
        setResult(Activity.RESULT_OK, intent)
        if (formItemId == null) {
            super.finish()
        } else {
            CoroutineScope(mJob).dbComplete {
                db = {
                    mFormModel.deleteFormItemAllValue(formItemId!!)
                    mList.forEachIndexed { index, user ->
                        if (user.checked) {
                            mFormModel.insertValue(Value(limitEdit = user.limitEdit, position = index).newUser(FormValueOfUser(
                                    userId = user.userId,
                                    userName = user.userName,
                                    userDesc = user.userDesc,
                                    userAvatar = user.userAvatar
                            ), formItemId))
                        }
                    }
                }
                onSuccess = {
                    super.finish()
                }
                onFail = {
                    it.printStackTrace()
                    super.finish()
                }
            }
        }
    }

    /**装配已选用户到用户列表*/
    private fun commitUser(checked: List<Value>, all: List<UserEntity>): List<UserEntity> {
        all.forEach {
            checked.forEach { v ->
                if (v.user!!.userId == it.userId) {
                    it.checked = true
                    it.limitEdit = v.limitEdit
                }
            }
        }
        return all
    }

    fun changeChoseUser() {
        if (limitMax != 1) {
            ll_form_user_chose.visibility = View.VISIBLE
            tv_form_user_chose_all.visibility = if (limitMax == -1) View.VISIBLE else View.GONE
            val limitMaxUser = if (limitMax == null || limitMax == -1) "可选人数不限" else "可选 $limitMax 人"
            form_user_chose_num.text = "已选 $chose 人，$limitMaxUser"
        } else {
            ll_form_user_chose.visibility = View.GONE
        }
    }
}
