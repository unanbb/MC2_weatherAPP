package com.example.appweather

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val CITY: String = "anyang-si,kr"
    val API: String = "2590760e901e5c4cf73fdae8a6e08da0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()
    }
    inner class weatherTask() : AsyncTask<String, Void, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.GONE
            findViewById<TextView>(R.id.errortext).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                    .readText(Charsets.UTF_8)

            }
            catch(e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val jsonObj=JSONObject(result)
                val main=jsonObj.getJSONObject("main")
                val sys=jsonObj.getJSONObject("sys")
                val wind=jsonObj.getJSONObject("wind")
                val weather=jsonObj.getJSONArray("weather").getJSONObject(0)
                val updateAt:Long=jsonObj.getLong("dt")
                val updatedAtText="Update at: "+SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.KOREAN).format(Date(updateAt*1000))
                val temp=main.getString("temp")+"℃"
                val tempMin = "최저 온도: "+main.getString("temp_min")+"℃"
                val tempMax = "최고 온도: "+main.getString("temp_max")+"℃"
                val pressure = main.getString("pressure")+"hPa"
                val humidity = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")+"m/s"
                val weatherDescription= weather.getString("description")
                val address=jsonObj.getString("name")+", "+sys.getString("country")

                findViewById<TextView>(R.id.address).text=address
                findViewById<TextView>(R.id.updated_at).text=updatedAtText
                findViewById<TextView>(R.id.status).text=weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text=temp
                findViewById<TextView>(R.id.temp_min).text=tempMin
                findViewById<TextView>(R.id.temp_max).text=tempMax
                findViewById<TextView>(R.id.sunrise).text=SimpleDateFormat("hh:mm a",Locale.KOREAN).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text=SimpleDateFormat("hh:mm a",Locale.KOREAN).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text=windSpeed
                findViewById<TextView>(R.id.pressure).text=pressure
                findViewById<TextView>(R.id.humidity).text=humidity

                findViewById<ProgressBar>(R.id.loader).visibility=View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.VISIBLE
            }
            catch(e: Exception){
                findViewById<ProgressBar>(R.id.loader).visibility=View.GONE
                findViewById<TextView>(R.id.errortext).visibility=View.VISIBLE
            }
        }
        fun getRecommends(temp : Int) : String {
            return when (temp) {
                in 5..8 -> "울 코트, 가죽 옷, 기모"
                in 9..11 -> "트렌치 코트, 야상, 점퍼"
                in 12..16 -> "자켓, 가디건, 청자켓"
                in 17..19 -> "니트, 맨투맨, 후드, 긴바지"
                in 20..22 -> "블라우스, 긴팔 티, 슬랙스"
                in 23..27 -> "얇은 셔츠, 반바지, 면바지"
                in 28..50 -> "민소매, 반바지, 린넨 옷"
                else -> "패딩, 누빔 옷, 목도리"
            }
        }
    }
}