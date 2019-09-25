package qsos.core.file

import androidx.annotation.IntDef

/**
 * @author : 华清松
 * 图片选择方式
 */
open class Sources {
    companion object {
        /**相机*/
        const val CAMERA: Int = 1
        /**单选*/
        const val ONE: Int = 2
        /**多选*/
        const val MULTI: Int = 3
        /**自选操作，相机或单选*/
        const val CHOOSER: Int = 4

        /**值设置是否超出范围*/
        fun overNumber(num: Int): Boolean {
            return num < 1 || num > 4
        }
    }

    @IntDef(CAMERA, ONE, CHOOSER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}

