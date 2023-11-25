package com.example.biblioteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class Bienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)
        VerBienvenida()
    }

    fun VerBienvenida(){
        object : CountDownTimer(2000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                //Vamos hacia el MainActivity
                val intent = Intent(this@Bienvenida, Eligir_rol::class.java)
                startActivity(intent)
                finishAffinity()
            }

        }.start()
    }

}