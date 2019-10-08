package qsos.core.lib.utils.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * @author 华清松
 * 底部自定义弹窗
 */
object BottomDialogUtils {

    fun showCustomerView(
            context: Context,
            @LayoutRes layoutId: Int,
            viewListener: BottomDialog.ViewListener,
            cancel: Boolean = true,
            dismissListener: DialogInterface.OnDismissListener? = null
    ) {
        val bottomDialog = BottomDialog()
        bottomDialog.setFragmentManager((context as AppCompatActivity).supportFragmentManager)
        bottomDialog.setLayoutRes(layoutId)
        bottomDialog.setDimAmount(0.6f)
        bottomDialog.setCancelOutside(cancel)
        bottomDialog.isCancelable = cancel
        bottomDialog.setViewListener(viewListener)
        dismissListener?.let {
            bottomDialog.setOnDismissListener(it)
        }
        bottomDialog.show()
    }

}