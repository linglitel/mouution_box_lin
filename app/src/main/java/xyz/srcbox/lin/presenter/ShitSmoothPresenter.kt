package xyz.srcbox.lin.presenter

import xyz.srcbox.lin.contract.ContractShitSmooth
import xyz.srcbox.lin.model.ShitSmoothModel
import xyz.srcbox.lin.util.EggUtil


class ShitSmoothPresenter(val view: ContractShitSmooth.View) : ContractShitSmooth.Presenter {
    private val shitSmoothModel = ShitSmoothModel()
    override fun makeShitSmooth() {
        if (view.getTitleV().isEmpty() || view.getLen() == 0L) {
            EggUtil.toast("参数不能为空")
        }
        view.resultShitSmooth(
            shitSmoothModel.getShitSmooth(
                view.getWordTab(),
                view.getTitleV(),
                view.getLen()
            )
        )
    }
}