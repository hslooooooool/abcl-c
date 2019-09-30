package qsos.core.player.view

import android.graphics.Rect
import android.view.View

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

import java.lang.ref.WeakReference

/**
 * @author : 华清松
 * 图片画廊工具类
 */
class GalleryScalableHelper constructor(private val recyclerView: RecyclerView, private val pageChangeListener: OnPageChangeListener) {

    private val snapHelper = PagerSnapHelper()
    private var pageChangeListenerRef: WeakReference<OnPageChangeListener>? = null

    private fun pageScrolled() {
        if (recyclerView.childCount == 0) return
        val layoutManager = recyclerView.layoutManager
        val snapView = snapHelper.findSnapView(layoutManager!!)
        val snapViewPosition = recyclerView.getChildAdapterPosition(snapView!!)
        val leftSnapView = layoutManager.findViewByPosition(snapViewPosition - 1)
        val rightSnapView = layoutManager.findViewByPosition(snapViewPosition + 1)

        val leftSnapOffset = calculateOffset(recyclerView, leftSnapView)
        val rightSnapOffset = calculateOffset(recyclerView, rightSnapView)
        val currentSnapOffset = calculateOffset(recyclerView, snapView)

        snapView.scaleX = currentSnapOffset
        snapView.scaleY = currentSnapOffset

        if (leftSnapView != null) {
            leftSnapView.scaleX = leftSnapOffset
            leftSnapView.scaleY = leftSnapOffset
        }

        if (rightSnapView != null) {
            rightSnapView.scaleX = rightSnapOffset
            rightSnapView.scaleY = rightSnapOffset
        }

        if (currentSnapOffset >= 1) {
            val listener =
                    if (pageChangeListenerRef != null) pageChangeListenerRef!!.get() else null

            listener?.onPageSelected(snapViewPosition)
        }
    }

    fun build() {
        this.pageChangeListenerRef = WeakReference(pageChangeListener)
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                pageScrolled()
            }
        })
        recyclerView.addItemDecoration(ScalableCardItemDecoration())
        recyclerView.post { pageScrolled() }
    }

    /**通过计算`view`中间点与[RecyclerView]的中间点的距离，算出`view`的偏移量。*/
    private fun calculateOffset(recyclerView: RecyclerView, view: View?): Float {
        if (view == null) return -1f

        val layoutManager = recyclerView.layoutManager
        val isVertical = layoutManager!!.canScrollVertically()
        val viewStart = if (isVertical) view.top else view.left
        val viewEnd = if (isVertical) view.bottom else view.right

        val centerX = if (isVertical) recyclerView.height / 2 else recyclerView.width / 2
        val childCenter = (viewStart + viewEnd) / 2
        val distance = Math.abs(childCenter - centerX)

        if (distance > centerX)
            return STAY_SCALE

        val offset = 1f - distance / centerX.toFloat()

        return (1f - STAY_SCALE) * offset + STAY_SCALE
    }

    interface OnPageChangeListener {
        fun onPageSelected(position: Int)
    }

    private class ScalableCardItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val holder = parent.getChildViewHolder(view)
            val position = if (holder.adapterPosition == RecyclerView.NO_POSITION) holder.oldPosition else holder.adapterPosition
            val layoutManager = parent.layoutManager
            val itemCount = layoutManager!!.itemCount

            if (position != 0 && position != itemCount - 1) {
                return
            }

            val peekWidth = getPeekWidth(parent, view)
            val isVertical = layoutManager.canScrollVertically()
            // 移除item时adapter position为-1。

            if (isVertical) {
                when (position) {
                    0 -> outRect.set(0, peekWidth, 0, 0)
                    itemCount - 1 -> outRect.set(0, 0, 0, peekWidth)
                    else -> outRect.set(0, 0, 0, 0)
                }
            } else {
                when (position) {
                    0 -> outRect.set(peekWidth, 0, 0, 0)
                    itemCount - 1 -> outRect.set(0, 0, peekWidth, 0)
                    else -> outRect.set(0, 0, 0, 0)
                }
            }
        }
    }

    companion object {

        private const val STAY_SCALE = 0.95f

        fun getPeekWidth(recyclerView: RecyclerView, itemView: View): Int {
            val layoutManager = recyclerView.layoutManager
            val isVertical = layoutManager!!.canScrollVertically()
            val position = recyclerView.getChildAdapterPosition(itemView)
            // RecyclerView使用wrap_content时，获取的宽度可能会是0。
            var parentWidth = recyclerView.measuredWidth
            var parentHeight = recyclerView.measuredHeight //有时会拿到0
            parentWidth = if (parentWidth == 0) recyclerView.width else parentWidth
            parentHeight = if (parentHeight == 0) recyclerView.height else parentHeight
            val parentEnd = if (isVertical) parentHeight else parentWidth
            val parentCenter = parentEnd / 2
            var itemSize = if (isVertical) itemView.measuredHeight else itemView.measuredWidth

            if (itemSize == 0) {
                val layoutParams = itemView.layoutParams
                val widthMeasureSpec = RecyclerView.LayoutManager.getChildMeasureSpec(parentWidth,
                        layoutManager.widthMode,
                        recyclerView.paddingLeft + recyclerView.paddingRight,
                        layoutParams.width, layoutManager.canScrollHorizontally())

                val heightMeasureSpec = RecyclerView.LayoutManager.getChildMeasureSpec(parentHeight,
                        layoutManager.heightMode,
                        recyclerView.paddingTop + recyclerView.paddingBottom,
                        layoutParams.height, layoutManager.canScrollVertically())


                itemView.measure(widthMeasureSpec, heightMeasureSpec)
                itemSize = if (isVertical) itemView.measuredHeight else itemView.measuredWidth
            }
            /**计算ItemDecoration的大小，确保插入的大小正好使view的start + itemSize / 2等于parentCenter。*/
            val startOffset = parentCenter - itemSize / 2
            val endOffset = parentEnd - (startOffset + itemSize)
            return if (position == 0) startOffset else endOffset
        }
    }
}
