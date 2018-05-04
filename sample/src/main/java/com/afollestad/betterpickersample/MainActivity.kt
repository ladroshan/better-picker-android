package com.afollestad.betterpickersample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.btn_show_selected_date
import kotlinx.android.synthetic.main.activity_main.btn_show_selected_time
import kotlinx.android.synthetic.main.include_date_picker.date_picker
import kotlinx.android.synthetic.main.include_time_picker.time_picker
import java.text.DateFormat
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    btn_show_selected_time.setOnClickListener {
      val selectedTime = time_picker.getTime()
          .time
      val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
      Toast.makeText(applicationContext, timeFormat.format(selectedTime), Toast.LENGTH_LONG)
          .show()
    }

    btn_show_selected_date.setOnClickListener {
      val selectedDate = date_picker.getDate()
          .time
      val dateFormat = SimpleDateFormat.getDateInstance()
      Toast.makeText(applicationContext, dateFormat.format(selectedDate), Toast.LENGTH_LONG)
          .show()
    }
  }
}
