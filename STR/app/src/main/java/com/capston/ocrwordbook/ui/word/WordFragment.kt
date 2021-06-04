package com.capston.ocrwordbook.ui.word

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.BaseFragment
import com.capston.ocrwordbook.databinding.FragmentWordBinding
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerItem
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerAdapter
import com.capston.ocrwordbook.utils.WordSet
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONException

class WordFragment : BaseFragment<FragmentWordBinding, WordViewModel>(R.layout.fragment_word), WordFragmentView {

    private var recyclerWordList = ArrayList<WordRecyclerItem>()
    private lateinit var recyclerWordAdapter  : WordRecyclerAdapter

    override var viewModel: WordViewModel = WordViewModel()
    override fun setViewModel() {
        binding.viewModel = viewModel
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)


        //저장된 단어 복원하는 코드
        var list = getStringArrayPref_item(context, "word_list")
        if(list != null) {
           for(wordSet in list) {
               recyclerWordList.add(WordRecyclerItem(wordSet!!.recognizedWord, wordSet!!.meaning, true))
           }
        }

        recyclerWordList.add(WordRecyclerItem("car","사과", true ))
        recyclerWordList.add(WordRecyclerItem("banana","사과", true ))
        recyclerWordList.add(WordRecyclerItem("apple","사과", true ))
        recyclerWordList.add(WordRecyclerItem("zebra","사과", false ))
        recyclerWordList.add(WordRecyclerItem("dart","사과", false ))
        recyclerWordList.add(WordRecyclerItem("tax","사과", false ))
        recyclerWordList.add(WordRecyclerItem("apple","사과", false ))


        recyclerWordList.sortWith(compareBy( {it.favorite}, {it.word} ))
        recyclerWordAdapter = WordRecyclerAdapter(context, recyclerWordList, this)
        binding.wordRecyclerView.apply {
            adapter = recyclerWordAdapter
            layoutManager = GridLayoutManager(context, 1)
        }

        binding.wordSearching.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do Nothing
            }
            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                recyclerWordAdapter?.filter?.filter(charSequence)
            }
            override fun afterTextChanged(charSequence: Editable?) {
                //Do Nothing
            }

        })



            return binding.root
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

    override fun onClickDialogYes(position: Int) {
        recyclerWordAdapter.itemList.removeAt(position)

        val temp = ArrayList<WordSet?>()
        for(word in recyclerWordList){
            temp.add(WordSet(word.word, word.meaning))
        }
        setStringArrayPref(context, "word_list", temp)
        recyclerWordAdapter.notifyDataSetChanged()
    }

}