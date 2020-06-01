package com.centosquare.devatease.goappdriver.utils

import android.os.AsyncTask
import android.util.Log
import com.centosquare.devatease.goappdriver.interfaces.TaskLoadedCallback
import com.centosquare.devatease.goappdriver.interfaces.TaskLoadedDurationCallback
import org.json.JSONObject

class DistanceAndDuration(private val taskLoadedListner: TaskLoadedCallback, private val taskLoadedDurationListner: TaskLoadedDurationCallback, directionMode: String) :
    AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

    // internal var taskCallBack: TaskLoadedCallback
    internal var directionMode = "driving"
    //   String time;
    internal var distance: String? = null
    internal var time: String? = null



    init {
        //  this.taskCallBack = mContext as TaskLoadedCallback
        this.directionMode = directionMode
    }

    override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
        val jObject: JSONObject
        var routes: List<List<HashMap<String, String>>>? = null

        try {
            jObject = JSONObject(jsonData[0])
            Log.d("mylog", jsonData[0])
            val parser = DataParser()
            Log.d("mylog", parser.toString())

            // Starts parsing data
            routes = parser.parsetad(jObject)
            Log.d("mylog", "Executing routes")
            Log.d("mylog", routes.toString())

        } catch (e: Exception) {
            Log.d("mylog", e.toString())
            e.printStackTrace()
        }

        return routes
    }

    // Executes in UI thread, after the parsing process
    override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
        var points: ArrayList<String>

        // Traversing through all the routes
        for (i in result.indices) {
            points = ArrayList()
            //lineOptions = new PolylineOptions();
            // Fetching i-th route
            val path = result[i]
            // Fetching all the points in i-th route

            for (j in path.indices) {
                val point = path[j]
                time = point["duration"]
                distance = point["distance"]
                points.add(time.toString())
                points.add(distance.toString())
            }
            Log.d("mylog", "onPostExecute lineoptions decoded")
            taskLoadedDurationListner.onDuration(time!!)

        }

    }

    companion object {

        var timeCheck: String? = null
    }
}