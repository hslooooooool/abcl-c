package qsos.core.lib.utils.file

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import okhttp3.ResponseBody
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.R
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.ToastUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import java.text.DecimalFormat

/**
 * @author : 华清松
 * 文件工具类
 */
object FileUtils {
    var mContext: BaseApplication = BaseApplication.appContext
    /**手机是否有SD存储*/
    private var hasSDCard = checkSDStatus()
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

    /**文档类型*/
    val WORD_MIME_TYPES = arrayListOf(
            /**PDF*/
            "application/pdf",
            /**WORD*/
            "application/msword",
            /**PPT*/
            "application/vnd.ms-powerpoint",
            /**EXCEL*/
            "application/vnd.ms-excel",
            /**TXT*/
            "text/plain"
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

    /**检查SD卡是否可用*/
    private fun checkSDStatus(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
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
        val end = fName.substring(dotIndex, fName.length).toLowerCase()
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
    fun openFileOnPhone(activity: Activity, file: File) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        // 获取文件file的MIME类型
        val type = getMIMEType(file)
        // 设置intent的data和Type属性。android 7.0以上crash,改用provider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val fileUri = FileProvider.getUriForFile(activity, activity.packageName + ".provider", file)
            intent.setDataAndType(fileUri, type)
            grantUriPermission(activity, fileUri, intent)
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
        activity.startActivity(intent)
    }

    private fun grantUriPermission(context: Context, fileUri: Uri, intent: Intent) {
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resInfoList.forEach {
            val packageName = it.activityInfo.packageName
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    @JvmStatic
    fun getFile(context: Context, contentUri: Uri): File? {
        return try {
            File(getRealPathFromUri(context, contentUri))
        } catch (e: Exception) {
            try {
                File(contentUri.path)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**获取路径为 path 的文件
     * @param path 文件路径
     * @return file path路径下的文件
     */
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
    fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        return try {
            val pro = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, pro, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val name = cursor.getString(columnIndex)
            getFileNameByPath(name)
        } catch (e: Exception) {
            contentUri.path ?: ""
        } finally {
            cursor?.close()
        }
    }

    /**获得默认的下载地址*/
    @JvmStatic
    fun getDefaultDownLoadPath(): String {
        return if (checkSDStatus()) Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        else Environment.getDownloadCacheDirectory().absolutePath
    }

    /**检查文件是否存在*/
    private fun checkExistence(path: String): Boolean {
        val temp = File(path)
        return temp.exists()
    }

    /**创建文件*/
    @Throws(IOException::class)
    fun createFile(path: String): Boolean {
        if (!checkExistence(path)) {
            val temp = File(path)
            temp.createNewFile()
        } else {
            return false
        }
        return true
    }

    /**将选择的文件存储到临时目录*/
    @Throws(Exception::class)
    fun fileFromUri(context: Context, data: Uri): File? {
        val file = File(data.path)

        val fileName = file.name ?: ""
        val fileCreated: File
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(data, "r")
        val filePath = "${context.externalCacheDir}/$fileName"

        if (!createFile(filePath)) {
            return File(filePath)
        }

        val from = Channels.newChannel(FileInputStream(parcelFileDescriptor!!.fileDescriptor))
        val to = Channels.newChannel(FileOutputStream(filePath))
        fastChannelCopy(from, to)
        from.close()
        to.close()
        fileCreated = File(filePath)
        return fileCreated
    }

    /**快速拷贝文件*/
    @Throws(IOException::class)
    private fun fastChannelCopy(src: ReadableByteChannel, dest: WritableByteChannel) {
        val buffer = ByteBuffer.allocateDirect(16 * 1024)
        while (src.read(buffer) != -1) {
            buffer.flip()
            dest.write(buffer)
            buffer.compact()
        }
        buffer.flip()
        while (buffer.hasRemaining()) {
            dest.write(buffer)
        }
    }

    /**
     * 从路径中，获得文件名
     */
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

    fun saveFile(file: File, filePath: String?, fileName: String?): File? {
        var resultFile: File? = null
        val saveFile: File? = if (TextUtils.isEmpty(filePath)) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absoluteFile, "昆明文件")
        } else {
            File(filePath)
        }

        if (!saveFile!!.exists()) {
            saveFile.mkdirs()
        }

        try {
            resultFile = FileUtils.copyFileToOtherFolder(file, saveFile, fileName
                    ?: System.currentTimeMillis().toString() + file.extension)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resultFile
    }

    @Throws(FileNotFoundException::class)
    fun callSystemUpdate(context: Context, file: File?) {
        if (file == null || !file.exists()) {
            throw FileNotFoundException()
        }
        MediaStore.Images.Media.insertImage(context.contentResolver,
                file.absolutePath, file.name, null)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(File(file.path))))
    }

    fun getDocDownloadFile(context: Context): File {
        return File(context.externalCacheDir, "docs")
    }

    fun getFileType(paramString: String): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) {
            return str
        }
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = paramString.substring(i + 1)
        return str
    }

