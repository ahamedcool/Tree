package com.hackathon2018.troublelistener.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import es.dmoral.toasty.Toasty
import android.net.ConnectivityManager
import com.hackathon2018.troublelistener.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity(), View.OnClickListener{


    private var mContext: Context? = null

    private var mPreference: SharedPreferences? = null
    private var mPreferenceEditor: SharedPreferences.Editor? = null

    var mId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }
    private fun init(){
        Toasty.Config.getInstance().apply() // Toasty init

        sign.setOnClickListener(this)
        sign_up.setOnClickListener(this)

        mContext = this
        mPreference = getSharedPreferences("user", Activity.MODE_PRIVATE)
        mPreferenceEditor = mPreference!!.edit()


    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.sign -> {
                mId = id.text.toString()
                val mPw = pw.text.toString()
                when(checkNetwork()) {
                    true -> {
                        when {
                            mId == "" -> Toasty.warning(this, "Please input your ID").show()
                            mPw == "" -> Toasty.warning(this, "Please input your password").show()
                            else -> loginAccount(mId!!, mPw)
                        }
                    }
                    false -> {
                        Toasty.warning(this, "Please check your network.").show()
                    }
                }
            }
            R.id.sign_up -> startActivity<RegisterActivity>()
        }
    }

    private fun loginAccount(id: String, password: String){
        var result: String = "ERR"
        doAsync {
            try {
                val body = "id=$id&pw=$password" //parameter
                val u = URL("http://10.0.2.2:8081/login.php")
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
                    val nick: String = result.replace("SUC:", "")
                    mPreferenceEditor!!.putString("id", mId)
                    mPreferenceEditor!!.apply()
                    Toasty.success(it, "Hello, $nick").show()
                    startActivity<MainActivity>()

                } else if (result.contains("ERR")) {
                    Toasty.error(it, "Error occur").show()
                }
            }
        }
    }


    private fun checkNetwork() : Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            return when {
                activeNetwork.type == ConnectivityManager.TYPE_WIFI -> true
                activeNetwork.type == ConnectivityManager.TYPE_MOBILE -> true
                else -> false
            }
        }
        return false
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()

    }
}