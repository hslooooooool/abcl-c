package qsos.core.lib.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.TextUtils
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author : 华清松
 * Bitmap 工具类
 */
object BitmapUtils {

    /**图片任意形状的放大缩小*/
    fun zoomToFixShape(bitmap: Bitmap, iconSize: Int): Bitmap {
        var size = iconSize.toFloat()
        val tempBitmap: Bitmap
        val bitH = bitmap.height.toFloat()
        val bitW = bitmap.width.toFloat()
        val mMatrix = Matrix()
        if (size < 1f) {
            size = 20f
        }
        val minBit = (if (bitH <= bitW) bitH else bitW)
        val scold = if (minBit < size) size / minBit else minBit / size
        mMatrix.reset()
        mMatrix.postScale(scold, scold)
        tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitW.toInt(), bitH.toInt(), mMatrix, true)

        return tempBitmap
    }

    /**图片模糊处理*/
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        // Create renderscript
        val rs = RenderScript.create(context)
        // Create allocation from Bitmap
        val allocation = Allocation.createFromBitmap(rs, bitmap)
        val t = allocation.type
        // Create allocation with the same typeEnum
        val blurredAllocation = Allocation.createTyped(rs, t)
        // Create script
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        // Set blur radius (maximum 25.0)
        blurScript.setRadius(radius)
        // Set input for script
        blurScript.setInput(allocation)
        // Call script for output allocation
        blurScript.forEach(blurredAllocation)
        // Copy script result into bitmap
        blurredAllocation.copyTo(bitmap)
        // Destroy everything to free memory
        allocation.destroy()
        blurredAllocation.destroy()
        blurScript.destroy()
        rs.destroy()
        return bitmap
    }

    /**图片文件转化成base64字符串*/
    fun fileToBase64(path: String?): String? {
        var inputStream: InputStream? = null
        var base64: String? = null
        if (TextUtils.isEmpty(path)) {
            return null
        }
        // 读取图片字节数组
        try {
            inputStream = FileInputStream(path)
            val bytes = ByteArray(inputStream.available())
            val length = inputStream.read(bytes)
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT)
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
        }
        return base64
    }

    /**返回Base64编码过的字节数组字符串*/
    @Throws(Exception::class)
    fun encodeImageToBase64(url: URL): String? {
        val conn: HttpURLConnection?
        try {
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5 * 1000
            val inStream = conn.inputStream
            val outStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var read: Int
            while (true) {
                read = inStream.read(buffer)
                if (read == -1) {
                    break
                }
                outStream.write(buffer, 0, read)
            }
            inStream.close()
            val data = outStream.toByteArray()
            return Base64.encodeToString(data, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}