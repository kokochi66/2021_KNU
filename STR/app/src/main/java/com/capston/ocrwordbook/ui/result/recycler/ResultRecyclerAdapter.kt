package com.capston.ocrwordbook.ui.result.recycler

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.web.WebActivity
import com.capston.ocrwordbook.ui.web.WebViewModel
import com.capston.ocrwordbook.utils.WordSet
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONException


class ResultRecyclerAdapter(private val context: Context?, val itemList: ArrayList<ResultRecyclerItem>)
    : RecyclerView.Adapter<ResultRecyclerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultRecyclerHolder {
        return ResultRecyclerHolder(context!!,
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_result, parent, false))

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ResultRecyclerHolder, position: Int) {
        holder.bindView(itemList[position])

        val container = holder.itemView.findViewById<ConstraintLayout>(R.id.recycler_item_result_container)

        container.setOnClickListener {

            WebViewModel.onClickWord.value = itemList[position].recognizedText
            val intent = Intent(context, WebActivity::class.java)
            context?.startActivity(intent)

        }

        container.setOnLongClickListener {
            var list : ArrayList<WordSet?>? = getStringArrayPref_item(context, "word_list")
            if(list == null) {
                list = ArrayList<WordSet?>()
            }
            list.add(WordSet(itemList[position].recognizedText, itemList[position].meaningText))
            setStringArrayPref(context, "word_list", list)
            true
        }

    }

    fun setStringArrayPref(context: Context?, key: String?, values: ArrayList<WordSet?>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        val a = JSONArray()
        val gson = GsonBuilder().create()
        for (i in 0 until values.size) {
            val string: String = gson.toJson(values[i], WordSet::class.java)
            a.put(string)
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.apply()
    }

    fun getStringArrayPref_item(context: Context?, key: String?): ArrayList<WordSet?>? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val json = prefs.getString(key, null)
        val OrderDatas: ArrayList<WordSet?> = ArrayList<WordSet?>()
        val gson = GsonBuilder().create()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val orderData: WordSet = gson.fromJson(a[i].toString(), WordSet::class.java)
                    OrderDatas.add(orderData)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return OrderDatas
    }
}