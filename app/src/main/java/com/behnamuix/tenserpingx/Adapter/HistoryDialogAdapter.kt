package com.behnamuix.tenserpingx.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.behnamuix.tenserpingx.Model.HistoryModel
import com.behnamuix.tenserpingx.R

class HistoryDialogAdapter(val listModel: List<HistoryModel>, val context: Context) :

    RecyclerView.Adapter<HistoryDialogAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card_bg_hist: CardView = itemView.findViewById(R.id.bg_card_hist)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        val tv_type: TextView = itemView.findViewById(R.id.tv_type)
        val tv_ip: TextView = itemView.findViewById(R.id.tv_ip)
        val tv_ping: TextView = itemView.findViewById(R.id.tv_ping)
        val img_type: ImageView = itemView.findViewById(R.id.img_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.frag_hist_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listModel[position]
        val x = getEven(position)
        if (x) {
            holder.card_bg_hist.setCardBackgroundColor(holder.card_bg_hist.context.getColor(R.color.white))
        } else {
            holder.card_bg_hist.setCardBackgroundColor(holder.card_bg_hist.context.getColor(R.color.stripped))

        }
        holder.tv_date.text = currentItem.hist_date
        holder.tv_type.text = currentItem.hist_type
        holder.tv_ip.text = currentItem.hist_ip
        holder.tv_ping.text = currentItem.hist_ping
        when (currentItem.hist_type) {
            "5G" -> holder.img_type.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.icon_5g)
            )

            "4G" -> holder.img_type.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.icon_4g)
            )

            "WIFI" -> holder.img_type.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.icon_wifi)
            )

            else -> {
                holder.tv_type.visibility = View.VISIBLE
                holder.img_type.visibility = View.GONE

            }
        }
    }

    private fun getEven(position: Int): Boolean {
        return position % 2 == 0 // نسخه ساده تر و خواناتر

    }

    override fun getItemCount(): Int {
        return listModel.size
    }
}