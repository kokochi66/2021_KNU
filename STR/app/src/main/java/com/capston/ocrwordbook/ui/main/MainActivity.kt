package com.capston.ocrwordbook.ui.main


import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.databinding.ActivityMainBinding
import com.capston.ocrwordbook.ui.camera.CameraFragment
import com.capston.ocrwordbook.ui.main.MainViewModel.Companion.PICK_IMAGE
import com.capston.ocrwordbook.ui.result.ResultActivity
import com.capston.ocrwordbook.ui.web.WebActivity
import com.capston.ocrwordbook.ui.web.WebViewModel
import com.capston.ocrwordbook.ui.word.WordFragment
import io.socket.client.IO
import io.socket.client.Socket
import java.io.InputStream
import java.net.URISyntaxException

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var mSocket: Socket
    }

    private lateinit var binding: ActivityMainBinding
    var viewModel: MainViewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_NoTitleBar)
        super.onCreate(savedInstanceState)

        try {
            mSocket = IO.socket("http://192.168.0.4:3000") // !!자신의 localhost로 수정
            mSocket.connect()
            Log.d("Connected", "OK")
        } catch (e: URISyntaxException) {
            Log.d("ERR", e.toString())
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        this.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, CameraFragment()).commit()

        binding.mainLinearOcr.setOnClickListener {
            SwitchingOcr()
        }
        binding.mainLinearWords.setOnClickListener {
            SwitchingWords()
        }

        //카메라 화면에서 사진 찍고 저장에 성공하거나 or 갤러리화면에서 사진 선택 성공시 호출 -> 결과화면으로 이동
        MainViewModel.onGetPicture.observe({ lifecycle }) {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }

        //카메라 화면에서 갤러리 버튼을 누르면 호출 -> 갤러리화면으로 이동
        MainViewModel.onClickGalleryButton.observe({ lifecycle }) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PICK_IMAGE)
        }


        //단어장 화면 또는 결과 화면에서 단어를 선택했을 때 호출 -> 웹뷰화면으로 이동
        WebViewModel.onClickWordItem.observe({ lifecycle }) {
            val intent = Intent(this, WebActivity::class.java)
            startActivity(intent)
        }
    }


    fun SwitchingOcr() {
       binding.mainBarWords.visibility = View.INVISIBLE
       binding.mainImageWords.setImageResource(R.drawable.word_list_gray_700)
       binding.mainTextWords.setTextColor(getResources().getColor(R.color.gray_700, null))

       binding.mainBarOcr.visibility = View.VISIBLE
       binding.mainImageOcr.setImageResource(R.drawable.add_photo_white)
       binding.mainTextOcr.setTextColor(resources.getColor(R.color.white_text, null))


        this.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, CameraFragment()).commit()
    }


    fun SwitchingWords() {
        binding.mainBarOcr.visibility = View.INVISIBLE
        binding.mainImageOcr.setImageResource(R.drawable.add_photo_gray_700)
        binding.mainTextOcr.setTextColor(getResources().getColor(R.color.gray_700, null))

        binding.mainBarWords.visibility = View.VISIBLE
        binding.mainImageWords.setImageResource(R.drawable.word_list_white)
        binding.mainTextWords.setTextColor(resources.getColor(R.color.white_text, null))

        this.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, WordFragment()).commit()
    }

    //갤러리 관련 코드
    //갤러리에서 이미지 불러온 후 행동
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("gallery", "onActivityResult 도달1")

        // Check which request we're responding to
        //if (requestCode == MainViewModel.PICK_IMAGE) {
            // Make sure the request was successful
            Log.d("gallery", "onActivityResult 도달2")
            if (resultCode == RESULT_OK) {
                Log.d("gallery", "onActivityResult 도달3")
                try {
                    // 선택한 이미지에서 비트맵 생성
                    val `in`: InputStream? = contentResolver.openInputStream(data?.data!!)
                    val img = BitmapFactory.decodeStream(`in`)
                    `in`?.close()
                    // Result 화면으로 이동하기위한 코드
                    Log.d("gallery", "onActivityResult 도달4")
                    MainViewModel.onGetPicture.postValue(data?.data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        //}
    }
}