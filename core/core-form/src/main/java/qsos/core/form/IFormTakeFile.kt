package qsos.core.form

import io.reactivex.functions.Consumer

interface IFormTakeFile {
    /**打开摄像头
     * @since 必须开启 Manifest.permission.CAMERA 权限
     * */
    fun <Result> takeCamera(next: Consumer<Result>)

    /**打开相册
     * @since 必须开启 Manifest.permission.CAMERA 权限
     * */
    fun <Result> takeGallery(next: Consumer<Result>)

    /**
     * 打开文档选择
     * @param mimeTypes 可选文档类型
     * @param code 选择回调码
     */
    fun takeWord(mimeTypes: ArrayList<String>, code: Int)

    /**
     * 进行视频录制
     */
    fun takeVideo()

    /**
     * 进行位置选择
     */
    fun takeLocation()
}