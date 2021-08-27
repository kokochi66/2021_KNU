import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.AppObject
import com.capston.ocrwordbook.data.Folder
import com.capston.ocrwordbook.data.Word
import com.capston.ocrwordbook.data.WordbookItem
import com.capston.ocrwordbook.databinding.FragmentWordsBinding
import com.capston.ocrwordbook.ui.dialog.FolderListDialog
import com.capston.ocrwordbook.ui.web.WebActivity
import com.capston.ocrwordbook.ui.word.dialog.AddFolderDialog
import com.capston.ocrwordbook.ui.word.dialog.WordListLongClickMenu
import com.capston.ocrwordbook.ui.word.recylcer.WordbookItemRecyclerAdapter
import kotlinx.coroutines.*
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
 *
 * 시나리오 정리
 * Fragment onResume -> currentFolderId 를 parentFolderId 로 가진  List<Folder>, List<Word> 로드
 * Add Folder Button Click -> 폴더이름작성 다이얼로그 -> currentFolderId 를 parentFolderId 로 Folder 저장, 이전폴더로 돌아가는 폴더도 함께 저장 -> 화면 업데이트
 * WordbookItem 중 단어 Click -> 웹뷰로 이동
 * WordbookItem 중 폴더 Click -> folderId(uid) 로 단어장에서 List<Folder>, List<Word> 로드 -> 화면 업데이트
 * WordbookItem 중 단어 Long Click -> 삭제 선택 -> Word 삭제 -> 화면 업데이트
 * WordbookItem 중 폴더 Long Click -> 삭제 선택 -> 해당 FolderId 를 parentFolderId 로 가지는 Word 들 삭제, 해당 Folder 삭제  -> 해당 FolderId 를 parentFolderId 로가지는 Folder 들을 가져오고 비어있지 않다면 재귀  -> 화면 업데이트
 * WordbookItem 중 단어 Long Click -> 이동 선택 -> 현재폴더 기준 FolderListDialog -> 폴더 선택 -> ( Word 의 parentFolderId UPDATE -> 화면 업데이트)
 * WordbookItem 중 폴더 Long Click -> 이동 선택 -> 현재폴더 기준 FolderListDialog -> 폴더 선택 -> ( Folder 의 parentFolderId UPDATE -> 화면 업데이트)
 *
 * 함수 정리
 * 리사이클러뷰 업데이트 함수
 *      getAndShowWordbookItems(),
 *      wordToWordbookItem(words: List<Word>): List<WordbookItem>,
 *      folderToWordbookItem(folders: List<Folder): List<Folder>,
 *      showWordbookItems(words: List<Word>, folders: List<Folder>)
 * 폴더 저장 함수
 *      makeNewFolder(folderName: String)
 * 웹액티비티로 이동 함수
 *      startWebActivity(word: String)
 * 폴더 변환 함수
 *      changeFolderForward(folderId: Long), changeFolderBackward(grandparentFolderId: Long)
 * 단어 삭제 함수
 *      deleteWord(word: Word)
 * 폴더 삭제 함수
 *      tailrec deleteFolder(folder: Folder)
 * 단어 이동 함수
 *      moveWord(uid: Long, destinationFolderId: Long)
 * 폴더 이동 함수
 *      moveWord(folderId: Long, destinationFolderId: Long)
 *
 *
 *
 * 추가기능 정리
 * 폴더 상관없이 전체 word 로 검색
 *
 * 정렬 기준
 * 폴더(String.compareTo()) -> 단어(좋아요 -> String.compareTo())
 *
 *
 * primaryKey 값이 중복되어서 폴더생성에 실패했었다. primaryKey 가 유일해져야한다.

 * TODO 1. 폴더 뒤로가기 Back Button 으로도 가능하게 하기
 * TODO 2. 검색 기능 구현
 * TODO 3. ResultActivity 에서 단어 추가하는 기능 추가
 * TODO 4. 단어테스트 기능 추가
 *
 */
class WordFragment : Fragment(R.layout.fragment_words), CoroutineScope {

    private var binding: FragmentWordsBinding? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var wordbookItemRecyclerAdapter: WordbookItemRecyclerAdapter

    private val wordDao by lazy {
        AppObject.getWordAppDatabase(requireContext()).wordDao()
    }
    private val folderDao by lazy {
        AppObject.getWordAppDatabase(requireContext()).folderDao()
    }

    private var prevFolderId = ROOT_FOLDER_ID
    private var currentFolderId = ROOT_FOLDER_ID

