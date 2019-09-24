package qsos.core.file

import androidx.annotation.IntDef

/**
 * @author : 华清松
 * 图片选择方式
 */
open class Sources {
    companion object {
        /**拍照*/
        const val CAMERA: Int = 1
        /**图库*/
        const val GALLERY: Int = 2
        /**文档*/
        const val DOCUMENTS: Int = 3
        /**选择拍照或文档*/
        const val CHOOSER: Int = 4
    }

    @IntDef(GALLERY, DOCUMENTS, CHOOSER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}

