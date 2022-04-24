package xyz.srcbox.lin.presenter

import android.content.Context
import xyz.srcbox.lin.contract.AppListContract
import xyz.srcbox.lin.model.AppListModel
import xyz.srcbox.lin.ui.AppList

class AppListPresenter(val v: AppListContract.View) : AppListContract.Presenter {
    private val appListModel = AppListModel()
    private var t: Thread? = null
    override fun installAppInfo(sortType: Int) {
        v.start()
        t = Thread {
            v.loading()
            val arrL = appListModel.getInstallAppInfo(v as Context)
            when (sortType) {
                AppList.sortSizeType -> {
                    arrL.sortByDescending { it.appSize }
                }

                AppList.sortTimeType -> {
                    arrL.sortByDescending { it.appInstallTime }
                }
            }
            v.listApp(arrL)
        }
        t?.start()
    }

}