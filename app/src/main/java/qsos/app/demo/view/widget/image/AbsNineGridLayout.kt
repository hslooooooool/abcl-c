package qsos.app.demo.view.widget.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.noober.menu.FloatMenu
import qsos.app.demo.R
import java.util.*
import kotlin.math.ceil

/**
 * @author : 华清松
 * 九宫格多类型样式
 */
abstract class AbsNineGridLayout : ViewGroup {
    private var mContext: Context? = null
    private var mSpacing = DEFAULT_SPACING
    private var mColumns: Int = 0
    private var mRows: Int = 0
    private var mTotalWidth: Int = 0
    private var mSingleWidth: Int = 0
    private var mIsShowAll = false
    private var mIsFirst = true
    private val mUrlList = ArrayList<String>()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbsNineGridLayout)
        mSpacing = typedArray.getDimension(R.styleable.AbsNineGridLayout_space, DEFAULT_SPACING)
        typedArray.recycle()
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        if (getListSize(mUrlList) == 0) {
            visibility = View.GONE
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mTotalWidth = right - left
        mSingleWidth = ((mTotalWidth - mSpacing * (3 - 1)) / 3).toInt()
        if (mIsFirst) {
            notifyDataSetChanged()
            mIsFirst = false
        }
    }

    /**
     * 设置间隔
     *
     * @param spacing 间隔
     */
    fun setSpacing(spacing: Float) {
        mSpacing = spacing
    }

    /**
     * 设置是否显示所有图片（超过最大数时）
     *
     * @param isShowAll 是否显示所有图片
     */
    fun setIsShowAll(isShowAll: Boolean) {
        mIsShowAll = isShowAll
    }

    /**设置图片地址池 */
    fun setUrlList(urlList: List<String>) {
        if (getListSize(urlList) == 0) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE

        mUrlList.clear()
        mUrlList.addAll(urlList)

        if (!mIsFirst) {
            notifyDataSetChanged()
        }
    }

    private fun notifyDataSetChanged() {
        post(object : TimerTask() {
            override fun run() {
                refresh()
            }
        })
    }

    private fun refresh() {
        removeAllViews()
        val size = getListSize(mUrlList)
        visibility = if (size > 0) View.VISIBLE else View.GONE

        if (size == 1) {
            val url = mUrlList[0]
            val imageView = createImageView(0, url)
            // 避免在ListView中一张图未加载成功时，布局高度受其他item影响
            val params = layoutParams
            params.height = mSingleWidth
            layoutParams = params
            imageView.layout(0, 0, mSingleWidth, mSingleWidth)

            val isShowDefault = displayOneImage(imageView, url, mTotalWidth)
            if (isShowDefault) {
                layoutImageView(imageView, 0, url, false)
            } else {
                addView(imageView)
            }
            return
        }

        generateChildrenLayout(size)
        layoutParams()

        for (i in 0 until size) {
            val url = mUrlList[i]
            val imageView: RatioImageView
            if (!mIsShowAll) {
                if (i < MAX_COUNT - 1) {
                    imageView = createImageView(i, url)
                    layoutImageView(imageView, i, url, false)
                } else {
                    //第9张时
                    if (size <= MAX_COUNT) {
                        //刚好第9张
                        imageView = createImageView(i, url)
                        layoutImageView(imageView, i, url, false)
                    } else {
                        //超过9张
                        imageView = createImageView(i, url)
                        layoutImageView(imageView, i, url, true)
                        break
                    }
                }
            } else {
                imageView = createImageView(i, url)
                layoutImageView(imageView, i, url, false)
            }
        }
    }

    private fun layoutParams() {
        val singleHeight = mSingleWidth
        // 根据子view数量确定高度
        val params = layoutParams
        params.height = (singleHeight * mRows + mSpacing * (mRows - 1)).toInt()
        layoutParams = params
    }

    private fun createImageView(i: Int, url: String): RatioImageView {
        val imageView = RatioImageView(mContext!!)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setOnClickListener {
            onClickImage(it, i, url, mUrlList)
        }
        val floatMenu = FloatMenu(imageView.context, imageView)
        floatMenu.items("保存", "分享")
        imageView.setOnLongClickListener {
            onLongClickImage(it, i, url, floatMenu)
            return@setOnLongClickListener true
        }
        return imageView
    }

    /**
     * @param imageView
     * @param url
     * @param showNumFlag 是否在最大值的图片上显示还有未显示的图片张数
     */
    @SuppressLint("SetTextI18n")
    private fun layoutImageView(imageView: RatioImageView, i: Int, url: String, showNumFlag: Boolean) {
        val singleWidth = ((mTotalWidth - mSpacing * (3 - 1)) / 3).toInt()

        val position = findPosition(i)
        val left = ((singleWidth + mSpacing) * position[1]).toInt()
        val top = ((singleWidth + mSpacing) * position[0]).toInt()
        val right = left + singleWidth
        val bottom = top + singleWidth

        imageView.layout(left, top, right, bottom)

        addView(imageView)
        if (showNumFlag) {
            // 添加超过最大显示数量的文本
            val overCount = getListSize(mUrlList) - MAX_COUNT
            if (overCount > 0) {
                val textSize = 30f
                val textView = TextView(mContext)
                textView.text = "+$overCount"
                textView.setTextColor(Color.WHITE)
                textView.setPadding(0, singleWidth / 2 - getFontHeight(textSize), 0, 0)
                textView.textSize = textSize
                textView.gravity = Gravity.CENTER
                textView.setBackgroundColor(Color.BLACK)
                textView.background.alpha = 120

                textView.layout(left, top, right, bottom)
                addView(textView)
            }
        }
        displayImage(imageView, url)
    }

    private fun findPosition(childNum: Int): IntArray {
        val position = IntArray(2)
        for (i in 0 until mRows) {
            for (j in 0 until mColumns) {
                if (i * mColumns + j == childNum) {
                    // 行
                    position[0] = i
                    // 列
                    position[1] = j
                    break
                }
            }
        }
        return position
    }

    /**
     * 根据图片个数确定行列数量
     *
     * @param length
     */
    private fun generateChildrenLayout(length: Int) {
        if (length <= 3) {
            mRows = 1
            mColumns = length
        } else if (length <= 6) {
            mRows = 2
            mColumns = 3
            if (length == 4) {
                mColumns = 2
            }
        } else {
            mColumns = 3
            if (mIsShowAll) {
                mRows = length / 3
                val b = length % 3
                if (b > 0) {
                    mRows++
                }
            } else {
                mRows = 3
            }
        }
    }

    protected fun setOneImageLayoutParams(imageView: RatioImageView, width: Int, height: Int) {
        imageView.layoutParams = LayoutParams(width, height)
        imageView.layout(0, 0, width, height)
        val params = layoutParams
        params.height = height
        layoutParams = params
    }

    private fun getListSize(list: List<String>?): Int {
        return if (list == null || list.isEmpty()) 0 else list.size
    }

    private fun getFontHeight(fontSize: Float): Int {
        val paint = Paint()
        paint.textSize = fontSize
        val fm = paint.fontMetrics
        return ceil((fm.descent - fm.ascent).toDouble()).toInt()
    }

    /**
     * @param imageView   单图控件
     * @param url         图片链接
     * @param parentWidth 父控件宽度
     * @return true 代表按照九宫格默认大小显示，false 代表按照自定义宽高显示
     */
    protected abstract fun displayOneImage(imageView: RatioImageView, url: String, parentWidth: Int): Boolean

    protected abstract fun displayImage(imageView: RatioImageView, url: String)

    protected abstract fun onClickImage(view: View, position: Int, url: String, urlList: List<String>)

    protected abstract fun onLongClickImage(view: View, position: Int, url: String, menu: FloatMenu)

    companion object {
        private const val DEFAULT_SPACING = 3f
        private const val MAX_COUNT = 9
    }

}
