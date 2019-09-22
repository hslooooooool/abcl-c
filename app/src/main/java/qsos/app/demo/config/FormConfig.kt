package qsos.app.demo.config

import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import kotlinx.coroutines.*
import qsos.app.demo.router.AppPath
import qsos.core.form.config.IFormConfig
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation
import qsos.core.form.db.entity.FormValueOfUser
import timber.log.Timber

/**
 * @author : 华清松
 * 表单文件操作代理
 */
class FormConfig : IFormConfig {

    override fun takeCamera(formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        Timber.tag("表单文件代理").i("拍照")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeFile = async(Dispatchers.IO) {
                val file = FormValueOfFile(fileId = "0001", fileName = "拍照", filePath = "/0/data/vip.qsos.demo/temp/logo.png", fileType = ".png", fileUrl = "http://www.qsos.vip/resource/logo.png", fileCover = "http://www.qsos.vip/resource/logo.png")
                file
            }
            val file = takeFile.await()
            onSuccess.invoke(file)
        }
    }

    override fun takeGallery(formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        Timber.tag("表单文件代理").i("图库")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeFile = async(Dispatchers.IO) {
                val file = FormValueOfFile(fileId = "0002", fileName = "图库", filePath = "/0/data/vip.qsos.demo/temp/logo.jpg", fileType = ".jpg", fileUrl = "http://www.qsos.vip/resource/logo.jpg", fileCover = "http://www.qsos.vip/resource/logo.jpg")
                file
            }
            val file = takeFile.await()
            onSuccess.invoke(arrayListOf(file))
        }
    }

    override fun takeVideo(formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        Timber.tag("表单文件代理").i("视频")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeFile = async(Dispatchers.IO) {
                val file = FormValueOfFile(fileId = "0003", fileName = "视频", filePath = "/0/data/vip.qsos.demo/temp/logo.mp4", fileType = ".mp4", fileUrl = "http://www.qsos.vip/resource/logo.mp4", fileCover = "http://www.qsos.vip/resource/logo.jpg")
                file
            }
            val file = takeFile.await()
            onSuccess.invoke(arrayListOf(file))
        }
    }

    override fun takeAudio(formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        Timber.tag("表单文件代理").i("音频")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeFile = async(Dispatchers.IO) {
                val file = FormValueOfFile(fileId = "0004", fileName = "音频", filePath = "/0/data/vip.qsos.demo/temp/logo.amr", fileType = ".amr", fileUrl = "http://www.qsos.vip/resource/logo.amr")
                file
            }
            val file = takeFile.await()
            onSuccess.invoke(file)
        }
    }

    override fun takeFile(formItemId: Long, canTakeSize: Int, mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any) {
        Timber.tag("表单文件代理").i("文件")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeFile = async(Dispatchers.IO) {
                val files = arrayListOf<FormValueOfFile>()
                for (i in 1..canTakeSize) {
                    val file = FormValueOfFile(fileId = "0004$i", fileName = "文件$i", filePath = "/0/data/vip.qsos.demo/temp/logo$i.pdf", fileType = ".pdf", fileUrl = "http://www.qsos.vip/resource/logo$i.pdf")
                    files.add(file)
                }
                files
            }
            val files = takeFile.await()
            onSuccess.invoke(files)
        }
    }

    override fun takeLocation(formItemId: Long, location: FormValueOfLocation?, onSuccess: (FormValueOfLocation) -> Any) {
        Timber.tag("表单位置代理").i("位置，已有位置${Gson().toJson(location)}")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeLocation = async(Dispatchers.IO) {
                FormValueOfLocation(locName = "测试位置成都市高新区孵化园A区B栋", locX = 30.0000, locY = 104.000000)
            }
            val l = takeLocation.await()
            onSuccess.invoke(l)
        }
    }

    override fun takeUser(formItemId: Long, canTakeSize: Int, checkedUsers: List<FormValueOfUser>, onSuccess: (List<FormValueOfUser>) -> Any) {
        Timber.tag("表单用户代理").i("用户，已有用户${Gson().toJson(checkedUsers)}")
        ARouter.getInstance().build(AppPath.FORM_ITEM_USERS)
                .withLong(AppPath.FORM_ITEM_ID, formItemId)
                .navigation()
        // or
//        CoroutineScope(Job()).launch(Dispatchers.Main) {
//            val takeUser = async(Dispatchers.IO) {
//                val users = arrayListOf<FormValueOfUser>()
//                users.addAll(checkedUsers)
//                for (i in checkedUsers.size..canTakeSize + checkedUsers.size) {
//                    users.add(FormValueOfUser(userId = "000$i", userName = "用户$i", userDesc = "1822755555$i", userAvatar = "http://www.qsos.vip/upload/2018/11/ic_launcher20181225044818498.png"))
//                }
//                users
//            }
//            val users = takeUser.await()
//            onSuccess.invoke(users)
//        }
    }

    override fun previewFile(index: Int, formValueOfFiles: List<FormValueOfFile>) {
        Timber.tag("表单文件预览代理").i("文件$index")
    }

    override fun previewUser(index: Int, formValueOfUser: List<FormValueOfUser>) {
        Timber.tag("表单用户预览代理").i("用户$index")
    }
}