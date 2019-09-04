package qsos.core.form.utils

import android.text.TextUtils
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.lib.utils.IDCardUtil

/**
 * @author : 华清松
 * @description : 表单填写结果校验 工具类
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
        for (formItem in form.form_item!!) {
            /*表单项值类型，0：文本展示；1：输入；2：选项；3：时间；4：人员；5：附件；6：位置*/
            val verify = when (formItem.form_item_type) {
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
        val required = formItem.form_item_required
        val nullValue = formItem.form_item_value?.values == null || formItem.form_item_value?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请填写【${formItem.form_item_key}】")
        } else if (required && !nullValue) {
            val inputContent = formItem.form_item_value?.values!![0].input_value
            /*必填，已填，判断文字是否为空*/
            if (TextUtils.isEmpty(inputContent)) {
                return Verify(false, "请填写【${formItem.form_item_key}】")
            } else {
                /*必填，已填，判断文字是否符合输入限制*/
                val typeRight = when (formItem.form_item_value!!.limit_type) {
                    "ID_CARD" -> IDCardUtil.isIDCard(inputContent)
                    else -> true
                }
                if (!typeRight) {
                    return Verify(false, "【${formItem.form_item_key}】格式不正确")
                }
            }
        }
        if (!nullValue) {
            if (formItem.form_item_value!!.limit_min != null && formItem.form_item_value?.values!![0].input_value!!.length < formItem.form_item_value!!.limit_min!!) {
                /*已填，判断最小输入字数是否满足*/
                return Verify(false, "【${formItem.form_item_key}】至少输入 ${formItem.form_item_value!!.limit_min!!} 字")
            }
            if (formItem.form_item_value!!.limit_max != null && formItem.form_item_value?.values!![0].input_value!!.length > formItem.form_item_value!!.limit_max!!) {
                /*已填，判断最小输入字数是否满足*/
                return Verify(false, "【${formItem.form_item_key}】至多输入 ${formItem.form_item_value!!.limit_min!!} 字")
            }
        }
        return Verify()
    }

    /**选项校验*/
    private fun chose(formItem: FormItem): Verify {
        val required = formItem.form_item_required
        val nullValue = formItem.form_item_value?.values == null || formItem.form_item_value?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请选择【${formItem.form_item_key}】")
        } else if (required && !nullValue) {
            if (formItem.form_item_value!!.limit_max == 1) {
                /*必填，单选，判断是否选择*/
                var chose = false
                for (value in formItem.form_item_value?.values!!) {
                    if (value.ck_check) {
                        chose = true
                    }
                }
                if (!chose) {
                    return Verify(false, "请选择【${formItem.form_item_key}】")
                }
            } else {
                /*必填，多选，判断是否选择*/
                var chose = 0
                for (value in formItem.form_item_value?.values!!) {
                    if (value.ck_check) {
                        chose++
                    }
                }
                if (formItem.form_item_value!!.limit_min != null && formItem.form_item_value!!.limit_min!! > chose) {
                    return Verify(false, "【${formItem.form_item_key}】至少选择 ${formItem.form_item_value!!.limit_min!!} 项")
                }
                if (formItem.form_item_value!!.limit_max != null && formItem.form_item_value!!.limit_max!! < chose) {
                    return Verify(false, "【${formItem.form_item_key}】至多选择 ${formItem.form_item_value!!.limit_min!!} 项")
                }
            }
        }
        return Verify()
    }

    /**时间校验*/
    private fun time(formItem: FormItem): Verify {
        val required = formItem.form_item_required
        val nullValue = formItem.form_item_value?.values == null || formItem.form_item_value?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请设置【${formItem.form_item_key}】")
        } else if (required && !nullValue) {
            if (formItem.form_item_value!!.values!!.size == 1) {
                /*必填，已填，判断单个时间是否设置*/
                if (formItem.form_item_value?.values!![0].time == 0L) {
                    return Verify(false, "请设置【${formItem.form_item_key}】")
                }
            } else if (formItem.form_item_value!!.values!!.size == 2) {
                val startTime = formItem.form_item_value?.values!![0].time
                val endTime = formItem.form_item_value?.values!![1].time
                /*必填，已填，判断区间时间是否设置*/
                if (startTime == 0L) {
                    return Verify(false, "请设置【${formItem.form_item_key}】的开始")
                }
                if (endTime == 0L) {
                    return Verify(false, "请设置【${formItem.form_item_key}】的结束")
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
        val required = formItem.form_item_required
        val nullValue = formItem.form_item_value?.values == null || formItem.form_item_value?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请设置【${formItem.form_item_key}】")
        }
        if (!nullValue) {
            if (formItem.form_item_value!!.limit_min != null && formItem.form_item_value!!.values!!.size < formItem.form_item_value!!.limit_min!!) {
                /*已填，判断人员数量是否满足最小要求*/
                return Verify(false, "【${formItem.form_item_key}】至少设置 ${formItem.form_item_value!!.limit_min} 人")
            }
            if (formItem.form_item_value!!.limit_max != null && formItem.form_item_value!!.limit_max!! > formItem.form_item_value!!.values!!.size) {
                /*已填，判断人员数量是否满足最大要求*/
                return Verify(false, "【${formItem.form_item_key}】至多设置 ${formItem.form_item_value!!.limit_max} 人")
            }
        }
        return Verify()
    }

    /**文件校验*/
    private fun file(formItem: FormItem): Verify {
        val required = formItem.form_item_required
        val nullValue = formItem.form_item_value?.values == null || formItem.form_item_value?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请上传【${formItem.form_item_key}】")
        }
        if (!nullValue) {
            val min = formItem.form_item_value!!.limit_min ?: 0
            val max = formItem.form_item_value!!.limit_max ?: 0
            if (min > 0 || max > 0) {
                if (
                        formItem.form_item_value!!.limit_min != null
                        && formItem.form_item_value!!.values!!.size < formItem.form_item_value!!.limit_min!!
                ) {
                    /*已填，判断文件数量是否满足最小要求*/
                    return Verify(false, "【${formItem.form_item_key}】至少上传 ${formItem.form_item_value!!.limit_min} 件")
                }
                if (
                        formItem.form_item_value!!.limit_max != null
                        && formItem.form_item_value!!.limit_max!! < formItem.form_item_value!!.values!!.size
                ) {
                    /*已填，判断文件数量是否满足最大要求*/
                    return Verify(false, "【${formItem.form_item_key}】至多设置 ${formItem.form_item_value!!.limit_max} 件")
                }
            }
        }
        return Verify()
    }

    /**位置校验*/
    private fun location(formItem: FormItem): Verify {
        val required = formItem.form_item_required
        val nullValue = formItem.form_item_value?.values == null || formItem.form_item_value?.values!!.isEmpty()
        if (required && nullValue) {
            /*必填，未填*/
            return Verify(false, "请设置【${formItem.form_item_key}】")
        } else if (required && !nullValue) {
            /*必填，已填，判断位置是否为空*/
            if (TextUtils.isEmpty(formItem.form_item_value?.values!![0].loc_name)) {
                return Verify(false, "请设置【${formItem.form_item_key}】")
            }
        }
        return Verify()
    }
}