    fun save(file: File, str: String): Boolean {
        var result = false
        try {
            val fw = FileWriter(file)
            fw.flush()
            fw.write(str)
            fw.close()
            result = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**获取指定文件大小*/
    private fun getFileSize(file: File?): Long {
        var size: Long = 0
        if (file != null && file.exists()) {
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
                size = fis.available().toLong()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
        }
        return size / 1024
    }

    /**获取指定文件夹*/
    private fun getFileSizes(file: File?): Long {
        var size: Long = 0
        if (file != null && file.exists()) {
            for (f in file.listFiles()) {
                if (f != null && f.exists()) {
                    size += if (f.isDirectory) {
                        getFileSizes(f)
                    } else {
                        getFileSize(f)
                    }
                }
            }
        }
        return size
    }

    fun writeFile(inputStream: InputStream?, file: File): Boolean {
        var result = false
        val parentDir = file.parentFile
        if (parentDir.exists() || !parentDir.exists() && parentDir.mkdirs()) {
            if (!file.exists() || file.exists() && file.delete()) {
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    val b = ByteArray(1024)
                    var len: Int = inputStream!!.read(b)
                    while (len != -1) {
                        len = inputStream.read(b)
                        fos.write(b, 0, len)
                    }
                    result = true
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        return result
    }

    @Throws(IOException::class)
    fun copyFileToOtherFolder(file: File, saveFolder: File, fileName: String): File? {
        if (!file.exists()) {
            return null
        }
        if (!saveFolder.exists() || !saveFolder.isDirectory) {
            saveFolder.mkdir()
        }
        val currentFile = File(saveFolder, fileName)
        val inputStream = FileInputStream(file)
        val fileOutputStream = FileOutputStream(currentFile)
        val buffer = ByteArray(1024)
        var byteRead: Int = inputStream.read(buffer)
        while (byteRead != -1) {
            byteRead = inputStream.read(buffer)
            fileOutputStream.write(buffer, 0, byteRead)
        }
        inputStream.close()
        fileOutputStream.close()
        return currentFile
    }

    fun getApkDownloadFile(context: Context): File {
        var file = File(context.externalCacheDir, "apk")
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(file, "uploadApp.apk")
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    /**创建一个图片文件*/
    fun createImageFile(): File? {
        return try {
            File(IMAGE_PATH, "IMAGE_" + System.currentTimeMillis().toString() + ".jpg")
        } catch (e: Exception) {
            null
        }
    }

    /**创建一个视频文件*/
    fun createMovieFile(): File? {
        return try {
            File(MEDIA_PATH, "MOVIE_" + System.currentTimeMillis().toString() + ".mp4")
        } catch (e: Exception) {
            null
        }
    }

    /**获取视屏第一帧画面*/
    fun getVideoThumb(path: String): Bitmap {
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        return media.frameAtTime
    }

    /**保存图片*/
    @SuppressLint("SdCardPath")
    fun saveCroppedImage(bmp: Bitmap) {
        var file = File("/sdcard/Download")
        if (!file.exists()) {
            file.mkdir()
        }
        file = File("/sdcard/temp.jpg".trim())
        val fileName = file.name
        val mName = fileName.substring(0, fileName.lastIndexOf("."))
        val sName = fileName.substring(fileName.lastIndexOf("."))
        val newFilePath = "/sdcard/Download" + "/" + mName + "_cropped" + sName
        file = File(newFilePath)
        try {
            file.createNewFile()
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**大图预览图片*/
    @SuppressLint("PrivateResource")
    fun previewImage(context: Context, url: String?) {
        if (TextUtils.isEmpty(url)) {
            ToastUtils.showToast(context, "文件链接错误")
            return
        }
        val dialog = Dialog(context, R.style.AlertDialog_AppCompat_Light)
        val imageView = ImageView(context)
        imageView.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // 设置Padding
        imageView.setPadding(20, 20, 20, 20)
        ImageLoaderUtils.display(context, imageView, url)
        dialog.setContentView(imageView)
        imageView.setOnClickListener {
            dialog.dismiss()
        }
        imageView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setItems(arrayOf("保存本地")) { dialog: DialogInterface, _: Int ->
                // todo
                dialog.dismiss()
            }
            builder.setCancelable(true)
            builder.show()
            return@setOnLongClickListener true
        }
        dialog.show()
    }

    /**转换文件大小*/
    fun formatFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        return when {
            fileS < 1024 -> df.format(fileS.toDouble()) + "B"
            fileS < 1048576 -> df.format(fileS.toDouble() / 1024) + "KB"
            fileS < 1073741824 -> df.format(fileS.toDouble() / 1048576) + "MB"
            else -> df.format(fileS.toDouble() / 1073741824) + "GB"
        }
    }

    /**读取文件,按固定编码*/
    fun readFile(filePath: String, charsetName: String): StringBuilder? {
        val file = File(filePath)
        val fileContent = StringBuilder("")
        if (!file.isFile) {
            return null
        }

        var reader: BufferedReader? = null
        try {
            val `is` = InputStreamReader(FileInputStream(file), charsetName)
            reader = BufferedReader(`is`)
            var line = reader.readLine()
            while (line != null) {
                fileContent.append(line)
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return fileContent
    }

    /**鲁班压缩*/
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
