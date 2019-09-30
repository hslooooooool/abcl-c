package qsos.core.lib.utils.dialog

import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager

/**
 * @author : 华清松
 * 自定义底部弹窗
 */
class BottomDialog : AbsBottomDialog() {

    private var mFragmentManager: FragmentManager? = null

    override var isFillParent = false

    override var layoutRes: Int = 0

    private var mViewListener: ViewListener? = null

    private var mDismissListener: DialogInterface.OnDismissListener? = null

    fun setOnDismissListener(dismissListener: DialogInterface.OnDismissListener) {
        this.mDismissListener = dismissListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            layoutRes = savedInstanceState.getInt(KEY_LAYOUT_RES)
            this.height = savedInstanceState.getInt(KEY_HEIGHT)
            this.dimAmount = savedInstanceState.getFloat(KEY_DIM)
            this.cancelOutside = savedInstanceState.getBoolean(KEY_CANCEL_OUTSIDE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_LAYOUT_RES, layoutRes)
        outState.putInt(KEY_HEIGHT, this.height)
        outState.putFloat(KEY_DIM, this.dimAmount)
        outState.putBoolean(KEY_CANCEL_OUTSIDE, this.cancelOutside)
        super.onSaveInstanceState(outState)
    }

    override fun bindView(dialog: AbsBottomDialog) {
        if (mViewListener != null) {
            mViewListener!!.bindView(dialog)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mDismissListener?.onDismiss(dialog)
    }

    fun setFragmentManager(manager: FragmentManager): BottomDialog {
        mFragmentManager = manager
        return this
    }

    fun setViewListener(listener: ViewListener): BottomDialog {
        mViewListener = listener
        return this
    }

    fun setLayoutRes(@LayoutRes layoutRes: Int): BottomDialog {
        this.layoutRes = layoutRes
        return this
    }


    fun setFillHeight(isFillHeight: Boolean) {
        this.isFillParent = isFillHeight
    }


    fun setCancelOutside(cancel: Boolean): BottomDialog {
        this.cancelOutside = cancel
        return this
    }

    fun setTag(tag: String): BottomDialog {
        this.fragmentTag = tag
        return this
    }

    fun setDimAmount(dim: Float): BottomDialog {
        this.dimAmount = dim
        return this
    }

    fun setHeight(heightPx: Int): BottomDialog {
        this.height = heightPx
        return this
    }

    interface ViewListener {
        fun bindView(dialog: AbsBottomDialog)
    }

    fun show(): AbsBottomDialog {
        show(mFragmentManager!!)
        return this
    }

    companion object {

        private const val KEY_LAYOUT_RES = "bottom_layout_res"
        private const val KEY_HEIGHT = "bottom_height"
        private const val KEY_DIM = "bottom_dim"
        private const val KEY_CANCEL_OUTSIDE = "bottom_cancel_outside"

        fun create(manager: FragmentManager): BottomDialog {
            val dialog = BottomDialog()
            dialog.setFragmentManager(manager)
            return dialog
        }
    }


}
