package xyz.srcbox.lin.contract

import xyz.srcbox.lin.base.BaseContract
import java.io.InputStream

class ContractShitSmooth {
    interface Model : BaseContract.Model {
        fun getShitSmooth(wordFileIn: InputStream, title: String, len: Long): String
    }

    interface View : BaseContract.View {
        fun resultShitSmooth(string: String)
        fun getTitleV(): String
        fun getLen(): Long
        fun getWordTab(): InputStream
        fun copyShitSmooth()
    }

    interface Presenter : BaseContract.Presenter {
        fun makeShitSmooth()
    }
}