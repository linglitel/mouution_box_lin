package xyz.srcbox.lin.model

import xyz.srcbox.lin.contract.ContractShitSmooth
import xyz.srcbox.lin.ui.util.BlindWork
import java.io.InputStream

class ShitSmoothModel : ContractShitSmooth.Model {
    override fun getShitSmooth(wordFileIn: InputStream, title: String, len: Long): String {
        return BlindWork().speak(
            wordFileIn,
            title,
            len
        )
    }
}