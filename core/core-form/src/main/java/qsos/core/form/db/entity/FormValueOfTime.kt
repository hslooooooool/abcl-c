package qsos.core.form.db.entity

/**
 * @author : 华清松
 * 表单项值-时间实体类
 * @param timeStart 开始时间毫秒数
 * @param timeEnd 结束时间毫秒数
 */
data class FormValueOfTime(
        var timeStart: Long = 0L,
        var timeEnd: Long? = null
)