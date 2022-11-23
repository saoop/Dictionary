package com.example.wordybook.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.SqliteOpenHelper
import com.example.wordybook.adapters.WordCollectionAdapter
import com.example.wordybook.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val dbHandler = SqliteOpenHelper(this, null)
    lateinit var mToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fabCollection.setOnClickListener{
            setUpCollectionNameDialog()
        }

        setSupportActionBar(binding.tbActivityMain)
        supportActionBar?.setTitle("Dictionary")
        mToggle = ActionBarDrawerToggle(this, binding.  dlMainActivity, R.string.open, R.string.close)

        binding.dlMainActivity.addDrawerListener(mToggle)
        mToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*binding.nvMainActivity.setNavigationItemSelectedListener {
            Log.i("MainActivity", "Option selected...")
            when(it.itemId) {
                R.id.nav_settings -> {
                    Toast.makeText(this@MainActivity, "Working...", Toast.LENGTH_SHORT).show()
                }

            }
            true
        }*/

        binding.nvMainActivity.setOnClickListener{
            Toast.makeText(this@MainActivity, "Working...", Toast.LENGTH_SHORT).show()

        }


        binding.nvMainActivity.setNavigationItemSelectedListener(object: NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {

                when(item.itemId){
                    R.id.nav_dark_mode -> {
                        Toast.makeText(this@MainActivity, "Soon will be implemented...", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_settings -> {
                        Toast.makeText(this@MainActivity, "Soon will be implemented...", Toast.LENGTH_SHORT).show()

                    }
                    R.id.nav_about_the_app ->{
                        val intent = Intent(this@MainActivity, AboutTheAppActivity::class.java)
                        startActivity(intent)
                    }
                }

                binding.dlMainActivity.closeDrawer(binding.nvMainActivity)
                return true
            }
        })


        setUpRecycleViewCollections()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(mToggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setUpCollectionNameDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_collection_create)
        val btnSubmit  = dialog.findViewById<Button>(R.id.btnCollectionNameSubmit)
        val etName = dialog.findViewById<EditText>(R.id.etCollectionName)
        btnSubmit.setOnClickListener{
            if (etName.text.isNotEmpty()){
                Toast.makeText(this, etName.text.toString(), Toast.LENGTH_SHORT).show()
                addNewCollection(etName.text.toString(), Constants.getCurrentDate())
                createNewTableWords(etName.text.toString())
                setUpRecycleViewCollections()
            }


            dialog.dismiss()
        }

        dialog.show()

    }


    private fun addNewCollection(name: String, date: String){
       val dbHandler = SqliteOpenHelper(this, null)
        dbHandler.addValue(name, date)

    }

    private fun setUpRecycleViewCollections(){

        binding.rvCollections.layoutManager =LinearLayoutManager(this)


        val list = dbHandler.getAllCollections()

        val adapter=  WordCollectionAdapter(this,list )
        binding.rvCollections.adapter = adapter

    }

    private fun createNewTableWords(tableName: String){

        dbHandler.createNewTableWords(tableName.lowercase().replace("\\s".toRegex(), ""))
    }

    fun deleteCollection(tableName: String){
        dbHandler.deleteCollection(tableName)
        setUpRecycleViewCollections()
    }

}