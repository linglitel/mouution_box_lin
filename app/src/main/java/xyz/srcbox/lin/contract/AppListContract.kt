package xyz.srcbox.lin.contract

import android.content.Context
import xyz.srcbox.lin.data.UserAppData
import xyz.srcbox.lin.base.BaseContract

interface AppListContract {
    interface Model : BaseContract.Model {
        fun getInstallAppInfo(context: Context): ArrayList<UserAppData>
    }

    interface View : BaseContract.View {
        fun start()
        fun listApp(arrayList: ArrayList<UserAppData>)
        fun loading()
    }

    interface Presenter : BaseContract.Presenter {
        fun installAppInfo(sortType: Int)
    }
}