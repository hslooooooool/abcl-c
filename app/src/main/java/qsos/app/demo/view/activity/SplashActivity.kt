package qsos.app.demo.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.app_activity_splash.*
import kotlinx.android.synthetic.main.app_item_component.view.*
import qsos.app.demo.R
import qsos.app.demo.router.TweetPath
import qsos.core.form.FormPath
import qsos.core.form.data.FormRepository
import qsos.core.form.utils.FormTransUtils
import qsos.lib.base.base.activity.BaseActivity
import qsos.lib.base.base.adapter.BaseNormalAdapter
import qsos.lib.base.utils.ActivityManager

/**
 * @author : 华清松
 * 闪屏界面
 */
class SplashActivity(
        override val layoutId: Int = R.layout.app_activity_splash,
        override val reload: Boolean = false
) : BaseActivity() {

    private val mList = arrayListOf("朋友圈", "表单", "表单2")
    private lateinit var mFormRepository: FormRepository

    override fun initData(savedInstanceState: Bundle?) {
        mFormRepository = FormRepository()
    }

    override fun initView() {
        ActivityManager.finishAllButNotMe(this)

        splash_rv.layoutManager = GridLayoutManager(this, 3)
        splash_rv.adapter = BaseNormalAdapter(R.layout.app_item_component, mList,
                setHolder = { holder, data, _ ->
                    holder.itemView.tv_item_component.text = data
                    holder.itemView.tv_item_component.setOnClickListener {
                        when (data) {
                            "朋友圈" -> {
                                ARouter.getInstance().build(TweetPath.TWEET).navigation()
                            }
                            "表单" -> {
                                mFormRepository.insertForm(FormTransUtils.getTestOrderFeedbackData())
                                        .subscribe {
                                            ARouter.getInstance().build(FormPath.MAIN)
                                                    .withLong(FormPath.FORM_ID, it.id!!)
                                                    .navigation()
                                        }
                            }
                            "表单2" -> {
                                mFormRepository.insertForm(FormTransUtils.getTestExecuteData())
                                        .subscribe {
                                            ARouter.getInstance().build(FormPath.MAIN)
                                                    .withLong(FormPath.FORM_ID, it.id!!)
                                                    .navigation()
                                        }
                            }
                        }
                    }
                })
        splash_rv.adapter?.notifyDataSetChanged()
    }

    override fun getData() {}
}