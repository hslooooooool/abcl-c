package qsos.app.demo.widget

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import qsos.lib.base.base.adapter.BaseAdapter
import java.util.Collections.swap

/**
 * @author : 华清松
 * RecycleView拖动排序
 */
class ItemDragCallback<T : BaseAdapter<*>>(private val mAdapter: T) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        itemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    @SuppressWarnings
    fun itemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                swap(mAdapter.data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                swap(mAdapter.data, i, i - 1)
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition)
    }
}