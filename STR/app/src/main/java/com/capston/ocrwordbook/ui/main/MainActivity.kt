package com.capston.ocrwordbook.ui.main


import WordFragment
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.InputStream
import java.net.URISyntaxException
import kotlin.coroutines.CoroutineContext

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

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


        initViews()
        initObservers()

    }

    private fun initViews() {

        this.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, CameraFragment())
            .commit()

        binding.mainImageOcr.setOnClickListener {
            ReplaceToCameraFragment()
        }
        binding.mainImageWords.setOnClickListener {
            ReplaceToWordbookFragment()
        }
    }


    private fun ReplaceToCameraFragment() {
        binding.mainImageWords.setImageResource(R.drawable.word_list_gray_700)
        binding.mainImageOcr.setImageResource(R.drawable.add_photo_white)

        this.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, CameraFragment())
            .commit()
    }


    private fun ReplaceToWordbookFragment() {
        binding.mainImageOcr.setImageResource(R.drawable.add_photo_gray_700)
        binding.mainImageWords.setImageResource(R.drawable.word_list_white)

        this.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, WordFragment())
            .commit()
    }



    private fun initObservers() {
        //카메라 화면에서 사진 찍고 저장에 성공하거나 or 갤러리화면에서 사진 선택 성공시 호출 -> 결과화면으로 이동
        MainViewModel.onGetPicture.observe({ lifecycle }) {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }

        //카메라로 찍은 사진
        MainViewModel.onCropPicture.observe({ lifecycle }) {
            cropImage(it)
        }

        //카메라 화면에서 갤러리 버튼을 누르면 호출 -> 갤러리화면으로 이동
        MainViewModel.onClickGalleryButton.observe({ lifecycle }) {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }


        //단어장 화면 또는 결과 화면에서 단어를 선택했을 때 호출 -> 웹뷰화면으로 이동
        WebViewModel.onClickWordItem.observe({ lifecycle }) {
            val intent = Intent(this, WebActivity::class.java)
            startActivity(intent)
        }

    }

    // 사진 자르기
    private fun cropImage(uri: Uri?){
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    // 이하 ClearApplicationData, deleteDir : 쿠키 삭제 관련
    fun clearApplicationData() {
        val cache: File = cacheDir
        val appDir = File(cache.getParent())
        if (appDir.exists()) {
            val children: Array<String> = appDir.list()
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                    Log.i(
                        "Cache", "Deleted"
                    )
                }
            }
        }
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }


    //갤러리 관련 코드
    //갤러리에서 이미지 불러온 후 행동
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("gallery", "onActivityResult 도달1")

        // 서버 연결 확인
        if (mSocket.connected() == false) {
            Toast.makeText(this, "Server is not connected", Toast.LENGTH_SHORT).show()
        }

        else {
            // 사진 자르기
            data?.data?.let { uri ->
                cropImage(uri)
            }

            val result = CropImage.getActivityResult(data)

            // Check which request we're responding to
            // Make sure the request was successful
            Log.d("gallery", "onActivityResult 도달2")
            if (resultCode == RESULT_OK) {
                Log.d("gallery", "onActivityResult 도달3")
                try {
                    // 선택한 이미지에서 비트맵 생성
                    val `in`: InputStream? = contentResolver.openInputStream(result.uri)
                    `in`?.close()

                    // Result 화면으로 이동하기위한 코드
                    Log.d("gallery", "onActivityResult 도달4")

                    // 자른 결과를 데이터로 Result 액티비티로 이동
                    MainViewModel.onGetPicture.postValue(result.uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 쿠키 데이터 삭제
     * 흔히 쓰는 종료 방법인 강제 종료 (특히 작업 관리자 종료)에서는 적용이 안되고
     * Main 액티비티에서 취소 버튼을 눌러 정상 종료 해야만 삭제되는 듯 합니다.
     * Rusult 액티비티에서 결과 출력하는데 사용만 한 후에 삭제해도 될 듯
     */
    override fun onDestroy() {
        super.onDestroy()
        clearApplicationData()
    }


}