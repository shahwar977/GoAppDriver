package com.centosquare.devatease.goappdriver.activities


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.centosquare.devatease.goappdriver.R

class NotificationAdapter(val context: Context, private val notificationlist: ArrayList<Notification>
): RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    override fun onBindViewHolder(holder: NotificationAdapter.MyViewHolder, position: Int) {



        holder.notiname.text = notificationlist.get(position).toridename
        holder.notipickup.text = notificationlist.get(position).toridepickup
        holder.notinum.text = notificationlist.get(position).toridenumber
        Glide.with(context).load(notificationlist.get(position).image).into(holder.notipic)

    }

    override fun getItemCount(): Int {
return notificationlist.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)

        return MyViewHolder(itemView)
    }


class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    internal var notiname: TextView
    internal var notipickup: TextView
    internal var notinum: TextView
internal var notipic: ImageView


    init {


        notiname = view.findViewById(R.id.notiname)
        notipickup =  view.findViewById(R.id.notipickup)
        notinum = view.findViewById(R.id.notinumber)
        notipic = view.findViewById(R.id.notipic)

    }


}
}