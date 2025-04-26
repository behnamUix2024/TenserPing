package com.behnamuix.tenserpingx.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.behnamuix.tenserpingx.Model.HistoryModel
import com.behnamuix.tenserpingx.R
class HistoryDialogAdapter(val listModel:List<HistoryModel>, val context: Context):

    RecyclerView.Adapter<HistoryDialogAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val card_bg_hist:CardView=itemView.findViewById(R.id.bg_card_hist)
            val tv_date: TextView = itemView.findViewById(R.id.tv_date)
            val tv_type: TextView = itemView.findViewById(R.id.tv_type)
            val tv_ip: TextView = itemView.findViewById(R.id.tv_ip)
            val tv_ping: TextView = itemView.findViewById(R.id.tv_ping)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.frag_hist_list_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = listModel[position]
            val x=getEven(position)
            if(x){
                holder.card_bg_hist.setCardBackgroundColor(holder.card_bg_hist.context.getColor(R.color.white))
            }else{
                holder.card_bg_hist.setCardBackgroundColor(holder.card_bg_hist.context.getColor(R.color.stripped))

            }
            holder.tv_date.text = currentItem.hist_date
            holder.tv_type.text = currentItem.hist_type
            holder.tv_ip.text = currentItem.hist_ip
            holder.tv_ping.text = currentItem.hist_ping
        }

    private fun getEven(position: Int): Boolean {
        return position % 2 == 0 // نسخه ساده تر و خواناتر

    }

    override fun getItemCount(): Int {
            return listModel.size
        }
    }