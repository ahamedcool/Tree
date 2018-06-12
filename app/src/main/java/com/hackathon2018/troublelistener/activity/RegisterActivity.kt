package com.hackathon2018.troublelistener.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import es.dmoral.toasty.Toasty
import com.hackathon2018.troublelistener.R
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.doAsync
import java.util.regex.Pattern
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class RegisterActivity : AppCompatActivity(), View.OnClickListener {


    var mContext: Context? = null

    private var mPreference: SharedPreferences? = null
    private var mPreferenceEditor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
    }
    private fun init(){
        Toasty.Config.getInstance().apply()
        sign.setOnClickListener(this)
        mContext = this

        mPreference = getSharedPreferences("user", Activity.MODE_PRIVATE)
        mPreferenceEditor = mPreference!!.edit()

    }
    override fun onClick(v: View?) {
        val mPw = pw.text.toString()
        val mPwConfirm = pw2.text.toString()
        val mId = id.text.toString()
        val mName = name.text.toString()
        when(checkNetwork()) {
            true -> {
                when {
                    mId == "" -> Toasty.warning(this, "Please input your email").show()
                    mPw == "" -> Toasty.warning(this, "Please input your password").show()
                    mPwConfirm == "" -> Toasty.warning(this, "Please confirm your password").show()
                    mName == "" -> Toasty.warning(this, "Please confirm your name").show()
                    mPw != mPwConfirm -> Toasty.warning(this, "Please verify your re-entered password").show()
                    else -> createAccount(mId, mPw, mName)
                }
            }
            false -> {
                Toasty.warning(this, "Please check your network.").show()
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

    private fun isValidPassword(target: String): Boolean {
        val p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0-9])(?=.*[a-zA-Z]).*$)")

        val m = p.matcher(target)
        return m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*".toRegex())
    }



    private fun createAccount(id: String, password: String, name: String) {

        if (!isValidPassword(password)) {
            Toasty.error(this,"Password is not valid").show()
            return
        }
        var result: String = "ERR"
        doAsync {
            try {
                val body = "id=$id&pw=$password&name=$name" //parameter
                val u = URL("http://10.0.2.2:8081/register.php")
                val huc = u.openConnection() as HttpURLConnection
                huc.readTimeout = 4000
                huc.connectTimeout = 4000
                huc.requestMethod = "POST"
                huc.doInput = true
                huc.doOutput = true
                huc.setRequestProperty("euc-kr", "application/x-www-form-urlencoded")
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
                    Toasty.success(it, "Sign up successfully").show()
                    startActivity<LoginActivity>()

                } else if (result.contains("ERR")) {
                    Toasty.error(it, "Error occur").show()
                }
            }
        }


    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }
}
