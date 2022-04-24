package xyz.srcbox.lin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.srcbox.file.R
import kotlinx.android.synthetic.main.app_list_item.view.*
import xyz.srcbox.lin.data.UserAppData
import xyz.srcbox.lin.data.`object`.AppSetting
import xyz.srcbox.lin.ui.AppFunActivity
import xyz.srcbox.lin.util.EggUtil

class AppListAdapter(val context: Context, private val arrayList: ArrayList<UserAppData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.app_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is xyz.srcbox.lin.adapter.AppListAdapter.ViewHolder) {
            holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val listContainer = itemView.findViewById<LinearLayout>(R.id.list_container)
            val arrayListO = arrayList[position]
            itemView.app_name.text = arrayListO.name
//            itemView.app_icon.setImageDrawable(arrayListO.appIcon)
            Glide.with(context).load(arrayListO.appIcon).placeholder(R.mipmap.placeholder)
                .error(R.mipmap.error).into(itemView.app_icon)
            itemView.app_size.text = EggUtil.getFileDiffType(arrayListO.appSize)

            listContainer.setBackgroundColor(Color.parseColor("#ffffff"))

            listContainer.tag = "-1"
            itemView.setOnClickListener {
                if (!xyz.srcbox.lin.adapter.AppListAdapter.Companion.isSelect) {
                    val intent = Intent(context, AppFunActivity::class.java)
                    intent.putExtra("package", arrayListO.appPackageName)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    if (listContainer.tag == "-1") {
                        listContainer.tag = "0"
                        listContainer.setBackgroundColor(Color.parseColor(AppSetting.colorTransTress))
                        /*ExtTable.getExtExtractsFile().listFiles()!!.forEach {
                            val j = JSON.parseObject(EggIO.readFile(it))
                            if (j.getString("filePackage") == arrayListO.appPackageName) {
                                return@setOnClickListener
                            }
                        }*/
                        xyz.srcbox.lin.adapter.AppListAdapter.Companion.selectApp.add(arrayListO)
                    } else {
                        listContainer.setBackgroundColor(Color.parseColor("#ffffff"))
                        xyz.srcbox.lin.adapter.AppListAdapter.Companion.selectApp.remove(arrayListO)
                    }
                }
            }
        }
    }

    companion object {
        var isSelect = false
        val selectApp = ArraySet<UserAppData>()
        var selColor = -1
    }
}