package qsos.core.form.utils

import io.reactivex.functions.Consumer
import qsos.core.form.IFormTakeFile

object FormTakeFileHelper : IFormTakeFile {

    var mIFormTakeFile: IFormTakeFile? = null

    override fun <Result> takeCamera(next: Consumer<Result>) {
        mIFormTakeFile?.takeCamera(next)
    }

    override fun <Result> takeGallery(next: Consumer<Result>) {
        mIFormTakeFile?.takeGallery(next)
    }

    override fun takeWord(mimeTypes: ArrayList<String>, code: Int) {
        mIFormTakeFile?.takeWord(mimeTypes, code)
    }

    override fun takeVideo() {
        mIFormTakeFile?.takeVideo()
    }

    override fun takeLocation() {
        mIFormTakeFile?.takeLocation()
    }

}