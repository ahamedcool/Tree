package com.hackathon2018.troublelistener.activity

import android.app.Activity
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.hackathon2018.troublelistener.R
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_post.*


class PostActivity : AppCompatActivity() {

    private var mPreference: SharedPreferences? = null

    var id :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        mPreference = getSharedPreferences("user", Activity.MODE_PRIVATE)
        id = mPreference!!.getString("id", null)

    }
    fun postComment(title: String, content: String, id: String, private: Boolean){
        var result = "ERR"
        doAsync {
            try {
                var u = URL("http://10.0.2.2:8081/posttroubleboard.php")
                val body = "id=$id&title=$title&content=$content" //parameter
                if (private) {
                    u = URL("http://10.0.2.2:8081/posttrouble.php")
                }
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
                if (result.contains("SUC")) {
                    Toasty.success(it, "누군가에게 고민을 보내었습니다!").show()
                    finish()
                } else {
                    Toasty.error(it, "애러가 발생했습니다").show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)//Menu Resource, Menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                postComment(titles.text.toString(), content.text.toString(), id!!, private_chk.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
