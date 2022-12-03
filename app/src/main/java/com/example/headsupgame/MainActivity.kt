package com.example.headsupgame

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Surface
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var llTop: LinearLayout
    private lateinit var llMain: LinearLayout
    private lateinit var llCelebrity: LinearLayout

    private lateinit var tvTime: TextView

    private lateinit var tvName: TextView
    private lateinit var tvTaboo1: TextView
    private lateinit var tvTaboo2: TextView
    private lateinit var tvTaboo3: TextView

    private lateinit var tvMain: TextView
    private lateinit var btStart: Button

    private var gameActive = false
    var celebrities = arrayListOf<CelebritiesItem>()

    private var celeb = 0

    var rotationPortrait = Surface.ROTATION_0
    var rotateLandscapeRight = Surface.ROTATION_180
    var rotateLandscapeLeft = Surface.ROTATION_270


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        llTop = findViewById(R.id.llTop)
        llMain = findViewById(R.id.llMain)
        llCelebrity = findViewById(R.id.llCelebrity)

        tvTime = findViewById(R.id.tvTime)

        tvName = findViewById(R.id.tvName)
        tvTaboo1 = findViewById(R.id.tvTaboo1)
        tvTaboo2 = findViewById(R.id.tvTaboo2)
        tvTaboo3 = findViewById(R.id.tvTaboo3)

        tvMain = findViewById(R.id.tvMain)
        btStart = findViewById(R.id.btStart)
        btStart.setOnClickListener { requestAPI() }


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Get the screen orientation
        val rotation = newConfig.orientation
        Log.d("test", "onConfigurationChanged:$rotation ")


        if(rotation == rotationPortrait || rotation == rotateLandscapeRight || rotation == rotateLandscapeLeft){
            if (gameActive) {
                updateStatus(true)
            } else {
                updateStatus(false)
            }
            tvName.setTextColor(Color.parseColor("#AF0000"))
        }else{
            if(gameActive){
                celeb++
                newCelebrity(celeb)
                updateStatus(false)
            }else{
                updateStatus(false)
            }
        }
    }


    private fun newTimer(){

        if(!gameActive){
            gameActive = true
            tvMain.text = "Please Rotate Device"
            btStart.isVisible = false
            val rotation = windowManager.defaultDisplay.rotation
            if(rotation == rotationPortrait || rotation == rotateLandscapeRight || rotation == rotateLandscapeLeft){
                updateStatus(false)
            }else{
                updateStatus(true)
            }

            // Show 60 second countdown in a text field
            object : CountDownTimer(63000, 1000) {
                override fun onTick(millisUntilFinished: Long) {


                    if(millisUntilFinished>60000) {
                        Toast.makeText( this@MainActivity,"Game will start in 3 seconds..", Toast.LENGTH_LONG).show()
                    }

                    if(millisUntilFinished<=60000){
                        tvTime.text = "Time: ${millisUntilFinished / 1000}"

                        if (millisUntilFinished <= 10000) {
                            tvTime.setTextColor(Color.parseColor("#AF0000"))
                        }
                    }

                }

                override fun onFinish() {
                    gameActive = false
                    tvTime.text = "Time: --"
                    tvMain.text = "Heads Up!"
                    btStart.isVisible = true
                    updateStatus(false)
                }
            }.start()

        }
    }


    private fun newCelebrity(id: Int){
        if(id < celebrities.size){
            tvName.text = celebrities[id].name
            tvTaboo1.text = celebrities[id].taboo1
            tvTaboo2.text = celebrities[id].taboo2
            tvTaboo3.text = celebrities[id].taboo3
        }
    }


    //Request data from API
    private fun requestAPI(){

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        apiInterface?.getCelebritiesData()
            ?.enqueue(object : Callback<Celebrities> {
                @RequiresApi(Build.VERSION_CODES.N)

                //onResponse function
                override fun onResponse(
                    call: Call<Celebrities>,
                    response: Response<Celebrities>
                ) {

                    var data = response.body()!!

                    if(data.isNotEmpty()){

                        celebrities.clear()

                        for(i in 0 until data.size){
                            celebrities.add(data[i])
                        }

                            celebrities.shuffle()
                            newCelebrity(0)
                            newTimer()

                    }else{
                        Toast.makeText(this@MainActivity, "something went wrong!", Toast.LENGTH_LONG).show()
                    }

                }

                //onFailure function
                override fun onFailure(call: Call<Celebrities>, t: Throwable) {
                    Log.d("retrofit", "onFailure: ${t.message.toString()}")
                }

            })
    }


    private fun updateStatus(showCelebrity: Boolean) {
        if (showCelebrity) {
            llCelebrity.isVisible = true
            llMain.isVisible = false
        } else {
            llCelebrity.isVisible = false
            llMain.isVisible = true
        }
    }

}