package com.example.weichenglin_simpleboggle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weichenglin_simpleboggle.databinding.FragmentGameBoardBinding
import com.google.android.material.button.MaterialButton
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.absoluteValue

private const val TAG = "GameBoardFragment"

class GameBoardFragment : Fragment(){

    private var _binding: FragmentGameBoardBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private lateinit var letters: Array<MaterialButton>
    private lateinit var gameBoard: Array<Array<MaterialButton>>
    private var selectedLetters: Array<MaterialButton> = arrayOf()
    private lateinit var selectedWord: TextView
    private lateinit var clearButton: MaterialButton
    private lateinit var submitButton: MaterialButton
    private lateinit var lastButtonSelected: MaterialButton
    private lateinit var words: List<String>
    private var wordsUsed: Array<String> = arrayOf()
    private var currentScore: Int = 0
    private var lettersToUse: Array<String> = generateLetters()

    private val viewModel : GameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBoardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        letters = getButtons()
        gameBoard = getGameBoard()
        selectedWord = binding.selectedWord
        clearButton = binding.clearButton
        submitButton = binding.submitButton

        viewModel.score.value = currentScore

        words = context?.let { getDictionary(it, R.raw.words) }!!


        letters.map { letter ->
            letter.setOnClickListener {
                selectedWord.text = selectedWord.text.toString() + letter.text.toString()
                val disabledColor = this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.button_disabled) }
                val enabledColor = this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.button_background_color)}
                if (disabledColor != null) {
                    letter.setBackgroundColor(disabledColor)
                }
                Log.d(TAG, "surrounding letters: ${getSurroundingLetters(gameBoard, letter).map { button -> button.text.toString() }}")
                Log.d(TAG, "NOT surrounding letters: ${getNonSurroundingLetters(gameBoard, letter).map { button -> button.text.toString() }}")

                val others = getNonSurroundingLetters(gameBoard, letter)
                others.map{button ->
                    button.isEnabled = false
                    if (disabledColor != null) {
                        button.setBackgroundColor(disabledColor)
                    }
                }

                val surrounding = getSurroundingLetters(gameBoard, letter)
                surrounding.map{ button ->
                    button.isEnabled = true
                    if (enabledColor != null) {
                        button.setBackgroundColor(enabledColor)
                    }
                }

                selectedLetters += letter
                lastButtonSelected = letter

                selectedLetters.map {
                    it.isEnabled = false
                    if (disabledColor != null) {
                        it.setBackgroundColor(disabledColor)
                    }
                }

            }
        }

        clearButton.setOnClickListener {
            selectedWord.text = ""
            letters.map { letter ->
                val enabledColor = this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.button_background_color) }
                if (enabledColor != null) {
                    letter.setBackgroundColor(enabledColor)
                }
                letter.isEnabled = true
                selectedLetters = arrayOf()
            }
        }

        submitButton.setOnClickListener {
            val wordScore = getScore(selectedWord.text.toString())

            Log.d(TAG, "score: $wordScore")

            selectedWord.text = ""
            letters.map { letter ->
                val enabledColor = this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.button_background_color) }
                if (enabledColor != null) {
                    letter.setBackgroundColor(enabledColor)
                }
                letter.isEnabled = true
                selectedLetters = arrayOf()
            }

            currentScore += wordScore
            if (currentScore < 0) {
                currentScore = 0
            }
            viewModel.score.value = currentScore
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getButtons(): Array<MaterialButton> {
        val buttonOne = binding.firstButton
        buttonOne.text = lettersToUse[0]
        val buttonTwo = binding.secondButton
        buttonTwo.text = lettersToUse[1]
        val buttonThree = binding.thirdButton
        buttonThree.text = lettersToUse[2]
        val buttonFour = binding.fourthButton
        buttonFour.text = lettersToUse[3]
        val buttonFive = binding.fifthButton
        buttonFive.text = lettersToUse[4]
        val buttonSix = binding.sixthButton
        buttonSix.text = lettersToUse[5]
        val buttonSeven = binding.seventhButton
        buttonSeven.text = lettersToUse[6]
        val buttonEight = binding.eighthButton
        buttonEight.text = lettersToUse[7]
        val buttonNine = binding.ninthButton
        buttonNine.text = lettersToUse[8]
        val buttonTen = binding.tenthButton
        buttonTen.text = lettersToUse[9]
        val buttonEleven = binding.eleventhButton
        buttonEleven.text = lettersToUse[10]
        val buttonTwelve = binding.twelfthButton
        buttonTwelve.text = lettersToUse[11]
        val buttonThirteen = binding.thirteenthButton
        buttonThirteen.text = lettersToUse[12]
        val buttonFourteen = binding.fourteenthButton
        buttonFourteen.text = lettersToUse[13]
        val buttonFifteen = binding.fifteenthButton
        buttonFifteen.text = lettersToUse[14]
        val buttonSixteen = binding.sixteenthButton
        buttonSixteen.text = lettersToUse[15]

        return arrayOf(
            buttonOne,
            buttonTwo,
            buttonThree,
            buttonFour,
            buttonFive,
            buttonSix,
            buttonSeven,
            buttonEight,
            buttonNine,
            buttonTen,
            buttonEleven,
            buttonTwelve,
            buttonThirteen,
            buttonFourteen,
            buttonFifteen,
            buttonSixteen
        )
    }

    private fun getGameBoard(): Array<Array<MaterialButton>> {
        return arrayOf(
            arrayOf(letters[0], letters[1], letters[2], letters[3]),
            arrayOf(letters[4], letters[5], letters[6], letters[7]),
            arrayOf(letters[8], letters[9], letters[10], letters[11]),
            arrayOf(letters[12], letters[13], letters[14], letters[15]),
        )
    }

    private fun getSurroundingLetters(x: Array<Array<MaterialButton>>, element: MaterialButton): List<MaterialButton> {
        val result = mutableListOf<MaterialButton>()

        // Find the row and column index of the element
        var rowIndex = -1
        var colIndex = -1
        for (i in x.indices) {
            for (j in x[i].indices) {
                if (x[i][j] == element) {
                    rowIndex = i
                    colIndex = j
                    break
                }
            }
            if (rowIndex != -1) break
        }

        // Add all surrounding elements to the result list
        for (i in (rowIndex - 1)..(rowIndex + 1)) {
            for (j in (colIndex - 1)..(colIndex + 1)) {
                if (i in x.indices && j in x[i].indices && (i != rowIndex || j != colIndex)) {
                    result.add(x[i][j])
                }
            }
        }

        return result
    }

    private fun getNonSurroundingLetters(x: Array<Array<MaterialButton>>, element: MaterialButton): List<MaterialButton> {
        val result = mutableListOf<MaterialButton>()

        // Find the row and column index of the element
        var rowIndex = -1
        var colIndex = -1
        for (i in x.indices) {
            for (j in x[i].indices) {
                if (x[i][j] == element) {
                    rowIndex = i
                    colIndex = j
                    break
                }
            }
            if (rowIndex != -1) break
        }

        // Add non-adjacent elements to the result list
        for (i in x.indices) {
            for (j in x[i].indices) {
                if ((i - rowIndex).absoluteValue > 1 || (j - colIndex).absoluteValue > 1) {
                    result.add(x[i][j])
                }
            }
        }

        return result
    }

    private fun getDictionary(context: Context, resId: Int): List<String> {
        val inputStream: InputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            stringBuilder.append(line.uppercase()).append('\n')
            line = reader.readLine()
        }
        reader.close()
        return stringBuilder.toString().split('\n')
    }

    private fun getScore(word: String) : Int {
        var score = 0
        var numVowels = 0
        val vowels = "AEIOU"
        val double = "SZPXQ"
        var toDouble = false
        
        // word has already been used
        if (word in wordsUsed) {
            Toast.makeText(this.context, "You have already used this word", Toast.LENGTH_SHORT).show()
            return 0
        }

        // word not in dictionary
        if (word !in words) {
            Toast.makeText(this.context, "That is not a word, -10", Toast.LENGTH_SHORT).show()
            return -10
        }
        
        // word is not at least 4 characters long
        if (word.length < 4) {
            Toast.makeText(this.context, "Words must be at least 4 chars long", Toast.LENGTH_SHORT).show()
            return -10
        }

        for (ch in word) {
            // character is a consonant
            if (ch !in vowels) {
                score += 1
            }

            // character is a vowel
            if (ch in vowels) {
                score += 5
                numVowels += 1
            }

            // need to double at the end
            if (ch in double) {
                toDouble = true
            }
        }
        
        if (toDouble) {
            score *= 2
        }

        if (numVowels < 2) {
            Toast.makeText(this.context, "You must use at least two vowels", Toast.LENGTH_SHORT).show()
            return 0
        }

        Toast.makeText(this.context, "That is correct, +$score", Toast.LENGTH_SHORT).show()
        wordsUsed += word
        return score
    }

    private fun generateLetters() : Array<String> {
        var result = arrayOf<String>()
        val consonants = arrayOf("B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "X", "Y", "Z")
        val vowels = arrayOf("A", "E", "I", "O", "U")
        val mapping = arrayOf(true, true, true, true, false, false, false, false, true, true, true, true, true, false, true, false)

        for (x in mapping) {
            result += if (x) {
                consonants.random()
            } else {
                vowels.random()
            }
        }

        return result
    }
}