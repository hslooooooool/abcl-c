package qsos.core.file

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : 华清松
 * 图片选择界面
 */
@SuppressLint("CheckResult")
class RxImagePicker : Fragment() {

    /**观察是否绑定Activity*/
    private lateinit var attachedSubject: PublishSubject<Boolean>
    /**选择单张*/
    private lateinit var publishSubject: PublishSubject<Uri>
    /**选择多张*/
    private lateinit var publishMultipleSubject: PublishSubject<List<Uri>>
    /**选择取消*/
    private lateinit var canceledSubject: PublishSubject<Int>
    /**是否为多选*/
    private var isMultiple = false
    /**选择方式*/
    private var mTakeType: Int = Sources.ONE
    /**选择文件种类
     * 0 图片 1 视频 2 音频 3 文件
     * */
    private var mFileType: Int = 0
    /**选择界面标题 Sources.CHOOSER 时生效*/
    private var mChooserTitle: String? = "选择"
    /**默认限制录制时长为10秒*/
    private var mLimitTime: Int = 10000
    /**默认限制文件类型
     * @see qsos.core.lib.utils.file.FileUtils.MIME_TABLE
     * */
    private var mMimeTypes: Array<String> = arrayOf("*/*")
    private var mMimeType: String = if (mMimeTypes.isEmpty()) "*/*" else mMimeTypes[0]

    /**图片选择*/
    fun takeImage(@Sources.Type type: Int = Sources.CHOOSER, chooserTitle: String = "图片选择"): Observable<Uri> {
        initSubjects()
        this.mFileType = 0
        this.isMultiple = false
        this.mTakeType = type
        this.mMimeTypes = arrayOf("image/*")
        this.mChooserTitle = chooserTitle
        requestPick()
        return publishSubject.takeUntil(canceledSubject)
    }

    /**视频选择*/
    fun takeVideo(@Sources.Type type: Int = Sources.CHOOSER, limitTime: Int = 10000, chooserTitle: String = "视频选择"): Observable<Uri> {
        initSubjects()
        this.mFileType = 1
        this.isMultiple = false
        this.mTakeType = type
        this.mMimeTypes = arrayOf("video/*")
        this.mLimitTime = limitTime
        this.mChooserTitle = chooserTitle
        requestPick()
        return publishSubject.takeUntil(canceledSubject)
    }

    /**文件选择*/
    fun takeFile(mimeType: String = "*/*", chooserTitle: String = "文件选择"): Observable<Uri> {
        initSubjects()
        this.mFileType = 3
        this.isMultiple = false
        this.mTakeType = Sources.ONE
        this.mMimeTypes = arrayOf(mimeType)
        this.mChooserTitle = chooserTitle
        requestPick()
        return publishSubject.takeUntil(canceledSubject)
    }

