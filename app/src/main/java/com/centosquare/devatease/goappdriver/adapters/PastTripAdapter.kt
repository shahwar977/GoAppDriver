import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.models.PastRidesModel
import kotlin.collections.ArrayList

class PastTripAdapter(
    private val context: Context,
    private val list: ArrayList<PastRidesModel>
) :
    RecyclerView.Adapter<PastTripAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastTripAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_past_trip, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PastTripAdapter.MyViewHolder, position: Int) {


        holder.customerNameTv.setText("Customer Name : ${list.get(position).customerName}")
        holder.destinationTv.setText("Destination : ${list.get(position).dropOffAddress}")
        holder.fareTv.setText("Rs : ${list.get(position).rideFare}/-")




    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

         internal val customerNameTv: TextView
         internal val destinationTv: TextView
         internal val fareTv: TextView
        // private val btnReorder: Button? = null

        init {


            customerNameTv = view.findViewById(R.id.tv_date_past_trip)
            destinationTv = view.findViewById(R.id.tv_name_past_trip)
            fareTv = view.findViewById(R.id.tv_price_past_trip)


        }

    }


}
