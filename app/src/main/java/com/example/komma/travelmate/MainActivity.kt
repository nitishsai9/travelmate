package com.example.komma.travelmate

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.ListViewCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var nameArray = ArrayList<String>()
    var locationArray = ArrayList<String>()
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId==R.id.add_place)
        {

val intent = Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val database =openOrCreateDatabase("places",Context.MODE_PRIVATE, null)
            val  cursor = database.rawQuery("SELECT * FROM places",null)
            val nameIndex = cursor.getColumnIndex("name")
            val  latitudeIndex = cursor.getColumnIndex("latitude")
            val  longitudeIndex = cursor.getColumnIndex("longitude")
            cursor.moveToFirst()
            nameArray.clear()
            locationArray.clear()

            while (cursor!=null)
            {
                val nameFromDatabase =cursor.getString(nameIndex)
                val latitudeFromDatabase = cursor.getString(latitudeIndex)
                val  longitudeFromDatabase = cursor.getString((longitudeIndex))
                nameArray.add(nameFromDatabase)
                val latitudeCordinates = latitudeFromDatabase.toDouble()
                val longitudeCordinates = longitudeFromDatabase.toDouble()

                val location = LatLng(latitudeCordinates,longitudeCordinates)
             locationArray.add(location.toString())
                cursor.moveToNext()

            }

        } catch (e: Exception){e.printStackTrace()}
        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,nameArray)
       ListView.adapter = arrayAdapter


        }

    }



