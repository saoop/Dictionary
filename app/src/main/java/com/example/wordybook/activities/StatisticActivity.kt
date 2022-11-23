package com.example.wordybook.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.LayoutInflater

import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.SqliteOpenHelper
import com.example.wordybook.databinding.ActivityStatisticBinding
import com.example.wordybook.models.ItemWord
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.sql.Date
import java.sql.SQLTimeoutException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StatisticActivity : AppCompatActivity() {
    lateinit var binding: ActivityStatisticBinding

    lateinit var sevenDaysBarDataSet: BarDataSet
    lateinit var sevenDaysBarData: BarData
    lateinit var sevenDaysBarEntryList: ArrayList<BarEntry>

    lateinit var sevenDaysReviewsBarDataSet: BarDataSet
    lateinit var sevenDaysReviewsBarData: BarData
    lateinit var sevenDaysReviewsBarEntryList: ArrayList<BarEntry>

    lateinit var datesWords: ArrayList<Pair<String, Int>>
    lateinit var datesReview: ArrayList<Pair<String, Int>>
    val dbHandler = SqliteOpenHelper(this@StatisticActivity, null)

    class DateValueFormatter: ValueFormatter(){
        private var days = ArrayList<String>()
        override fun getBarLabel(barEntry: BarEntry?): String {
            return super.getBarLabel(barEntry)
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()) ?: value.toString()
        }

        fun setUpDays(arr: ArrayList<String>){
            this.days = arr
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        val tableName = intent.getStringExtra(Constants.TABLE_NAME).toString()
        datesWords = dbHandler.getLastSevenDaysStats(tableName)
        datesReview = dbHandler.getLastSevenDaysStatsReviews(tableName)

        setSupportActionBar(binding.tbStatisticToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(tableName)

        setUpStatisticTotalWords(tableName)

        binding.tbStatisticToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sevenDaysReviewsBarEntryList = getSevenDaysReviewsArray()
        sevenDaysReviewsBarDataSet = BarDataSet(sevenDaysReviewsBarEntryList, "Bar chart Data")
        sevenDaysReviewsBarData = BarData(sevenDaysReviewsBarDataSet)

        setUpBarchartReviews()

        sevenDaysBarEntryList = getSevenDaysArray()
        sevenDaysBarDataSet = BarDataSet(sevenDaysBarEntryList, "Bar chart Data")
        sevenDaysBarData = BarData(sevenDaysBarDataSet)

        binding.brSevenDaysStats.data = sevenDaysBarData
        sevenDaysBarDataSet.valueTextColor = Color.BLACK
        sevenDaysBarDataSet.setColor(resources.getColor(R.color.purple_500))
        sevenDaysBarDataSet.valueTextSize = 0f
        binding.brSevenDaysStats.description.isEnabled = false
        binding.brSevenDaysStats.setScaleEnabled(false)
        binding.brSevenDaysStats.setTouchEnabled(false)
        binding.brSevenDaysStats.setDrawGridBackground(false)
        //binding.brSevenDaysStats.setGridBackgroundColor(resources.getColor(R.color.white))
        val legend = binding.brSevenDaysStats.legend
        legend.form =  Legend.LegendForm.CIRCLE

        binding.brSevenDaysStats.axisLeft.setDrawGridLines(false)
        binding.brSevenDaysStats.axisRight.setDrawGridLines(false)
        binding.brSevenDaysStats.xAxis.setDrawGridLines(false)

        binding.brSevenDaysStats.xAxis.valueFormatter = DateValueFormatter()
        val valueFormatter = binding.brSevenDaysStats.xAxis.valueFormatter as DateValueFormatter
        valueFormatter.setUpDays(ArrayList(datesWords.map{it.first}.reversed()))
        //valueFormatter.setUpDays(ArrayList(datesWords.filter { it -> it.first }))



    }

    fun setUpBarchartReviews(){
        binding.brSevenDaysStatsReviews.data = sevenDaysReviewsBarData
        sevenDaysReviewsBarDataSet.valueTextColor = Color.BLACK
        sevenDaysReviewsBarDataSet.setColor(resources.getColor(R.color.green_light))
        sevenDaysReviewsBarDataSet.valueTextSize = 0f
        binding.brSevenDaysStatsReviews.description.isEnabled = false
        binding.brSevenDaysStatsReviews.setScaleEnabled(false)
        binding.brSevenDaysStatsReviews.setTouchEnabled(false)
        binding.brSevenDaysStatsReviews.setDrawGridBackground(false)
        //binding.brSevenDaysStats.setGridBackgroundColor(resources.getColor(R.color.white))
        val legend = binding.brSevenDaysStatsReviews.legend
        legend.form =  Legend.LegendForm.CIRCLE

        binding.brSevenDaysStatsReviews.axisLeft.setDrawGridLines(false)
        binding.brSevenDaysStatsReviews.axisRight.setDrawGridLines(false)
        binding.brSevenDaysStatsReviews.xAxis.setDrawGridLines(false)

        binding.brSevenDaysStatsReviews.xAxis.valueFormatter = DateValueFormatter()
        val valueFormatter = binding.brSevenDaysStatsReviews.xAxis.valueFormatter as DateValueFormatter
        valueFormatter.setUpDays(ArrayList(datesReview.map{it.first}.reversed()))
    }

    private fun setUpStatisticTotalWords(tableName: String) {
        val numberWords = dbHandler.getNumberOfRowsInTable(tableName)
        binding.tvTotalNumberWords.text = "Total number of Words: $numberWords"


    }

    private fun getSevenDaysArray(): ArrayList<BarEntry>{
        val temp = ArrayList<BarEntry>()
        val arrNumbers = datesWords.map{it.second}.reversed()
        for (i in 0..6){
            temp.add(BarEntry(i.toFloat(), arrNumbers[i].toFloat()))
        }
        return temp
    }

    private fun getSevenDaysReviewsArray(): ArrayList<BarEntry>{
        val temp = ArrayList<BarEntry>()
        val arrNumbers = datesReview.map{it.second}.reversed()
        for (i in 0..6){
            temp.add(BarEntry(i.toFloat(), arrNumbers[i].toFloat()))
        }
        return temp
    }

}