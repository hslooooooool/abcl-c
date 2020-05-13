package qsos.app.demo.tweet

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.app_activity_demo.*
import kotlinx.android.synthetic.main.app_item_tweet.view.*
import qsos.app.demo.AppPath
import qsos.app.demo.R
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.activity.BaseActivity
import qsos.lib.base.base.adapter.NormalAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.utils.BaseUtils
import qsos.lib.base.utils.ToastUtils
import java.util.*
import kotlin.math.min

/**
 * @author : 华轻松
 * 朋友圈界面
 */
@Route(group = AppPath.GROUP, path = AppPath.TWEET)
class TweetActivity : BaseActivity(R.layout.app_activity_demo, false) {

    private lateinit var mTweetModel: TweetModelIml
    private lateinit var mTweetAdapter: NormalAdapter<EmployeeBeen>
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private val mList = arrayListOf<EmployeeBeen>()

    private var mRefresh: Boolean = true
    private var mOffset = 0
    private var mScrollY = 0

    override fun initData(savedInstanceState: Bundle?) {
        mTweetModel = ViewModelProviders.of(this).get(TweetModelIml::class.java)
    }

    override fun initView() {

        StatusBarUtil.immersive(this)
        StatusBarUtil.setMargin(this, tweet_list_ch)
        StatusBarUtil.setPaddingSmart(this, tweet_list_head_tb)

        /**默认Toolbar背景透明*/
        tweet_list_head_tb.setBackgroundColor(0)

        mTweetAdapter = NormalAdapter(R.layout.app_item_tweet, mList,
                setHolder = { holder, data, _ ->
                    setHolder(holder, data)
                }
        )

        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.isSmoothScrollbarEnabled = true
        tweet_list_rv.adapter = mTweetAdapter
        tweet_list_rv.layoutManager = mLinearLayoutManager
        tweet_list_rv.isNestedScrollingEnabled = false
        tweet_list_rv.setHasFixedSize(false)
        (tweet_list_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        registerForContextMenu(tweet_list_camera_iv)

        tweet_list_camera_iv.setOnClickListener { ToastUtils.showToast(this, "TAKE PHOTO") }

        /**滚动视图监听*/
        tweet_list_nsv.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            private val defaultHeight = BaseUtils.dip2px(mContext, 170f)
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                mScrollY = min(defaultHeight, scrollY)
                item_tweet_head_profile_iv.translationY = (mOffset - scrollY).toFloat()
                changeToolbar(255 * mScrollY / defaultHeight)
            }
        })

        /**刷新监听,重新获取数据*/
        tweet_list_srl.setOnRefreshListener {
            mRefresh = true
            getData()
        }.setOnLoadMoreListener {
            mRefresh = false
            mTweetModel.getList()
        }

        /**刷新滚动监听,设置状态栏及背景图动效*/
        tweet_list_srl.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                mOffset = offset
                item_tweet_head_profile_iv.translationY = (offset - mScrollY).toFloat()
            }
        })

        tweet_list_head_avatar_iv.setOnClickListener {
            val sEm = mTweetModel.mOne().value?.data
            if (sEm != null) {
                sEm.name = Date().time.toString()
                mTweetModel.put(sEm,
                        {
                            updateUserInfo(it)
                        },
                        {
                            ToastUtils.showToast(this, it)
                        }
                )
            }
        }

        /**观测用户数据更新*/
        mTweetModel.mOne().observe(this, Observer {
            if (it.data != null) updateUserInfo(it.data!!)
        })

        /**观测推特数据更新*/
        mTweetModel.mList().observe(this, Observer {
            tweet_list_srl.finishLoadMore()

            if (mRefresh) {
                mList.clear()
            }
            val oldSize = mList.size
            if (it.data != null) {
                val addSize = it.data!!.size
                mList.addAll(it.data!!)

                if (mRefresh || oldSize == 0) {
                    mTweetAdapter.notifyDataSetChanged()
                } else {
                    mTweetAdapter.notifyItemRangeInserted(oldSize, addSize)
                }
            } else {
                ToastUtils.showToast(this, it.msg ?: "服务器错误")
            }
        })

        mTweetModel.mList().httpState.observe(this, Observer {
            tweet_list_srl.closeHeaderOrFooter()
        })

        getData()
    }

    override fun getData(loadMore: Boolean) {

        mTweetModel.delete(
                {
                    println("删除成功")
                },
                {
                    ToastUtils.showToast(this, it)
                }
        )

        mTweetModel.addOne(
                {
                    println("添加成功")
                },
                {
                    ToastUtils.showToast(this, it)
                }
        )

        mTweetModel.getOne()
        mTweetModel.getList()

    }

    private fun updateUserInfo(user: EmployeeBeen) {
        ImageLoaderUtils.display(mContext, tweet_list_head_avatar_iv, user.head)
        ImageLoaderUtils.display(mContext, item_tweet_head_profile_iv, user.head)
        tweet_list_head_name_tv.text = user.name
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(0, 1, 0, "LIVE")
        menu?.add(0, 2, 0, "CLEAR")
        menu?.setGroupEnabled(0, true)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        ToastUtils.showToast(this, "${item.title}")
        if (item.title == "CLEAR") {
            mTweetModel.clear({
                ToastUtils.showToast(this, it ?: "清除成功")
            }, {
                ToastUtils.showToast(this, it)
            })
        }
        return super.onContextItemSelected(item)
    }

    /**修改状态栏样式*/
    private fun changeToolbar(color: Int) {
        when (color) {
            in 250..255 -> {
                tweet_list_head_tb.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            }
            else -> {
                tweet_list_head_tb.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            }
        }
    }

    private fun setHolder(holder: BaseHolder<EmployeeBeen>, data: EmployeeBeen) {
        ImageLoaderUtils.display(holder.itemView.context, holder.itemView.item_tweet_head_iv, data.head)
        holder.itemView.item_tweet_nick_tv.text = "${data.managerId}"
        holder.itemView.item_tweet_content_tv.text = data.name
        holder.itemView.item_tweet_image_ngl.setUrlList(arrayListOf(data.head ?: ""))
    }

}
