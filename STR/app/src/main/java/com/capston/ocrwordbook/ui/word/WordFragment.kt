import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.AppObject
import com.capston.ocrwordbook.config.AppObject.wordbookItemComparator
import com.capston.ocrwordbook.data.Folder
import com.capston.ocrwordbook.data.WordbookItem
import com.capston.ocrwordbook.databinding.FragmentWordsBinding
import com.capston.ocrwordbook.ui.web.WebActivity
import com.capston.ocrwordbook.ui.word.dialog.AddFolderDialog
import com.capston.ocrwordbook.ui.word.dialog.WordListLongClickMenu
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerAdapter
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import kotlin.coroutines.CoroutineContext


/**
 * 저장한 단어를 리스트형태로 볼 수 있다.
 * 짧게 누르면 웹사전 화면으로 이동한다.
 * 길게 누르면 삭제 또는 폴더를 선택할 수 있게 해준다. ( 폴더 안의 폴더는 우선 계획 없음 )
 *
 * 단어인식결과화면 (ResultActivity) 에서 단어를 길게누르며 저장할 때 DB 에 단어를 저장한다.
 * 단어장화면이 만들어질 때마다 DB 에서 저장된 단어를 불러온다. ( 폴더 포함 )
 *      - 폴더는 word 자리에 폴더명을, meaning 자리에 빈문자열 ""을, favorite 자리에는 > 이미지를 설정하여 상단에 나타나게 한다.
 *      - 그리고 아이템 클릭이벤트에 단어면 WebActivity 로 이동하고, 폴더면 리스트아이템을 바꿔준다.
 *      - 리스트 순서는 위에서 아래로 폴더 -> favorite 단어 -> 단어
 * 단어장 화면에서 길게 누르면 삭제할 수 있게 만든다. ( 폴더 포함 )
 *
 * data class Folder(val folderId: String, val folderName: String, val folderIds: List<String>)
 * 폴더 id 는
 * data class Folder(val belongedFolderId: String ~ wordInfo)
 * 폴더와 워드리스트 테이블을 따로 만든다.
 *
 * 로직 정리
 * 폴더 생성 ( Folder 저장, WordbookItem 저장 -> 화면 업데이트 )
 * 폴더 삭제 ( 해당 FolderId 의 List<WordbookItem> 를 가져오고, 순회하며 폴더가 있으면 재귀, 없으면 belongedFolderId 로 단어장에서 List<WordbookItem> 삭제, Folder 삭제  -> 화면 업데이트)
 * 단어 저장 ( WordbookItem 저장 )
 * 단어 삭제 ( WordbookItem 삭제 -> 화면 업데이트 )
 * 단어 이동 ( WordbookItem 삭제 -> belongedFolderId 만 바꿔서 다시 WordbookItem 저장 || UPDATE -> 화면 업데이트)
 * 폴더 이동 ( WordbookItem 삭제 -> belongedFolderId 만 바꿔서 다시 WordbookItem 저장 || UPDATE -> 화면 업데이트 )
 * 폴더 선택 ( belongedFolderId 로 단어장에서 List<WordbookItem> 로드 -> 화면 업데이트 )
 * 단어 검색
 *
 * 함수 정리
 * - belongedFolderId 로 단어장에서 List<WordbookItem> 로드
 * - belongedFolderId 로 단어장에서 List<WordbookItem> 삭제
 * - WordbookItem 저장
 * - WordbookItem 삭제
 * - word 로 WordbookItem 검색
 * - Folder 저장
 * - Folder 삭제
 *
 * 시나리오 정리
 *   O   Fragment onResume -> belongedFolderId 로 단어장에서 List<WordbookItem> 로드
 *   O   Add Folder Button Click -> 폴더이름작성 다이얼로그 -> Folder 저장, WordbookItem 현재와 이전 폴더 저장 -> 화면 업데이트
 *   O   WordbookItem 중 단어 Click -> 웹뷰로 이동
 *   O   WordbookItem 중 폴더 Click -> belongedFolderId 로 단어장에서 List<WordbookItem> 로드 -> 화면 업데이트
 *   O   WordbookItem 중 단어 Long Click -> WordbookItem 삭제 or 이동 다이얼로그 -> 삭제 선택 -> WordbookItem 삭제 -> 화면 업데이트
 * WordbookItem 중 폴더 Long Click -> WordbookItem 삭제 or 이동 다이얼로그 -> 삭제 선택 -> 해당 FolderId 의 List<WordbookItem> 를 가져오고, 순회하며 폴더가 있으면 재귀, 없으면 belongedFolderId 로 단어장에서 List<WordbookItem> 삭제, Folder 삭제  -> 화면 업데이트
 * WordbookItem Long Click -> WordbookItem 삭제 or 이동 다이얼로그 -> 이동 선택 -> (WordbookItem 삭제 -> belongedFolderId 만 바꿔서 다시 WordbookItem 저장 || UPDATE -> 화면 업데이트)
 *
 * 추가기능 정리
 * 폴더 상관없이 전체 word 로 검색
 *
 * 정렬 기준
 * 폴더 -> 단어 -> 좋아요 -> String.compareTo()
 *
 *
 * primaryKey 값이 중복되어서 폴더생성에 실패했었다. primaryKey 가 유일해져야한다.
 *
 * TODO 프로젝트 전체 MVVM 패턴 제대로 적용
 * TODO 아래로 스크롤 하면 검색툴바가 사라지는 애니메이션 적용
 * TODO 단어와 폴더가 같은 데이터클래스로 처리되는데 데이터클래스 이름이 Word 인 것이 이상하니 다른 이름을 사용할것
 * TODO 폴더 뒤로가기 기능 추가
 * TODO 폴더리스트 다이얼로그 추가
 */
