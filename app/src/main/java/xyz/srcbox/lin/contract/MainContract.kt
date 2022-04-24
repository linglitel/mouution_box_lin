package xyz.srcbox.lin.contract

import com.alibaba.fastjson.JSONObject
import xyz.srcbox.lin.base.BaseContract

interface MainContract {
    interface Model : BaseContract.Model {
        fun getAppUpdateAndNotificationInfo(): JSONObject
        fun getNetWorkAppTypes(): JSONObject
    }

    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter {
        fun getNetWorkAppTypes()
        fun checkAppUpdateAndNotificationInfo()
    }
}