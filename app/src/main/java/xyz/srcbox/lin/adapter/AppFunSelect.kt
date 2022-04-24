package xyz.srcbox.lin.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srcbox.file.R
import kotlinx.android.synthetic.main.app_fun_select_recycler_item.view.*
import xyz.srcbox.lin.util.EggUtil

class AppFunSelect(val context: Context, private val arrayList: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.app_fun_select_recycler_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is xyz.srcbox.lin.adapter.AppFunSelect.ViewHolder) {
            holder.bind(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

//            itemView.app_fun_type_select.isSelected = arrayList[position].isSelect
            itemView.app_fun_type_title.text = arrayList[position]
            itemView.setOnClickListener {
                EggUtil.copyText(
                    context as Activity,
                    itemView.app_fun_type_title.text.toString()
                )
                EggUtil.toast("已复制")
            }

            itemView.setOnLongClickListener {
                EggUtil.copyText(
                    context as Activity,
                    itemView.app_fun_type_title.text.toString()
                )
                EggUtil.toast("已复制")
                false
            }
        }
    }
}