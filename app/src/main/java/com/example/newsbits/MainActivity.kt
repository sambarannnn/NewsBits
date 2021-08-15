package com.example.newsbits

import android.content.ContentValues.TAG
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NewsItemClicked {
    private lateinit var mAdapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rcView.layoutManager = LinearLayoutManager(this)
        fetchData()
        mAdapter = NewsListAdapter(this)
        rcView.adapter = mAdapter
    }


    private fun fetchData() {
        //https://stackoverflow.com/questions/64585493/unexpected-response-code-403-while-fetching-data-from-apis
        val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=36dc766bd278427db844ef69a8b3b600"
        val jsonObjectRequest = object: JsonObjectRequest (Request.Method.GET, url, null,
            {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)

            },
            {
                Log.d(TAG, "ERROR")
            }
        ){//this api was giving unexpected response code 403
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }



    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder();
        val intent = builder.build();
        intent.launchUrl(this, Uri.parse(item.url));
    }
}