package qsos.core.lib.utils

import android.annotation.SuppressLint
import java.util.*

/**
 * @author : 华清松
 * 身份证格式验证
 */
object IDCardUtil {

    @SuppressLint("UseSparseArrays")
    private val zoneNum = HashMap<Int, String>()

    private val PARITY_BIT = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')
    private val POWER_LIST = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)

    private val idCardCalendar: Int
        get() {
            val curDay = GregorianCalendar()
            val curYear = curDay.get(Calendar.YEAR)
            return Integer.parseInt(curYear.toString().substring(2))
        }

    init {
        zoneNum[11] = "北京"
        zoneNum[12] = "天津"
        zoneNum[13] = "河北"
        zoneNum[14] = "山西"
        zoneNum[15] = "内蒙古"
        zoneNum[21] = "辽宁"
        zoneNum[22] = "吉林"
        zoneNum[23] = "黑龙江"
        zoneNum[31] = "上海"
        zoneNum[32] = "江苏"
        zoneNum[33] = "浙江"
        zoneNum[34] = "安徽"
        zoneNum[35] = "福建"
        zoneNum[36] = "江西"
        zoneNum[37] = "山东"
        zoneNum[41] = "河南"
        zoneNum[42] = "湖北"
        zoneNum[43] = "湖南"
        zoneNum[44] = "广东"
        zoneNum[45] = "广西"
        zoneNum[46] = "海南"
        zoneNum[50] = "重庆"
        zoneNum[51] = "四川"
        zoneNum[52] = "贵州"
        zoneNum[53] = "云南"
        zoneNum[54] = "西藏"
        zoneNum[61] = "陕西"
        zoneNum[62] = "甘肃"
        zoneNum[63] = "青海"
        zoneNum[64] = "新疆"
        zoneNum[71] = "台湾"
        zoneNum[81] = "香港"
        zoneNum[82] = "澳门"
        zoneNum[91] = "外国"
    }

    /**
     * 身份证验证
     *
     * @param certNo 号码内容
     * @return 是否有效 null和"" 都是false
     */
    @SuppressLint("DefaultLocale")
    fun isIDCard(certNo: String?): Boolean {
        if (certNo == null || certNo.length != 15 && certNo.length != 18)
            return false
        val cs = certNo.toUpperCase().toCharArray()
        //校验位数
        var power = 0
        for (i in cs.indices) {
            if (i == cs.size - 1 && cs[i] == 'X')
                break//最后一位可以 是X或x
            if (cs[i] < '0' || cs[i] > '9')
                return false
            if (i < cs.size - 1) {
                power += (cs[i] - '0') * POWER_LIST[i]
            }
        }

        // 校验区位码
        if (!zoneNum.containsKey(Integer.valueOf(certNo.substring(0, 2)))) {
            return false
        }

        // 校验年份
        val year = if (certNo.length == 15) idCardCalendar.toString() + certNo.substring(6, 8) else certNo.substring(6, 10)

        val iYear = Integer.parseInt(year)
        if (iYear < 1900 || iYear > Calendar.getInstance().get(Calendar.YEAR)) {
            // 1900年的PASS，超过今年的PASS
            return false
        }

        // 校验月份
        val month = if (certNo.length == 15) certNo.substring(8, 10) else certNo.substring(10, 12)
        val iMonth = Integer.parseInt(month)
        if (iMonth < 1 || iMonth > 12) {
            return false
        }

        // 校验天数
        val day = if (certNo.length == 15) certNo.substring(10, 12) else certNo.substring(12, 14)
        val iDay = Integer.parseInt(day)
        if (iDay < 1 || iDay > 31) return false

        // 校验"校验码"
        return if (certNo.length == 15) true else cs[cs.size - 1] == PARITY_BIT[power % 11]
    }
}