    /**文件选择*/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun takeFiles(mimeType: Array<String> = arrayOf("*/*"), multiple: Boolean = true): Observable<List<Uri>> {
        initSubjects()
        this.isMultiple = multiple
        this.mTakeType = Sources.MULTI
        this.mMimeTypes = mimeType
        requestPick()
        return publishMultipleSubject.takeUntil(canceledSubject)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 保留Fragment
        retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (::attachedSubject.isInitialized.not() or
                ::publishSubject.isInitialized.not() or
                ::publishMultipleSubject.isInitialized.not() or
                ::canceledSubject.isInitialized.not()) {
            initSubjects()
        }
        attachedSubject.onNext(true)
        attachedSubject.onComplete()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            /**选择结果回调*/
            when (requestCode) {
                Sources.CAMERA -> pushImage(temFileUri)
                Sources.ONE, Sources.MULTI -> handleGalleryResult(data)
                Sources.CHOOSER -> if (isCamera(data)) pushImage(temFileUri) else handleGalleryResult(data)
            }
        } else {
            /**取消选择*/
            canceledSubject.onNext(requestCode)
        }
    }

    /**初始化*/
    private fun initSubjects() {
        publishSubject = PublishSubject.create()
        attachedSubject = PublishSubject.create()
        canceledSubject = PublishSubject.create()
        publishMultipleSubject = PublishSubject.create()
    }

    /**是否为相机*/
    private fun isCamera(data: Intent?): Boolean {
        return data == null || data.data == null && data.clipData == null
    }

    private fun requestPick() {
        if (!isAdded) {
            attachedSubject.subscribe {
                startPick()
            }
        } else {
            startPick()
        }
    }

    /**图片选取方式判断*/
    private fun startPick() {
        /**类型超出返回*/
        if (Sources.overNumber(mTakeType)) return
        /**未授予权限*/
        if (!checkPermission()) return

        var chooseIntent: Intent? = null
        when {
            /**拍照*/
            mTakeType == Sources.CAMERA && mFileType == 0 -> {
                temFileUri = createImageUri()
                chooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    it.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
                    grantWritePermission(context!!, it, temFileUri!!)
                }
            }
            /**视频拍摄*/
            mTakeType == Sources.CAMERA && mFileType == 1 -> {
                temFileUri = createImageUri()
                chooseIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).also {
                    it.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
                    grantWritePermission(context!!, it, temFileUri!!)
                }
            }

            /**拍照或选择*/
            mTakeType == Sources.CHOOSER && mFileType == 0 -> {
                chooseIntent = createImageChooserIntent()
            }
            /**视频拍摄或选择*/
            mTakeType == Sources.CHOOSER && mFileType == 1 -> {
                chooseIntent = createVideoChooserIntent()
            }
            /**音频录制或选择*/
            mTakeType == Sources.CHOOSER && mFileType == 2 -> {
                chooseIntent = createAudioChooserIntent()
            }

            /**文件单选*/
            mTakeType == Sources.ONE -> {
                chooseIntent = createPickOne()
            }
            /**文件多选*/
            mTakeType == Sources.MULTI -> {
                chooseIntent = createPickMore()
            }
        }
        activity?.packageManager?.let {
            chooseIntent?.resolveActivity(it)?.let {
                startActivityForResult(chooseIntent, mTakeType)
            }
        }
    }

    /**构建文件单选Intent*/
    private fun createPickOne(): Intent {
        val pictureChooseIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pictureChooseIntent.type = mMimeType
        return pictureChooseIntent
    }

    /**构建文件多选Intent*/
    private fun createPickMore(): Intent {
        val pictureChooseIntent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)

            pictureChooseIntent.type = "*/*"
            pictureChooseIntent.putExtra(Intent.EXTRA_MIME_TYPES, mMimeTypes)
        } else {
            pictureChooseIntent = createPickOne()
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return pictureChooseIntent
    }

    /**构建图片选择器Intent*/
    private fun createImageChooserIntent(): Intent {
        temFileUri = createImageUri()
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
            grantWritePermission(context!!, intent, temFileUri!!)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createPickMore(), mChooserTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**构建视频选择器Intent*/
    private fun createVideoChooserIntent(): Intent {
        temFileUri = createVideoUri()
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        // 某些手机此设置是不生效的，需要自行封装解决
        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mLimitTime)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
            grantWritePermission(context!!, intent, temFileUri!!)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createPickMore(), mChooserTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**构建音频选择器Intent*/
    private fun createAudioChooserIntent(): Intent {
        temFileUri = createVideoUri()
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
            grantWritePermission(context!!, intent, temFileUri!!)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createPickMore(), mChooserTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**创建拍照保存路径*/
    private fun createImageUri(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**创建录像保存路径*/
    private fun createVideoUri(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Video.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**申请文件读写权限*/
    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
            false
        } else {
            true
        }
    }

    /**申请文件读写权限*/
    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**获取选择结果*/
    private fun handleGalleryResult(data: Intent?) {
        if (isMultiple) {
            val imageUris = ArrayList<Uri>()
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    imageUris.add(clipData.getItemAt(i).uri)
                }
            } else {
                data?.data?.let { imageUris.add(it) }
            }
            pushImageList(imageUris)
        } else {
            pushImage(data?.data)
        }
    }

    /**传递选择的多图*/
    private fun pushImageList(uris: List<Uri>) {
        publishMultipleSubject.onNext(uris)
        publishMultipleSubject.onComplete()
    }

    /**传递选择的单图*/
    private fun pushImage(uri: Uri?) {
        if (uri == null) {
            publishSubject.onError(Throwable(NullPointerException("Uri is null")))
        } else {
            publishSubject.onNext(uri)
        }
        publishSubject.onComplete()
    }

    companion object {

        private val TAG = RxImagePicker::class.java.name
        /**拍照存储URI*/
        private var temFileUri: Uri? = null

        /**获取RxImagePicker实例*/
        fun with(fm: FragmentManager): RxImagePicker {
            var rxImagePickerFragment = fm.findFragmentByTag(TAG) as RxImagePicker?
            if (rxImagePickerFragment == null) {
                rxImagePickerFragment = RxImagePicker()
                fm.beginTransaction().add(rxImagePickerFragment, TAG).commit()
            }
            return rxImagePickerFragment
        }
    }

}
