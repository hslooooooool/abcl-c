package qsos.core.form.view.other

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : 华清松
 * @description : 表单项分割线
 */
class FormItemDecoration : RecyclerView.ItemDecoration() {
    /**
     * @param outRect 边界
     * @param view    recyclerView ItemView
     * @param parent  recyclerView
     * @param state   recycler 内部数据管理
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        // 设定底部边距为1px
        outRect.set(0, 0, 0, 1)
    }
}