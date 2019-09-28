package qsos.core.form

/**
 * @author : 华清松
 * 表单模块页面路由
 */
object FormPath {

    const val FORM = "FORM"

    /**表单页*/
    const val MAIN = "/$FORM/MAIN"
    /**表单ID，必传，将从数据库获取表单数据 Long*/
    const val FORM_ID = "/$MAIN/FORM_ID"

    /**表单项回调更新，data = 表单项 ID*/
    const val FORM_REQUEST_CODE = 1
    /**表单项ID，将从数据库获取表单项数据 Long*/
    const val FORM_ITEM_ID = "/$MAIN/FORM_ITEM_ID"
}