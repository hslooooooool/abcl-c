package qsos.core.lib.utils.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import okhttp3.ResponseBody
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.callback.OnTListener
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.*
import java.util.*

/**
 * @author : 华清松
 * 文件工具类
 */
object FileUtils {
    var mContext: BaseApplication = BaseApplication.appContext
    /**缓存地址*/
    var CACHE_PATH: String = BaseApplication.appContext.getExternalFilesDir(null)?.path
            ?: BaseApplication.appContext.filesDir.path
    /**音视频缓存地址*/
    var MEDIA_PATH: String
    /**图片缓存地址*/
    var IMAGE_PATH: String
    /**下载缓存地址*/
    var DOWNLOAD_PATH: String
    /**其它文件缓存地址*/
    var OTHER_PATH: String

    /**媒体类型*/
    private val MIME_TABLE = arrayOf(
            arrayOf(".3gp", "form_take_video/3gpp"),
            arrayOf(".apk", "application/vnd.android.package-archive"),
            arrayOf(".asf", "form_take_video/x-ms-asf"),
            arrayOf(".avi", "form_take_video/x-msvideo"),
            arrayOf(".bin", "application/octet-stream"),
            arrayOf(".bmp", "image/bmp"),
            arrayOf(".c", "text/plain"),
            arrayOf(".class", "application/octet-stream"),
            arrayOf(".conf", "text/plain"),
            arrayOf(".cpp", "text/plain"),
            arrayOf(".doc", "application/msword"),
            arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            arrayOf(".xls", "application/vnd.ms-excel"),
            arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            arrayOf(".exe", "application/octet-stream"),
            arrayOf(".gif", "image/gif"),
            arrayOf(".gtar", "application/x-gtar"),
            arrayOf(".gz", "application/x-gzip"),
            arrayOf(".h", "text/plain"),
            arrayOf(".htm", "text/html"),
            arrayOf(".html", "text/html"),
            arrayOf(".jar", "application/java-archive"),
            arrayOf(".java", "text/plain"),
            arrayOf(".jpeg", "image/jpeg"),
            arrayOf(".jpg", "image/jpeg"),
            arrayOf(".js", "application/x-javascript"),
            arrayOf(".log", "text/plain"),
            arrayOf(".m3u", "form_take_audio/x-mpegurl"),
            arrayOf(".m4a", "form_take_audio/mp4a-latm"),
            arrayOf(".m4b", "form_take_audio/mp4a-latm"),
            arrayOf(".m4p", "form_take_audio/mp4a-latm"),
            arrayOf(".m4u", "form_take_video/vnd.mpegurl"),
            arrayOf(".m4v", "form_take_video/x-m4v"),
            arrayOf(".mov", "form_take_video/quicktime"),
            arrayOf(".mp2", "form_take_audio/x-mpeg"),
            arrayOf(".mp3", "form_take_audio/x-mpeg"),
            arrayOf(".mp4", "form_take_video/mp4"),
            arrayOf(".mpc", "application/vnd.mpohun.certificate"),
            arrayOf(".mpe", "form_take_video/mpeg"),
            arrayOf(".mpeg", "form_take_video/mpeg"),
            arrayOf(".mpg", "form_take_video/mpeg"),
            arrayOf(".mpg4", "form_take_video/mp4"),
            arrayOf(".mpga", "form_take_audio/mpeg"),
            arrayOf(".msg", "application/vnd.ms-outlook"),
            arrayOf(".ogg", "form_take_audio/ogg"),
            arrayOf(".pdf", "application/pdf"),
            arrayOf(".png", "image/png"),
            arrayOf(".pps", "application/vnd.ms-powerpoint"),
            arrayOf(".ppt", "application/vnd.ms-powerpoint"),
            arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            arrayOf(".prop", "text/plain"),
            arrayOf(".rc", "text/plain"),
            arrayOf(".rmvb", "form_take_audio/x-pn-realaudio"),
            arrayOf(".rtf", "application/rtf"),
            arrayOf(".sh", "text/plain"),
            arrayOf(".tar", "application/x-tar"),
            arrayOf(".tgz", "application/x-compressed"),
            arrayOf(".txt", "text/plain"),
            arrayOf(".wav", "form_take_audio/x-wav"),
            arrayOf(".wma", "form_take_audio/x-ms-wma"),
            arrayOf(".wmv", "form_take_audio/x-ms-wmv"),
            arrayOf(".wps", "application/vnd.ms-works"),
            arrayOf(".xml", "text/plain"),
            arrayOf(".z", "application/x-compress"),
            arrayOf(".zip", "application/x-zip-compressed"),
            arrayOf("", "*/*")
    )

    init {
        IMAGE_PATH = "$CACHE_PATH/image/"
        MEDIA_PATH = "$CACHE_PATH/media/"
        DOWNLOAD_PATH = "$CACHE_PATH/download/"
        OTHER_PATH = "$CACHE_PATH/other/"
        initFile(IMAGE_PATH)
        initFile(MEDIA_PATH)
        initFile(DOWNLOAD_PATH)
        initFile(OTHER_PATH)
    }

