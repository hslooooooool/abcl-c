package vip.qsos.demo.netservice.data.mock

import qsos.lib.netservice.mock.AbstractMockData

class UserMockData : AbstractMockData() {
    override val method: String
        get() = "GET"
    override val group: String
        get() = "account"
    override val path: String
        get() = "api/user"
    override val filename: String
        get() = "user.json"
    override val requestTime: Long
        get() = 2000
}
