package qsos.core.form

/**
 * @author : 华清松
 * 表单模块页面路由
 */
object FormPath {

    const val FORM = "FORM"
    /**表单页*/
    const val MAIN = "/$FORM/MAIN"
    /**表单ID，必传，将从数据库获取表单结构 Long*/
    const val FORM_ID = "/$MAIN/FORM_ID"
    /**表单修改类型，可选，表单是否可修改，默认是 Boolean*/
    const val FORM_EDIT = "/$MAIN/FORM_EDIT"

    /**表单项文本录入页*/
    const val FORM_ITEM_INPUT = "/$FORM/FORM_ITEM_INPUT"
    /**表单项多用户选择/查看页*/
    const val FORM_ITEM_USERS = "/$FORM/FORM_ITEM_USERS"
    /**表单项ID key*/
    const val FORM_ITEM_ID = "/FORM_ITEM/FORM_ITEM_ID"

}