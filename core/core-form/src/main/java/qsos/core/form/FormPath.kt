package qsos.core.form

/**
 * @author : 华清松
 * 表单模块页面路由
 */
object FormPath {

    const val FORM = "FORM"
    /*表单页*/
    const val MAIN = "/$FORM/MAIN"

    const val FORM_TYPE = "FORM_TYPE"
    const val FORM_CONNECT_ID = "FORM_CONNECT_ID"
    const val FORM_EDIT = "FORM_EDIT"
    /**0 表单内提交 1回传提交*/
    const val FORM_POST_TYPE = "FORM_POST_TYPE"
    const val FORM_POST_CODE = 0 * 10011
    const val FORM_ID = "FORM_ID"

    /*表单项录入页*/
    const val ITEM_INPUT = "/$FORM/ITEM_INPUT"
    /*表单项用户页*/
    const val ITEM_USERS = "/$FORM/ITEM_USERS"
    /*审核协调选择用户*/
    const val ITEM_USER = "/$FORM/ITEM_USER"

    const val choseUserCode = 20001
}