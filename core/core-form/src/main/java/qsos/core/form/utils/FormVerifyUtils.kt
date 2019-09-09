package qsos.core.form.utils

import android.text.TextUtils
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.lib.utils.IDCardUtil

/**
 * @author : 华清松
 * 表单填写结果校验 工具类
 */
object FormVerifyUtils {
    /*校验信息*/
    class Verify {
        var pass = true
        var message: String = "校验通过"

        constructor()
        constructor(pass: Boolean, message: String) {
            this.pass = pass
            this.message = message
        }
    }

    /**校验*/
    fun verify(form: FormEntity): Verify {
        for (formItem in form.formItem!!) {
            /*表单项值类型，0：文本展示；1：输入；2：选项；3：时间；4：人员；5：附件；6：位置*/
            val verify = when (formItem.valueType) {
                1 -> input(formItem)
                2 -> chose(formItem)
                3 -> time(formItem)
                4 -> user(formItem)
                5 -> file(formItem)
                6 -> location(formItem)
                else -> Verify()
            }
            if (!verify.pass) {
                return verify
            }
        }
        return Verify()
    }

    /**输入校验*/
    private fun input(formItem: FormItem): Verify {
        val required = formItem.require
        val nullValue = formItem.formItemValue?.values == null || formItem.formItemValue?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请填写【${formItem.title}】")
        } else if (required && !nullValue) {
            val inputContent = formItem.formItemValue?.values!![0].text?.content
            /*必填，已填，判断文字是否为空*/
            if (TextUtils.isEmpty(inputContent)) {
                return Verify(false, "请填写【${formItem.title}】")
            } else {
                /*必填，已填，判断文字是否符合输入限制*/
                val typeRight = when (formItem.formItemValue!!.limitType) {
                    "ID_CARD" -> IDCardUtil.isIDCard(inputContent)
                    else -> true
                }
                if (!typeRight) {
                    return Verify(false, "【${formItem.title}】格式不正确")
                }
            }
        }
        if (!nullValue) {
            if (formItem.formItemValue!!.limitMin != null && formItem.formItemValue?.values!![0].text!!.content!!.length < formItem.formItemValue!!.limitMin!!) {
                /*已填，判断最小输入字数是否满足*/
                return Verify(false, "【${formItem.title}】至少输入 ${formItem.formItemValue!!.limitMin!!} 字")
            }
            if (formItem.formItemValue!!.limitMax != null && formItem.formItemValue?.values!![0].text!!.content!!.length > formItem.formItemValue!!.limitMax!!) {
                /*已填，判断最小输入字数是否满足*/
                return Verify(false, "【${formItem.title}】至多输入 ${formItem.formItemValue!!.limitMin!!} 字")
            }
        }
        return Verify()
    }

    /**选项校验*/
    private fun chose(formItem: FormItem): Verify {
        val required = formItem.require
        val nullValue = formItem.formItemValue?.values == null || formItem.formItemValue?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请选择【${formItem.title}】")
        } else if (required && !nullValue) {
            if (formItem.formItemValue!!.limitMax == 1) {
                /*必填，单选，判断是否选择*/
                var chose = false
                for (value in formItem.formItemValue?.values!!) {
                    if (value.check.ckChecked) {
                        chose = true
                    }
                }
                if (!chose) {
                    return Verify(false, "请选择【${formItem.title}】")
                }
            } else {
                /*必填，多选，判断是否选择*/
                var chose = 0
                for (value in formItem.formItemValue?.values!!) {
                    if (value.check.ckChecked) {
                        chose++
                    }
                }
                if (formItem.formItemValue!!.limitMin != null && formItem.formItemValue!!.limitMin!! > chose) {
                    return Verify(false, "【${formItem.title}】至少选择 ${formItem.formItemValue!!.limitMin!!} 项")
                }
                if (formItem.formItemValue!!.limitMax != null && formItem.formItemValue!!.limitMax!! < chose) {
                    return Verify(false, "【${formItem.title}】至多选择 ${formItem.formItemValue!!.limitMin!!} 项")
                }
            }
        }
        return Verify()
    }

    /**时间校验*/
    private fun time(formItem: FormItem): Verify {
        val required = formItem.require
        val nullValue = formItem.formItemValue?.values == null || formItem.formItemValue?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请设置【${formItem.title}】")
        } else if (required && !nullValue) {
            if (formItem.formItemValue!!.values!!.size == 1) {
                /*必填，已填，判断单个时间是否设置*/
                if (formItem.formItemValue?.values!![0].time.timeStart == 0L) {
                    return Verify(false, "请设置【${formItem.title}】")
                }
            } else if (formItem.formItemValue!!.values!!.size == 2) {
                val startTime = formItem.formItemValue?.values!![0].time.timeStart
                val endTime = formItem.formItemValue?.values!![1].time.timeStart
                /*必填，已填，判断区间时间是否设置*/
                if (startTime == 0L) {
                    return Verify(false, "请设置【${formItem.title}】的开始")
                }
                if (endTime == 0L) {
                    return Verify(false, "请设置【${formItem.title}】的结束")
                }
                if (startTime < endTime) {
                    return Verify(false, "结束时间不能小于开始时间，请修改")
                }
            }
        }
        return Verify()
    }

    /**人员校验*/
    private fun user(formItem: FormItem): Verify {
        val required = formItem.require
        val nullValue = formItem.formItemValue?.values == null || formItem.formItemValue?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请设置【${formItem.title}】")
        }
        if (!nullValue) {
            if (formItem.formItemValue!!.limitMin != null && formItem.formItemValue!!.values!!.size < formItem.formItemValue!!.limitMin!!) {
                /*已填，判断人员数量是否满足最小要求*/
                return Verify(false, "【${formItem.title}】至少设置 ${formItem.formItemValue!!.limitMin} 人")
            }
            if (formItem.formItemValue!!.limitMax != null && formItem.formItemValue!!.limitMax!! > formItem.formItemValue!!.values!!.size) {
                /*已填，判断人员数量是否满足最大要求*/
                return Verify(false, "【${formItem.title}】至多设置 ${formItem.formItemValue!!.limitMax} 人")
            }
        }
        return Verify()
    }

    /**文件校验*/
    private fun file(formItem: FormItem): Verify {
        val required = formItem.require
        val nullValue = formItem.formItemValue?.values == null || formItem.formItemValue?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请上传【${formItem.title}】")
        }
        if (!nullValue) {
            val min = formItem.formItemValue!!.limitMin ?: 0
            val max = formItem.formItemValue!!.limitMax ?: 0
            if (min > 0 || max > 0) {
                if (
                        formItem.formItemValue!!.limitMin != null
                        && formItem.formItemValue!!.values!!.size < formItem.formItemValue!!.limitMin!!
                ) {
                    /*已填，判断文件数量是否满足最小要求*/
                    return Verify(false, "【${formItem.title}】至少上传 ${formItem.formItemValue!!.limitMin} 件")
                }
                if (
                        formItem.formItemValue!!.limitMax != null
                        && formItem.formItemValue!!.limitMax!! < formItem.formItemValue!!.values!!.size
                ) {
                    /*已填，判断文件数量是否满足最大要求*/
                    return Verify(false, "【${formItem.title}】至多设置 ${formItem.formItemValue!!.limitMax} 件")
                }
            }
        }
        return Verify()
    }

    /**位置校验*/
    private fun location(formItem: FormItem): Verify {
        val required = formItem.require
        val nullValue = formItem.formItemValue?.values == null || formItem.formItemValue?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请设置【${formItem.title}】")
        } else if (required && !nullValue) {
            /*必填，已填，判断位置是否为空*/
            if (TextUtils.isEmpty(formItem.formItemValue?.values!![0].location.locName)) {
                return Verify(false, "请设置【${formItem.title}】")
            }
        }
        return Verify()
    }
}