package com.hackathon2018.troublelistener.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hackathon2018.troublelistener.R
import com.hackathon2018.troublelistener.adapter.CommentAdapter
import com.hackathon2018.troublelistener.adapter.CommentItem
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {


    private var adapter: RecyclerView.Adapter<*>? = null
    private val mItems = ArrayList<CommentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val i : Intent = intent

        val idx : Int = i.getIntExtra("idx", 0)
        titles.text= i.getStringExtra("title")
        content.text = i.getStringExtra("content")
        writer.text = i.getStringExtra("writer")
        val id = i.getStringExtra("id")
        val private : Boolean = i.getBooleanExtra("private", false)

        if(private) {
            writer.visibility = View.GONE
        }
        initRecyclerView()
        getList(idx, private)

        post.setOnClickListener {
            postComment(idx, comment_edit.text.toString(),id,private)
        }

    }
    private fun initRecyclerView() { // RecyclerView 기본세팅
        // 변경될 가능성 o : false 로 , 없다면 true.
        comment!!.setHasFixedSize(false)

        adapter = CommentAdapter(mItems)
        comment!!.adapter = adapter
        comment!!.layoutManager = LinearLayoutManager(this)
    }
    fun getList(idx : Int, private : Boolean){
        var result: String = "ERR"
        doAsync {
            try {
                val body = "idx=$idx" //parameter
                var u = URL("http://10.0.2.2:8081/commentboard.php")
                if(private)
                    u = URL("http://10.0.2.2:8081/comment.php")
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
                if (!result.contains("ERR")) {
                    val jsonObject = JSONArray(result)
                    for(i in 0..(jsonObject.length() - 1)){
                        val innerObject = jsonObject.getJSONObject(i)
                        mItems.add(CommentItem(
                                innerObject.getString("content"),
                                innerObject.getString("writer")))
                    }
                    adapter!!.notifyDataSetChanged()
                } else{
                    Toasty.error(it, "오류가 발생했습니다!").show()
                }
            }
        }
    }
    fun postComment(idx: Int, content: String, writer: String, private: Boolean){
        var result = "ERR"
        doAsync {
            try {
                val body = "idx=$idx&content=$content&writer=$writer" //parameter
                var u = URL("http://10.0.2.2:8081/postcommentboard.php")
                if (private)
                    u = URL("http://10.0.2.2:8081/postcomment.php")
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
                if (!result.contains("ERR")) {
                    mItems.clear()
                    getList(idx, private)
                } else {
                    Toasty.error(it, "오류가 발생했습니다!").show()
                }
            }
        }
    }
}
