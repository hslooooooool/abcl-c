package qsos.app.demo.form

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import qsos.app.demo.R
import qsos.core.file.RxImageConverters
import qsos.core.file.RxImagePicker
import qsos.core.file.Sources
import qsos.core.form.config.IFormConfig
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation
import qsos.core.form.db.entity.FormValueOfUser
import qsos.core.lib.utils.dialog.AbsBottomDialog
import qsos.core.lib.utils.dialog.BottomDialog
import qsos.core.lib.utils.dialog.BottomDialogUtils
import qsos.core.lib.utils.file.FileUtils
import timber.log.Timber
import java.io.File

/**
 * @author : 华清松
 * 表单文件操作代理
 */
class FormConfig : IFormConfig {

    override fun takeCamera(context: Context, formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        Timber.tag("表单文件代理").i("拍照")
        RxImagePicker.with((context as FragmentActivity).supportFragmentManager).takeImage(Sources.DEVICE)
                .flatMap {
                    RxImageConverters.uriToFileObservable(context, it, FileUtils.createImageFile())
                }
                .subscribe {
                    val file = FormValueOfFile(fileId = "0001", fileName = it.name, filePath = it.absolutePath, fileType = it.extension, fileUrl = it.absolutePath, fileCover = it.absolutePath)
                    onSuccess.invoke(file)
                }.takeUnless {
                    context.isFinishing
                }
    }

    override fun takeGallery(context: Context, formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        Timber.tag("表单文件代理").i("图库")
        when (canTakeSize) {
            1 -> {
                RxImagePicker.with((context as FragmentActivity).supportFragmentManager).takeImage(Sources.ONE)
                        .flatMap {
                            RxImageConverters.uriToFileObservable(context, it, FileUtils.createImageFile())
                        }
                        .subscribe {
                            val file = FormValueOfFile(fileId = it.absolutePath, fileName = it.name, filePath = it.absolutePath, fileType = it.extension, fileUrl = it.absolutePath, fileCover = it.absolutePath)
                            onSuccess.invoke(arrayListOf(file))
                        }.takeUnless {
                            context.isFinishing
                        }
            }
            in 2..9 -> {
                RxImagePicker.with((context as FragmentActivity).supportFragmentManager).takeFiles(arrayOf("image/*"))
                        .flatMap {
                            val files = arrayListOf<File>()
                            it.forEachIndexed { index, uri ->
                                if (index < canTakeSize) {
                                    RxImageConverters.uriToFile(context, uri, FileUtils.createImageFile())?.let { f ->
                                        files.add(f)
                                    }
                                }
                            }
                            Observable.just(files)
                        }
                        .subscribe {
                            val files = arrayListOf<FormValueOfFile>()
                            it.forEach { f ->
                                files.add(FormValueOfFile(fileId = "takeGallery${f.absolutePath}", fileName = f.name, filePath = f.absolutePath, fileType = f.extension, fileUrl = f.absolutePath, fileCover = f.absolutePath))
                            }
                            onSuccess.invoke(files)
                        }.takeUnless {
                            context.isFinishing
                        }
            }
            else -> onSuccess(arrayListOf())
        }
    }

    override fun takeVideo(context: Context, formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        Timber.tag("表单文件代理").i("视频")
        RxImagePicker.with((context as FragmentActivity).supportFragmentManager).takeVideo()
                .flatMap {
                    RxImageConverters.uriToFileObservable(context, it, FileUtils.createVideoFile())
                }
                .subscribe {
                    val file = FormValueOfFile(fileId = "takeVideo", fileName = it.name, filePath = it.absolutePath, fileType = it.extension, fileUrl = it.absolutePath, fileCover = it.absolutePath)
                    onSuccess.invoke(arrayListOf(file))
                }.takeUnless {
                    context.isFinishing
                }
    }

    override fun takeAudio(context: Context, formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        Timber.tag("表单文件代理").i("音频")
        BottomDialogUtils.showCustomerView(context, R.layout.audio_dialog, object : BottomDialog.ViewListener {
            override fun bindView(dialog: AbsBottomDialog) {
                AudioUtils.record(dialog).subscribe({
                    val file = File(it)
                    file.exists().let {
                        val valueOfFile = FormValueOfFile(fileId = "takeAudio", fileName = file.name, filePath = file.absolutePath, fileType = file.extension, fileUrl = file.absolutePath, fileCover = file.absolutePath)
                        onSuccess.invoke(valueOfFile)
                    }
                }, {
                    it.printStackTrace()
                }).takeUnless {
                    (context as AppCompatActivity).isFinishing
                }
            }
        })
    }

    override fun takeFile(context: Context, formItemId: Long, canTakeSize: Int, mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any) {
        Timber.tag("表单文件代理").i("文件")
        RxImagePicker.with((context as FragmentActivity).supportFragmentManager).takeFiles(arrayOf("image/*", "video/*", "text/plain"))
                .flatMap {
                    val files = arrayListOf<File>()
                    it.forEachIndexed { index, uri ->
                        if (index < canTakeSize) {
                            RxImageConverters.uriToFile(context, uri, FileUtils.createFileByUri(context, uri))?.let { f ->
                                files.add(f)
                            }
                        }
                    }
                    Observable.just(files)
                }
                .subscribe {
                    val files = arrayListOf<FormValueOfFile>()
                    it.forEach { f ->
                        files.add(FormValueOfFile(fileId = "takeFile", fileName = f.name, filePath = f.absolutePath, fileType = f.extension, fileUrl = f.absolutePath, fileCover = f.absolutePath))
                    }
                    onSuccess.invoke(files)
                }.takeUnless {
                    context.isFinishing
                }
    }

    override fun takeLocation(context: Context, formItemId: Long, location: FormValueOfLocation?, onSuccess: (FormValueOfLocation) -> Any) {
        Timber.tag("表单位置代理").i("位置，已有位置${Gson().toJson(location)}")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeLocation = async(Dispatchers.IO) {
                FormValueOfLocation(locName = "测试位置成都市高新区孵化园A区B栋", locX = 30.0000, locY = 104.000000)
            }
            val l = takeLocation.await()
            onSuccess.invoke(l)
        }
    }

    override fun takeUser(context: Context, formItemId: Long, canTakeSize: Int, checkedUsers: List<FormValueOfUser>, onSuccess: (List<FormValueOfUser>) -> Any) {
        Timber.tag("表单用户代理").i("用户，已有用户${Gson().toJson(checkedUsers)}")
        RxUserPicker.with((context as FragmentActivity).supportFragmentManager).takeUser(formItemId)
                .observeOn(Schedulers.io())
                .map {
                    FormDatabase.getInstance().formItemValueDao.getByFormItemId(it).filter { v ->
                        !v.limitEdit
                    }.map { v ->
                        v.user!!
                    }
                }.subscribe {
                    onSuccess.invoke(it)
                }.takeUnless {
                    context.isFinishing
                }
    }

    override fun previewFile(context: Context, index: Int, formValueOfFiles: List<FormValueOfFile>) {
        Timber.tag("表单文件预览代理").i("文件$index")
    }

    override fun previewUser(context: Context, index: Int, formValueOfUser: List<FormValueOfUser>) {
        Timber.tag("表单用户预览代理").i("用户$index")
    }
}