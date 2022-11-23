package com.example.wordybook.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordybook.*
import com.example.wordybook.adapters.WordsAdapter
import com.example.wordybook.databinding.ActivityCollectionBinding
import com.example.wordybook.models.ItemWord
import com.example.wordybook.utils.SwipeToDeleteCallback
import com.example.wordybook.utils.SwipeToEditCallback
import com.google.android.material.snackbar.Snackbar
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList

class CollectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityCollectionBinding
    lateinit var tableName: String //the name of the table in Database corresponding to this collection
    var listOfWords: ArrayList<ItemWord>? = null

    val dbHandler = SqliteOpenHelper(this, null)

    companion object {
        const val CREATE_WORD_REQUEST_KEY = 1
        const val EDIT_WORD_REQUEST_KEY = 2
        const val TRANSLATION_EXTRA = "translation"
        const val WORD_EXTRA = "word"
        const val REQUEST_CODE = "request_code"
        const val ADAPTER_POSITION_EXTRA = "ADAPTER_POSITION_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCollectionBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        tableName = intent.getStringExtra(Constants.TABLE_NAME).toString()

        binding.tbCollectionToolbar.setTitle(tableName)
        setSupportActionBar(binding.tbCollectionToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbCollectionToolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        Log.i("CollectionActivity", "setting Up CollectionActivity, tableName: " + tableName)

        //tableName = tableName.lowercase().replace("\\s".toRegex(), "")

        listOfWords = Constants.getWords(this, tableName)

        setUpRecyclerViewWords()



        binding.llAddWord.setOnClickListener {
            val intent = Intent(this@CollectionActivity, CreateNewWordActivity::class.java)
            intent.putExtra(REQUEST_CODE, CREATE_WORD_REQUEST_KEY)
            intent.putExtra(Constants.TABLE_NAME, tableName)
            startActivityForResult(intent, CREATE_WORD_REQUEST_KEY)

            //setUpWordDialog()
        }

        binding.llStudy.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)
            intent.putExtra(Constants.TABLE_NAME, tableName)

            startActivity(intent)
            finish()
        }

        binding.llStatistic.setOnClickListener {
            val intent = Intent(this, StatisticActivity::class.java)
            intent.putExtra(Constants.TABLE_NAME, tableName)
            startActivity(intent)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CREATE_WORD_REQUEST_KEY -> {
                    val word = data!!.getStringExtra(WORD_EXTRA)
                    val translation = data.getStringExtra(TRANSLATION_EXTRA)
                    dbHandler.addWord(
                        tableName, word.toString(), translation.toString(),
                        Constants.getCurrentDate()
                    ) }
                EDIT_WORD_REQUEST_KEY -> {
                    Log.i("Editing", "EDIT_WORD_REQUEST_KEY")
                    val word = data!!.getStringExtra(WORD_EXTRA)
                    val translation = data.getStringExtra(TRANSLATION_EXTRA)
                    val id = data.getIntExtra(ADAPTER_POSITION_EXTRA, 0)
                    val adapter = binding.rvWords.adapter as WordsAdapter
                    adapter.editWord(id, word!!, translation!!)
                    listOfWords = Constants.getWords(this@CollectionActivity, tableName)
                    Log.i("Editing", "EDIT_WORD_REQUEST_KEY end")
                }

            }
        }
        setUpRecyclerViewWords()
    }

    private fun setUpWordDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_new_word)
        val btbSubmit = dialog.findViewById<Button>(R.id.btnNewWordSubmit)
        val etWord = dialog.findViewById<EditText>(R.id.etWord)
        val etTranslation = dialog.findViewById<EditText>(R.id.etTranslation)


        btbSubmit.setOnClickListener {
            dbHandler.addWord(
                tableName, etWord.text.toString(), etTranslation.text.toString(),
                Constants.getCurrentDate()
            )
            setUpRecyclerViewWords()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setUpSwipeHandlers() {
        val deleteSwipeHandler = object : SwipeToDeleteCallback(this@CollectionActivity) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvWords.adapter as WordsAdapter
                val word = adapter.items[viewHolder.adapterPosition]
                val pos = viewHolder.adapterPosition

                var isRestoredFlag = false

                adapter.deleteWord(pos)
                val undoSnackBar =
                    Snackbar.make(binding.root, "Word deleted...", Snackbar.LENGTH_SHORT)
                undoSnackBar.setAction("Undo") { _ ->
                    isRestoredFlag = true
                    adapter.addWord(word, pos)
                    adapter.notifyItemInserted(pos)
                }
                undoSnackBar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (!isRestoredFlag) {
                            Log.i("Deleting", "in onDismissed")
                            adapter.deleteWordFromDatabase(word, pos)
                            listOfWords = Constants.getWords(this@CollectionActivity, tableName)
                        }
                    }
                })
                undoSnackBar.show()
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvWords)

        val editSwipeHandler = object : SwipeToEditCallback(this@CollectionActivity) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvWords.adapter as WordsAdapter
                val intent = Intent(this@CollectionActivity, CreateNewWordActivity::class.java)
                intent.putExtra(WORD_EXTRA, adapter.items[viewHolder.adapterPosition].word)
                intent.putExtra(
                    TRANSLATION_EXTRA,
                    adapter.items[viewHolder.adapterPosition].translation
                )
                intent.putExtra(REQUEST_CODE, EDIT_WORD_REQUEST_KEY)
                intent.putExtra(ADAPTER_POSITION_EXTRA, viewHolder.adapterPosition)
                startActivityForResult(intent, EDIT_WORD_REQUEST_KEY)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvWords)

    }

    private fun setUpRecyclerViewWords() {
        Log.i("CollectionActivity", "setting Up RecyclerView, tableName: " + tableName)

        binding.rvWords.layoutManager = LinearLayoutManager(this)
        val list = dbHandler.getWords(tableName)
        binding.rvWords.adapter = WordsAdapter(this, list, tableName)
        setUpSwipeHandlers()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_words_menu, menu)

        val search = menu?.findItem(R.id.nav_words_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Find a word"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }


        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun filter(text: String?) {
        val filteredList = ArrayList<ItemWord>()
        val adapter = binding.rvWords.adapter as WordsAdapter

        for (item in listOfWords!!) {
            if (item.word.toLowerCase().contains(text!!.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            binding.tvNoDataAvailable.visibility = View.VISIBLE
            binding.rvWords.visibility = View.GONE
        } else {
            binding.tvNoDataAvailable.visibility = View.GONE
            binding.rvWords.visibility = View.VISIBLE
            adapter.filterList(filteredList)
        }
    }

}