    private suspend fun printDB() {
        withContext(Dispatchers.IO) {
            Log.e("printDB", "prev : $prevFolderId")
            Log.e("printDB", "current : $currentFolderId")
            //val words = wordDao.getAllWord()
            val folders = folderDao.getAllFolder()
            withContext(Dispatchers.Main) {
                Log.e("printDB", " \n 모든 Word ")
                //words.forEach {
                //    Log.e("printDB", "$it")
                //}
                Log.e("printDB", " \n 모든 Folder ")
                folders.forEach {
                    Log.e("printDB", "$it id : ${it.uid}")
                }
            }
        }
    }

    private fun printCurrentPosition() {
        Log.e("printDB", "prev : $prevFolderId")
        Log.e("printDB", "current : $currentFolderId")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentWordsBinding = FragmentWordsBinding.bind(view)
        binding = fragmentWordsBinding
        launch(coroutineContext) {
            printDB()
        }

        initWordbookItemRecyclerView(fragmentWordsBinding)
        initSearchEditText(fragmentWordsBinding)
        initAddFolderButton(fragmentWordsBinding)
        initPreviousButton(fragmentWordsBinding)
        initTestButton(fragmentWordsBinding)

    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            getAndShowChildWordbookItems(currentFolderId)
        }
    }


    private fun initWordbookItemRecyclerView(localBinding: FragmentWordsBinding) {
        wordbookItemRecyclerAdapter = WordbookItemRecyclerAdapter(
            onClickWordbookItem = { wordbookItem ->
                if (wordbookItem.isWord) {
                    startWebActivity(wordbookItem.word)
                } else {
                    changeFolderForward(wordbookItem.uid)

                }
            },
            onLongClickWordbookItem = { wordbookItem ->
                showDeleteOrMoveMenuDialog(wordbookItem)
            }
        )

        localBinding.wordRecyclerView.apply {
            adapter = wordbookItemRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPreviousButton(fragmentWordsBinding: FragmentWordsBinding) {
        fragmentWordsBinding.wordImageButtonPreviousFolder.setOnClickListener {
            if (currentFolderId == ROOT_FOLDER_ID) {
                Toast.makeText(requireContext(), "현재 폴더가 최상단 폴더 입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            launch(coroutineContext) {
                withContext(Dispatchers.IO) {
                    val grandparentFolder: Folder? = folderDao.getFolderByUid(prevFolderId)
                    changeFolderBackward(grandparentFolder?.parentFolderId ?: ROOT_FOLDER_ID)
                }
            }

        }
    }


    private fun changeFolderForward(folderId: Long) {
        launch(coroutineContext) {
            prevFolderId = currentFolderId
            currentFolderId = folderId
            getAndShowChildWordbookItems(currentFolderId)

            printCurrentPosition()

        }
    }

    private suspend fun changeFolderBackward(grandparentFolderId: Long) =
        withContext(Dispatchers.IO) {
            currentFolderId = prevFolderId
            prevFolderId = grandparentFolderId
            getAndShowChildWordbookItems(currentFolderId)

            printCurrentPosition()
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
                launch(coroutineContext) {
                    if (localBinding.wordEditTextSearch.text.isNotEmpty()) {
                        searchWords(localBinding.wordEditTextSearch.text.toString())
                    } else {

                        getAndShowChildWordbookItems(currentFolderId)

                    }
                }
            }

            override fun afterTextChanged(charSequence: Editable?) {}

        })
    }

    private suspend fun searchWords(keyword: String) = withContext(Dispatchers.IO) {
        //val words = wordDao.searchWords(keyword)
        //wordbookItemRecyclerAdapter.submitList(wordToWordbookItem(words)?.sortedWith(AppObject.wordbookItemComparator))
    }

    private fun initAddFolderButton(localBinding: FragmentWordsBinding) {
        localBinding.wordImageButtonAddFolder.setOnClickListener {
            inflateAddFolderDialog()
        }
    }

    private fun inflateAddFolderDialog() {
        AddFolderDialog(requireContext()) { newFolderName ->
            launch(coroutineContext) {
                val newFolder = Folder(currentFolderId, newFolderName)
                makeNewFolder(newFolder)
            }

        }.show()
    }

    private suspend fun makeNewFolder(newFolder: Folder) = withContext(Dispatchers.IO) {
        // 현재 폴더에 폴더 추가
        folderDao.insertFolder(newFolder)
        getAndShowChildWordbookItems(currentFolderId)
    }


    private fun showDeleteOrMoveMenuDialog(wordbookItem: WordbookItem) {
        val isWord = wordbookItem.isWord
        WordListLongClickMenu(
            requireContext(),
            onClickDeleteButton = {
                launch(coroutineContext) {
                    if (isWord) {
                        deleteWord(wordbookItem.uid)
                    } else {
                        deleteFolder(wordbookItem.uid)
                    }
                    getAndShowChildWordbookItems(currentFolderId)
                    printDB()
                }
            },
            onClickMoveFolderButton = {
                launch(coroutineContext) {
                    withContext(Dispatchers.IO) {
                        val folders =
                            folderDao.getChildFolders(currentFolderId) ?: return@withContext
                        withContext(Dispatchers.Main) {
                            showFolderListDialog(wordbookItem, folders)
                        }
                    }
                }


            }
        ).show()
    }

    private suspend fun showFolderListDialog(wordbookItem: WordbookItem, folders: List<Folder>) {
        FolderListDialog(
            requireContext(),
            folders,
            onClickFolder = { folder ->
                launch(coroutineContext) {
                    if (wordbookItem.isWord) {
                        moveWord(wordbookItem.uid, folder.uid)
                    } else {
                        moveFolder(wordbookItem.uid, folder.uid)
                    }
                    getAndShowChildWordbookItems(currentFolderId)
                    printDB()
                }
            }
        ).show()

    }


    private suspend fun deleteWord(uid: Long) = withContext(Dispatchers.IO) {
        wordDao.deleteWordByUid(uid)
    }

    /**
     * @param : 삭제하려고하는 폴더가 인자로 들어온다.
     * 인자를 uid 로 가지는 폴더와 인자를 parentFolderId 로 가지는 Word 를 삭제한다.
     * 인자를 parentFolderId 로 가지는 모든 폴더(children)를 불러오고
     * children 이 있으면 재귀한다.
     */
    private suspend fun deleteFolder(folderId: Long) {
        withContext(Dispatchers.IO) {

            folderDao.deleteFolderByUid(folderId)
            wordDao.deleteChildWordsByUid(folderId)
            val childFolders = folderDao.getChildFolders(folderId) ?: return@withContext

            childFolders?.forEach {
                deleteFolder(it.uid)
            }
        }
    }

    private suspend fun moveWord(uid: Long, destinationFolderId: Long) =
        withContext(Dispatchers.IO) {
            val word = wordDao.getWordByUid(uid)
            word.parentFolderId = destinationFolderId
            wordDao.insertWord(word)
        }

    private suspend fun moveFolder(folderId: Long, destinationFolderId: Long) =
        withContext(Dispatchers.IO) {
            val folder = folderDao.getFolderByUid(folderId)
            folder.parentFolderId = destinationFolderId
            folderDao.insertFolder(folder)
        }


    // todo 단어테스트 기능 추가
    private fun initTestButton(localBinding: FragmentWordsBinding) {
        localBinding.wordImageButtonTest.setOnClickListener {
            launch(coroutineContext) {
                withContext(Dispatchers.IO) {
                    wordDao.insertWord(
                        Word(
                            parentFolderId = currentFolderId,
                            word = "apple",
                            meaning = "사과"
                        )
                    )
                    wordDao.insertWord(
                        Word(
                            parentFolderId = currentFolderId,
                            word = "banana",
                            meaning = "바나나"
                        )
                    )
                    wordDao.insertWord(
                        Word(
                            parentFolderId = currentFolderId,
                            word = "cat",
                            meaning = "고양이"
                        )
                    )
                    wordDao.insertWord(
                        Word(
                            parentFolderId = currentFolderId,
                            word = "shit",
                            meaning = "쉿"
                        )
                    )

                    getAndShowChildWordbookItems(currentFolderId)
                }
            }
        }


    }

    private suspend fun getAndShowChildWordbookItems(parentFolderId: Long) =
        withContext(Dispatchers.IO) {
            val folders = folderDao.getChildFolders(parentFolderId)
            val words = wordDao.getChildWords(parentFolderId)
            showWordbookItems(words, folders)
        }

    private fun wordToWordbookItem(words: List<Word>?): List<WordbookItem>? =
        words?.map {
            WordbookItem(
                uid = it.uid,
                parentFolderId = it.parentFolderId,
                word = it.word,
                meaning = it.meaning
            )
        }


    private fun folderToWordbookItem(folders: List<Folder>?): List<WordbookItem>? =
        folders?.map {
            WordbookItem(
                uid = it.uid,
                parentFolderId = it.parentFolderId,
                word = it.folderName,
                isWord = false
            )
        }


    private suspend fun showWordbookItems(words: List<Word>?, folders: List<Folder>?) {
        withContext(Dispatchers.Main) {
            val wordItems = wordToWordbookItem(words) ?: listOf<WordbookItem>()
            val folderItems = folderToWordbookItem(folders) ?: listOf<WordbookItem>()
            val list = wordItems + folderItems
            wordbookItemRecyclerAdapter.submitList(list.sortedWith(AppObject.wordbookItemComparator))
        }
    }


    private fun startWebActivity(word: String) {
        val intent = Intent(requireContext(), WebActivity::class.java)
        intent.putExtra("word", word)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ROOT_FOLDER_ID = 960529L
    }


}