    /**创建文件夹或文件*/
    @JvmStatic
    fun initFile(fileHolder: String) {
        val file = File(fileHolder)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**根据文件后缀名获得对应的 MIME 类型*/
    @JvmStatic
    fun getMIMEType(file: File): String {
        var type = "*/*"
        val fName = file.name
        // 获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        // 获取文件的后缀名
        val end = fName.substring(dotIndex, fName.length).toLowerCase(Locale.CHINA)
        if (end === "") return type
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (i in 0 until MIME_TABLE.size) {
            if (end == MIME_TABLE[i][0])
                type = MIME_TABLE[i][1]
        }
        return type
    }

    /**调用本地应用打开文件*/
    @Throws(Exception::class)
    @JvmStatic
    fun openFileByPhone(activity: Activity, file: File) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        // 获取文件file的MIME类型
        val type = getMIMEType(file)
        // 设置intent的data和Type属性。android 7.0以上crash,改用provider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val fileUri = FileProvider.getUriForFile(activity, "qsos.core.file.provider", file)
            intent.setDataAndType(fileUri, type)
            grantUriPermission(activity, fileUri, intent)
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
        activity.startActivity(intent)
    }

    @JvmStatic
    fun grantUriPermission(context: Context, fileUri: Uri, intent: Intent) {
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resInfoList.forEach {
            val packageName = it.activityInfo.packageName
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**获取路径为 path 的文件
     * @param path 文件路径
     * @return file path路径下的文件
     */
    @JvmStatic
    fun getFile(path: String?): File? {
        if (TextUtils.isEmpty(path)) return null
        return try {
            val file = File(path)
            if (file.exists()) file else null
        } catch (e: Exception) {
            null
        }
    }

    /**通过URI获取文件名称*/
    @JvmStatic
    fun getNameFromUri(context: Context, contentUri: Uri): String {
        var fileName = ""
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(contentUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst()
            fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
            cursor.close()
        }
        return fileName
    }

    /**
     * 从路径中，获得文件名
     */
    @JvmStatic
    fun getFileNameByPath(path: String?): String {
        if (path == null || path.isEmpty()) return "unknown"
        val nameStart = path.lastIndexOf('/') + 1
        return path.substring(nameStart)
    }

    /**
     * 将下载读取的文件流数据写入本地文件
     * @param path 被保存的路径
     * @param body 请求体
     * @return file 被保存的文件，异常时为 null
     */
    @JvmStatic
    fun writeBodyToFile(path: String, body: ResponseBody): File? {
        var outPutStream: OutputStream? = null
        var inputStream: InputStream? = null
        val file: File?
        try {
            file = File(path)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            inputStream = body.byteStream()
            outPutStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var read: Int

            while (true) {
                read = inputStream.read(buffer)
                if (read == -1) {
                    break
                }
                outPutStream.write(buffer, 0, read)
            }
            outPutStream.flush()
        } catch (e: IOException) {
            return null
        } finally {
            inputStream?.close()
            outPutStream?.close()
        }
        return file
    }

    /**创建一个图片文件*/
    @JvmStatic
    fun createImageFile(): File? {
        return try {
            File(IMAGE_PATH, "IMAGE_" + System.currentTimeMillis().toString() + ".jpg")
        } catch (e: Exception) {
            null
        }
    }

    /**创建一个视频文件*/
    @JvmStatic
    fun createVideoFile(): File? {
        return try {
            File(MEDIA_PATH, "VIDEO_" + System.currentTimeMillis().toString() + ".3gp")
        } catch (e: Exception) {
            null
        }
    }

    /**创建一个音频文件*/
    @JvmStatic
    fun createAudioFile(): File? {
        return try {
            File(MEDIA_PATH, "AUDIO_" + System.currentTimeMillis().toString() + ".amr")
        } catch (e: Exception) {
            null
        }
    }

    /**创建一个文件*/
    @JvmStatic
    fun createFileByUri(context: Context, contentUri: Uri): File? {
        return try {
            File(MEDIA_PATH, "FILE_" + getNameFromUri(context, contentUri))
        } catch (e: Exception) {
            null
        }
    }

    /**获取视频第一帧画面*/
    @JvmStatic
    fun getVideoThumb(path: String): Bitmap {
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        return media.frameAtTime
    }

    /**鲁班压缩*/
    @JvmStatic
    fun zipFileByLuBan(context: Context, fileList: List<File>, listener: OnTListener<List<File>>) {
        val newFileList = arrayListOf<File>()
        for ((index, file) in fileList.withIndex()) {
            val oldFile = File(file.path)
            Luban.with(context)
                    .load(oldFile)
                    .setCompressListener(object : OnCompressListener {
                        override fun onStart() {
                        }

                        override fun onSuccess(newFile: File?) {
                            if (newFile != null) {
                                newFileList.add(index, newFile)
                            }
                            if (newFileList.size == fileList.size) {
                                listener.back(newFileList)
                            }
                        }

                        override fun onError(e: Throwable?) {
                        }
                    }).launch()
        }
    }
}
