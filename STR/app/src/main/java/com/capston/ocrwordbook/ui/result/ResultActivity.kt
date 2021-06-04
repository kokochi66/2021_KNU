package com.capston.ocrwordbook.ui.result

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.databinding.ActivityResultBinding
import com.capston.ocrwordbook.ui.main.MainActivity
import com.capston.ocrwordbook.ui.main.MainViewModel
import com.capston.ocrwordbook.ui.result.dialog.DescriptionDialog
import com.capston.ocrwordbook.ui.result.recycler.ResultRecyclerAdapter
import com.capston.ocrwordbook.ui.result.recycler.ResultRecyclerItem
import com.capston.ocrwordbook.utils.LoadingDialog
import com.capston.ocrwordbook.utils.WordSet
import com.google.gson.GsonBuilder
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class ResultActivity() : AppCompatActivity(), ResultActivityView {

    lateinit var mLoadingDialog: LoadingDialog
    lateinit var mDescDialog: DescriptionDialog


    var getImg: MutableLiveData<String> = MutableLiveData()
    var getOCR: MutableLiveData<String> = MutableLiveData()

    private lateinit var binding: ActivityResultBinding
    var viewModel: ResultViewModel = ResultViewModel()
    var soc = MainActivity.mSocket // 소켓


    private var resultRecyclerList = ArrayList<ResultRecyclerItem>()
    private lateinit var resultRecyclerAdapter : ResultRecyclerAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_NoTitleBar)
        super.onCreate(savedInstanceState)

        soc.on("image", onCraftImg) // image가 넘어올 경우 : oncraftImg
        soc.on("ocr", onOCRRes) // OCR 결과가 넘어올 경우 : onOCRRes

        binding = DataBindingUtil.setContentView(this , R.layout.activity_result)
        binding.viewModel = viewModel

        // 갤러리 or 사진에서 넘어 왔을 경우 URI로 이미지뷰에 출력
        binding.resultImage.setImageURI(MainViewModel.onGetPicture.value)

        var picData = MainViewModel.onGetPicture.value
        var imgPath = ""

        // 넘어온 사진을 base64 인코딩해서 서버로 전송
        if (picData.toString().startsWith("file"))
            // 카메라로 찍은 사진 : Path가 그대로 넘어옴
            imgPath = picData.toString().substring(6)
        else
            // 갤러리에서 고른 사진 : URI에서 Path로
            imgPath = convertMediaUriToPath(MainViewModel.onGetPicture.value)

        // 사진을 base64 인코딩하여 서버로 전송
        var imgEnc = encoder(imgPath)

        showLoadingDialog(this)
        MainActivity.mSocket.emit("image", imgEnc)

        // 서버에서 이미지가 넘어왔을 때 : 비트맵으로 디코딩 후 이미지뷰에 출력
        getImg.observe(this, Observer {
            binding.resultImage.setImageBitmap(decoder(it))
        })

        var jsonString=""

        // 서버에서 OCR 결과가 넘어왔을 때 : json 파싱
        getOCR.observe(this, Observer {
            jsonString=it.trimIndent()

            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("total")

            for (i in 0..jsonArray.length() - 1) {
                val iObject = jsonArray.getJSONObject(i)
                val cropped = decoder(iObject.getString("base64"))
                val word = iObject.getString("word")
                val mean = iObject.getString("trans")

                resultRecyclerList.add(ResultRecyclerItem(cropped, word, mean))
            }

            resultRecyclerAdapter.notifyDataSetChanged()

            dismissLoadingDialog()
            showDescDialog(this)
        })

        resultRecyclerAdapter = ResultRecyclerAdapter(this, resultRecyclerList, this)
        //리사이클러뷰 리스트에 데이터 넣는 과점 필요
        binding.resultRecyclerView.apply {
            adapter = resultRecyclerAdapter
            layoutManager = GridLayoutManager(context, 1)
        }


    }

    // 클라이언트에서 'image' 이벤트로 데이터가 넘어왔을 때
    val onCraftImg = Emitter.Listener { args ->
        getImg.postValue(args[0].toString())
    }

    // 클라이언트에서 'ocr' 이벤트로 데이터가 넘어왔을 때
    val onOCRRes = Emitter.Listener { args ->
        getOCR.postValue(args[0].toString())
    }

    // 선택된 이미지의 uri를 넘겨주면 해당 이미지 파일의 절대 경로를 리턴하는 함수
    fun convertMediaUriToPath(uri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri!!, proj, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(column_index)
        cursor.close()
        return path
    }

    // 이미지를 base64 인코딩
    @RequiresApi(Build.VERSION_CODES.O)
    fun encoder(filePath: String): String {
        val bytes = File(filePath).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }

    // base64를 비트맵 형식 반환
    @RequiresApi(Build.VERSION_CODES.O)
    fun decoder(base64Str: String): Bitmap {
        val imageByteArray = Base64.getDecoder().decode(base64Str)
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }


    //다이얼로그 정리
    fun showLoadingDialog(context: Context) {
        mLoadingDialog = LoadingDialog(context)
        mLoadingDialog.show()
    }

    fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }

    fun showDescDialog(context: Context) {
        mDescDialog = DescriptionDialog(context)
        mDescDialog.show()
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

    override fun onClickDialogYes() {
        var list : ArrayList<WordSet?>? = getStringArrayPref_item(this, "word_list")
        if(list == null) {
            list = ArrayList<WordSet?>()
        }

        if(!list.contains(WordSet(ResultViewModel.recognizedText.value!!, ResultViewModel.meaningText.value!!))) {
            list.add(WordSet(ResultViewModel.recognizedText.value!!, ResultViewModel.meaningText.value!!))
            setStringArrayPref(this, "word_list", list)
            Toast.makeText(this, ResultViewModel.recognizedText.value!!+" 저장 성공", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "저장실패! 이미 저장된 단어입니다.", Toast.LENGTH_LONG).show()
        }
    }
}