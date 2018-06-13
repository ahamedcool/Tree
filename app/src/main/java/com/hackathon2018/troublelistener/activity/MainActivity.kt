package com.hackathon2018.troublelistener.activity

import android.app.Activity
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hackathon2018.troublelistener.R
import com.hackathon2018.troublelistener.adapter.BaseAdapter
import com.hackathon2018.troublelistener.adapter.RecyclerItem
import com.hackathon2018.troublelistener.util.OnItemClickListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.uiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_post.*


class MainActivity : AppCompatActivity(), OnItemClickListener {
    override fun onItemClick(position: Int) {
        val title = mItems[position].title
        val writer = mItems[position].writer
        val content = mItems[position].content
        val idx = mItems[position].idx
        val private = mItems[position].private
        startActivity<DetailActivity>("idx" to idx, "title" to title, "writer" to writer, "content" to content, "private" to private, "id" to id)
    }

    private var adapter: RecyclerView.Adapter<*>? = null
    private val mItems = ArrayList<RecyclerItem>()
    private var id : String? = null
    private var mPreference: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        mPreference = getSharedPreferences("user", Activity.MODE_PRIVATE)
        id = mPreference!!.getString("id", null)
        refresh()
        btn.setOnClickListener {
            startActivity<PostActivity>()
        }
    }
    fun refresh(){
        mItems.clear()
        getPrivateTrouble()
        getPublicTrouble()
    }
    private fun initRecyclerView() { // RecyclerView 기본세팅
        // 변경될 가능성 o : false 로 , 없다면 true.
        recycler!!.setHasFixedSize(false)

        adapter = BaseAdapter(mItems, this, this)
        recycler!!.adapter = adapter
        recycler!!.layoutManager = LinearLayoutManager(this)
    }
    private fun getPrivateTrouble() {
        var result: String = "ERR"
        doAsync {
            try {
                val body = "id=$id" //parameter
                val u = URL("http://10.0.2.2:8081/troubletome.php")
                val huc = u.openConnection() as HttpURLConnection
                huc.readTimeout = 4000
                huc.connectTimeout = 4000
                huc.requestMethod = "POST"
                huc.doInput = true
                huc.doOutput = true
                huc.setRequestProperty("utf-8", "application/x-www-form-urlencoded")
                val os = huc.outputStream
                os.write(body.toByteArray(charset("utf-8")))
                os.flush()
                os.close()

                val `is` = BufferedReader(InputStreamReader(huc.inputStream, "utf-8"))
                var ch: Int
                ch = `is`.read()
                val sb = StringBuffer()
                while (ch != -1) {
                    sb.append(ch.toChar())
                    ch = `is`.read()
                }

                `is`.close()

                result = sb.toString()


            } catch (e: Exception) {
                result = "ERR"
            }
            uiThread {
                if(!result.contains("ERR")) {
                    val jsonObject = JSONArray(result)
                    for(i in 0..(jsonObject.length() - 1)){
                        val innerObject = jsonObject.getJSONObject(i)
                        mItems.add(RecyclerItem(
                                innerObject.getInt("idx"),
                                innerObject.getString("title"),
                                innerObject.getString("writer"),
                                innerObject.getString("content"), true))
                    }
                    adapter!!.notifyDataSetChanged()
                }
                else {
                    Toasty.error(it, "오류가 발생했습니다!").show()
                }
            }
        }
    }
    private fun getPublicTrouble() {
        var result: String = "ERR"
        doAsync {
            try {
                val body = "id=$id" //parameter
                val u = URL("http://10.0.2.2:8081/trouble.php")
                val huc = u.openConnection() as HttpURLConnection
                huc.readTimeout = 4000
                huc.connectTimeout = 4000
                huc.requestMethod = "POST"
                huc.doInput = true
                huc.doOutput = true
                huc.setRequestProperty("utf-8", "application/x-www-form-urlencoded")
                val os = huc.outputStream
                os.write(body.toByteArray(charset("utf-8")))
                os.flush()
                os.close()

                val `is` = BufferedReader(InputStreamReader(huc.inputStream, "utf-8"))
                var ch: Int
                ch = `is`.read()
                val sb = StringBuffer()
                while (ch != -1) {
                    sb.append(ch.toChar())
                    ch = `is`.read()
                }

                `is`.close()

                result = sb.toString()


            } catch (e: Exception) {
                result = "ERR"
            }
            uiThread {
                if(!result.contains("ERR")) {
                    val jsonObject = JSONArray(result)
                    for(i in 0..(jsonObject.length() - 1)){
                        val innerObject = jsonObject.getJSONObject(i)
                        mItems.add(RecyclerItem(
                                innerObject.getInt("idx"),
                                innerObject.getString("title"),
                                innerObject.getString("writer"),
                                innerObject.getString("content"),false))
                    }
                    adapter!!.notifyDataSetChanged()
                }
                else {
                    Toasty.error(it, "오류가 발생했습니다!").show()
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_refresh, menu)//Menu Resource, Menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