class WordFragment : Fragment(R.layout.fragment_words), CoroutineScope {

    private var binding: FragmentWordsBinding? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var recyclerWordAdapter: WordRecyclerAdapter

    private val wordbookItemDao by lazy {
        AppObject.getWordAppDatabase(requireContext()).wordbookItemDao()
    }
    private val folderDao by lazy {
        AppObject.getWordAppDatabase(requireContext()).folderDao()
    }

    private var currentBelongedFolderId = "Main"

    private suspend fun getAllWordbookItems() = withContext(Dispatchers.IO) {
        val words = wordbookItemDao.getAllWordbookItem()
        Log.e("wordbookItem", "모든 word ${words.joinToString("\n")}")
        Log.d("wordbookItem", "모든 word ${words.toString()}")
        Log.e("wordbookItem", "현재 폴더 : $currentBelongedFolderId")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentWordsBinding = FragmentWordsBinding.bind(view)
        binding = fragmentWordsBinding

        initWordbookItemRecyclerView(fragmentWordsBinding)
        initSearchEditText(fragmentWordsBinding)
        initAddFolderButton(fragmentWordsBinding)

        launch(Dispatchers.IO) {
            wordbookItemDao.insertWordbookItem(
                WordbookItem(
                    uid = currentBelongedFolderId,
                    belongedFolderId = currentBelongedFolderId,
                    word = currentBelongedFolderId,
                    meaning = currentBelongedFolderId,
                    favorite = false,
                    isWord = true,
                    nextFolderId = currentBelongedFolderId
                )
            )

            wordbookItemDao.insertWordbookItem(
                WordbookItem(
                    uid = "asdf",
                    belongedFolderId = currentBelongedFolderId,
                    word = "folderName",
                    meaning = "asdf",
                    favorite = false,
                    isWord = false,
                    nextFolderId = "asdf"
                )
            )

            getAllWordbookItems()

        }


    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            getAndShowAllBelongedWordbookItem(currentBelongedFolderId)
            getAllWordbookItems()
        }
    }

    private fun initWordbookItemRecyclerView(localBinding: FragmentWordsBinding) {
        recyclerWordAdapter = WordRecyclerAdapter(
            onClickWordbookItem = { wordbookItem ->
                if (wordbookItem.isWord) {
                    startWebActivity(wordbookItem.word)
                } else {
                    launch(coroutineContext) {
                        currentBelongedFolderId = wordbookItem.nextFolderId
                        getAndShowAllBelongedWordbookItem(currentBelongedFolderId)

                    }
                }
            },
            onLongClickWordbookItem = { wordbookItem ->
                showDeleteOrMoveMenuDialog(wordbookItem)
            }
        )

        localBinding.wordRecyclerView.apply {
            adapter = recyclerWordAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


    private fun initSearchEditText(localBinding: FragmentWordsBinding) {
        localBinding.wordEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (localBinding.wordEditTextSearch.text.isNotEmpty()) {
                    // TODO 폴더 상관없이 영단어만으로 검색
                } else {
                    launch(coroutineContext) {
                        getAndShowAllBelongedWordbookItem(currentBelongedFolderId)
                    }
                }
            }

            override fun afterTextChanged(charSequence: Editable?) {}

        })
    }

    private fun initAddFolderButton(localBinding: FragmentWordsBinding) {
        localBinding.wordImageButtonAddFolder.setOnClickListener {
            showAddFolderDialog()
        }
    }

    private fun showAddFolderDialog() {
        AddFolderDialog(requireContext()) { producedFolderName ->

            val newFolderId = System.currentTimeMillis().toString()
            launch(Dispatchers.IO) {
                getAllWordbookItems()

                // 현재 폴더에 폴더 추가
                wordbookItemDao.insertWordbookItem(
                    WordbookItem(
                        uid = System.currentTimeMillis().toString()+producedFolderName,
                        belongedFolderId = currentBelongedFolderId,
                        word = producedFolderName,
                        meaning = "",
                        favorite = false,
                        isWord = false,
                        nextFolderId = newFolderId
                    )
                )

                // 이전 폴더로 이동하는 아이템을 미리 추가
                wordbookItemDao.insertWordbookItem(
                    WordbookItem(
                        uid = System.currentTimeMillis().toString()+"...",
                        belongedFolderId = newFolderId,
                        word = "...",
                        meaning = "",
                        favorite = false,
                        isWord = false,
                        nextFolderId = currentBelongedFolderId
                    )
                )

                folderDao.insertFolder(
                    Folder(
                        folderId = newFolderId,
                        folderName = producedFolderName,
                        folderIds = ""
                    )
                )

                //insertFolderIdInFolderIds(currentBelongedFolderId, newFolderId)

                getAllWordbookItems()
                getAndShowAllBelongedWordbookItem(currentBelongedFolderId)

            }

        }.show()
    }

    private suspend fun insertFolderIdInFolderIds(currentFolderId: String, newFolderId: String) {
        val currentFolder = folderDao.getFolder(currentFolderId)
        folderDao.deleteFolder(currentFolder)

        val updatedFolderIds = currentFolder.folderIds + ",$newFolderId"
        currentFolder.folderIds = updatedFolderIds

        folderDao.insertFolder(currentFolder)
    }


    private suspend fun deleteFolderIdInFolderIds(currentFolderId: String, newFolderId: String) {

    }

    private fun showDeleteOrMoveMenuDialog(wordbookItem: WordbookItem) {
        val isWord = wordbookItem.isWord
        WordListLongClickMenu(
            requireContext(),
            onClickDeleteButton = {
                launch(coroutineContext) {
                    if (isWord) {
                        deleteWordAndShowWordbookItems(wordbookItem)
                    } else {
                        deleteFolderAndShowWordbookItems(wordbookItem.nextFolderId)
                    }
                }
            },
            onClickMoveFolderButton = {
                // 폴더 리스트 다이얼로그
            }
        ).show()
    }

    private fun initTestButton(localBinding: FragmentWordsBinding) {
        // TODO 테스트 기능 추가

    }

    private suspend fun getAndShowAllBelongedWordbookItem(belongedFolderId: String) =
        withContext(Dispatchers.IO) {
            val wordbookItems = wordbookItemDao.getAllBelongedWordbookItem(belongedFolderId)
            withContext(Dispatchers.Main) {
                currentBelongedFolderId = belongedFolderId
                showAllWordsToRecyclerView(wordbookItems.sortedWith(wordbookItemComparator))
            }
        }


    private fun showAllWordsToRecyclerView(wordbookItems: List<WordbookItem>) =
        recyclerWordAdapter.submitList(wordbookItems)


    private suspend fun deleteWordAndShowWordbookItems(wordbookItem: WordbookItem) =
        withContext(Dispatchers.IO) {
            wordbookItemDao.deleteWordbookItem(wordbookItem)
            //withContext(Dispatchers.Main) {
                getAndShowAllBelongedWordbookItem(currentBelongedFolderId)
            //}
        }


    // 해당 FolderId 의 Folder 의 folderIds 를 가져오고 비어있지 않다면 재귀, 비어있다면 belongedFolderId 로 단어장에서 List<WordbookItem> 삭제, Folder 삭제  -> 화면 업데이트
    private suspend fun deleteFolderAndShowWordbookItems(folderId: String) =
        withContext(Dispatchers.Main) {
            deleteFolder(folderId)
            getAndShowAllBelongedWordbookItem(currentBelongedFolderId)
        }

    private suspend fun deleteFolder(folderId: String) {
        withContext(Dispatchers.IO) {
            val folder = folderDao.getFolder(folderId)
            folderDao.deleteFolder(folder)

            if (folder.folderIds.isEmpty()) {
                wordbookItemDao.deleteAllBelongedWordbookItem(folderId)
            } else {
                folder.folderIds.stringToList().forEach {
                    deleteFolder(it)
                }
            }
        }
    }

    // WordbookItem 의 belongedFolderId 를 UPDATE, Folder 의 folderIds 수정. 즉 폴더를 잃은 입장과 폴더가 생긴 입장 두개를 처리해줘야함 -> 화면 업데이트
    private suspend fun moveAndShowFolder(wordbookItem: WordbookItem) =
        withContext(Dispatchers.IO) {

        }


    // WordbookItem 의 belongedFolderId 를 UPDATE -> 화면 업데이트
    private suspend fun moveAndShowWord(wordbookItem: WordbookItem, nextFolderId: String) =
        withContext(Dispatchers.IO) {
            wordbookItemDao.updateWordbookItemPosition(
                uid = wordbookItem.uid,
                nextFolderId = nextFolderId
            )
            getAndShowAllBelongedWordbookItem(currentBelongedFolderId)
        }

    private fun String.stringToList(): List<String> = this.split(",")
    private fun List<String>.listToString(): String = this.joinToString(",") { it }


    private fun startWebActivity(word: String) {
        val intent = Intent(requireContext(), WebActivity::class.java)
        intent.putExtra("word", word)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }




}


