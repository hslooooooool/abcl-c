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

}