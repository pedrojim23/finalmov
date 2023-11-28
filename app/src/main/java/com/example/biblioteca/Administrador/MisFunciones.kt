package com.example.biblioteca.Administrador

import android.app.Application
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

class MisFunciones : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatoTiempo (tiempo : Long) : String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = tiempo
            return DateFormat.format("dd/MM/yyy",cal).toString()
        }
    }
}