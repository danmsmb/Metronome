package com.example.metronome_app

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var timesig: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timesig = findViewById(R.id.bottom_sheet)
        timesig!!.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)
        val _2_4layout = dialog.findViewById<LinearLayout>(R.id.TS2_4)
        val _3_4layout = dialog.findViewById<LinearLayout>(R.id.TS3_4)
        val _4_4layout = dialog.findViewById<LinearLayout>(R.id.TS4_4)
        val _6_8layout = dialog.findViewById<LinearLayout>(R.id.TS6_8)
        _2_4layout.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "2/4 time signature clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
        _3_4layout.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "3/4 time signature clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
        _4_4layout.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "4/4 time signature clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
        _6_8layout.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "6/8 time signature clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialoAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }
}
