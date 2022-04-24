package xyz.srcbox.lin.model

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import okhttp3.*
import xyz.srcbox.lin.contract.MainContract
import xyz.srcbox.lin.data.AppStorageData
import xyz.srcbox.lin.util.EggIO

class MainModel : MainContract.Model {
    override fun getAppUpdateAndNotificationInfo(): JSONObject {
        TODO("Not yet implemented")
    }

    override fun getNetWorkAppTypes():JSONObject {
        return JSON.parseObject(EggIO.readFile(AppStorageData.getAssetsIn("json/apptypesx.json")))
    }
}