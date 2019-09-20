package qsos.core.form.config

import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation

/**
 * @author : 华清松
 * 表单多媒体数据（文件、定位等等）操作接口
 */
interface IFormTakeConfig {
    /**
     * 进行拍照
     * @param onSuccess 选择成功调用回传
     */
    fun takeCamera(onSuccess: (FormValueOfFile) -> Any)

    /**
     * 打开相册选择
     * @param canTakeSize 可选文件数限制
     * @param onSuccess 选择成功调用回传
     */
    fun takeGallery(canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any)

    /**
     * 进行视频录制或视频选择，录制后通过以下方法回传录音文件
     * @param canTakeSize 可选文件数限制
     * @param onSuccess 选择成功调用回传
     */
    fun takeVideo(canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any)

    /**
     * 进行音频录制，推荐方法调用后直接启动录音，类似输入法录音交互，录制后通过以下方法回传录音文件
     * @param onSuccess 选择成功调用回传
     */
    fun takeAudio(onSuccess: (FormValueOfFile) -> Any)

    /**
     * 打开文件选择
     * @param canTakeSize 可选文件数限制
     * @param mimeTypes 可选文档类型
     * @param onSuccess 选择成功调用回传
     */
    fun takeFile(canTakeSize: Int, mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any)

    /**
     * 进行位置选择
     * @param location 已有位置，可用于展示
     * @param onSuccess 选择成功调用回传
     */
    fun takeLocation(location: FormValueOfLocation?, onSuccess: (FormValueOfLocation) -> Any)
}