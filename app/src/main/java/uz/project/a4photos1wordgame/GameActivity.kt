package uz.project.a4photos1wordgame

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.*
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.core.view.isVisible
import com.google.android.gms.ads.interstitial.InterstitialAd
import uz.project.a4photos1word.data.Question
import uz.project.a4photos1wordgame.core.Constants
import uz.project.a4photos1wordgame.databinding.ActivityGameBinding
import kotlin.random.Random


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val questions = Constants.getQuestions()
    private lateinit var currentQuestion: Question
    private var index = -1
    private var repeatedCount = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var answersList = mutableListOf<TextView>()
    private var optionList = mutableListOf<TextView>()
    private var userAnswer = mutableListOf<Pair<String, TextView>>()
    private var coins_count = 0
    private var last_que: Int = 0

    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)



        sharedPreferences = getSharedPreferences("SHARE_PREF", MODE_PRIVATE)

        if (questions.isNotEmpty()) {
            last_que = sharedPreferences.getInt("LAST_QUE", 0)
            coins_count = sharedPreferences.getInt("COIN_COUNT", 0)
            repeatedCount = sharedPreferences.getInt("REPEATED_COUNT", 0)
            index = last_que
            currentQuestion = questions[index]
        }
        binding.tvLevelNumber.text = "${currentQuestion.id + repeatedCount * questions.size}"
        binding.coinCount.text = coins_count.toString()
        binding.clearBtn.setOnClickListener {
            removeAllLetters()
        }


        var clicked_img: Int = -1
        binding.ivOne.setOnClickListener {
            clicked_img = 1
            animationScaleUpImageView(1)
        }
        binding.ivTwo.setOnClickListener {
            clicked_img = 2
            animationScaleUpImageView(2)
        }
        binding.ivThree.setOnClickListener {
            clicked_img = 3
            animationScaleUpImageView(3)
        }
        binding.ivFour.setOnClickListener {
            clicked_img = 4
            animationScaleUpImageView(4)
        }

        binding.bigImage.setOnClickListener {
            animationScaleDownImageView(clicked_img)
        }

        binding.addAnim.setOnClickListener {
            alphaAnimationUp(binding.adsView)
            binding.adsView.visibility = View.VISIBLE
        }
        binding.btnCloseAdView.setOnClickListener {
            alphaAnimationDownAdView(binding.adsView)
            Handler().postDelayed({
                binding.adsView.visibility = View.GONE
            }, 300L)
        }




        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            fillAnswersList()
            fillOptionsList()

            tvOption1.setOnClickListener { setLetter(tvOption1) }
            tvOption2.setOnClickListener { setLetter(tvOption2) }
            tvOption3.setOnClickListener { setLetter(tvOption3) }
            tvOption4.setOnClickListener { setLetter(tvOption4) }
            tvOption5.setOnClickListener { setLetter(tvOption5) }
            tvOption6.setOnClickListener { setLetter(tvOption6) }
            tvOption7.setOnClickListener { setLetter(tvOption7) }
            tvOption8.setOnClickListener { setLetter(tvOption8) }
            tvOption9.setOnClickListener { setLetter(tvOption9) }
            tvOption10.setOnClickListener { setLetter(tvOption10) }
            tvOption11.setOnClickListener { setLetter(tvOption11) }
            tvOption12.setOnClickListener { setLetter(tvOption12) }

            tvAnswer1.setOnClickListener { removeLetter(tvAnswer1) }
            tvAnswer2.setOnClickListener { removeLetter(tvAnswer2) }
            tvAnswer3.setOnClickListener { removeLetter(tvAnswer3) }
            tvAnswer4.setOnClickListener { removeLetter(tvAnswer4) }
            tvAnswer5.setOnClickListener { removeLetter(tvAnswer5) }
            tvAnswer6.setOnClickListener { removeLetter(tvAnswer6) }
            tvAnswer7.setOnClickListener { removeLetter(tvAnswer7) }
            tvAnswer8.setOnClickListener { removeLetter(tvAnswer8) }

            setQuestion()
        }

    }


    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        binding.apply {
            tvLevelNumber.text = "${index + 1 + repeatedCount * questions.size}"

            ivOne.setImageResource(currentQuestion.images[0])
            ivTwo.setImageResource(currentQuestion.images[1])
            ivThree.setImageResource(currentQuestion.images[2])
            ivFour.setImageResource(currentQuestion.images[3])

            repeat(8) {
                answersList[it].text = ""
                answersList[it].visibility = View.GONE
            }


            repeat(currentQuestion.answer.length) {
                answersList[it].isVisible = true
            }

            setOptionLetters()
        }
    }

    private fun setOptionLetters() {
        binding.apply {
            val optionLetters = mutableListOf<Char>()
            optionLetters.addAll(currentQuestion.answer.toList())

            repeat(12 - optionLetters.size) {
                optionLetters.add(Random.nextInt(1040, 1072).toChar())
            }
            optionLetters.shuffle()

            tvOption1.text = optionLetters[0].toString()
            tvOption2.text = optionLetters[1].toString()
            tvOption3.text = optionLetters[2].toString()
            tvOption4.text = optionLetters[3].toString()
            tvOption5.text = optionLetters[4].toString()
            tvOption6.text = optionLetters[5].toString()
            tvOption7.text = optionLetters[6].toString()
            tvOption8.text = optionLetters[7].toString()
            tvOption9.text = optionLetters[8].toString()
            tvOption10.text = optionLetters[9].toString()
            tvOption11.text = optionLetters[10].toString()
            tvOption12.text = optionLetters[11].toString()
        }
    }

    private fun fillAnswersList() {
        binding.apply {
            answersList.add(tvAnswer1)
            answersList.add(tvAnswer2)
            answersList.add(tvAnswer3)
            answersList.add(tvAnswer4)
            answersList.add(tvAnswer5)
            answersList.add(tvAnswer6)
            answersList.add(tvAnswer7)
            answersList.add(tvAnswer8)
        }
    }

    private fun fillOptionsList() {
        binding.apply {
            optionList.add(tvOption1)
            optionList.add(tvOption2)
            optionList.add(tvOption3)
            optionList.add(tvOption4)
            optionList.add(tvOption5)
            optionList.add(tvOption6)
            optionList.add(tvOption7)
            optionList.add(tvOption8)
            optionList.add(tvOption9)
            optionList.add(tvOption10)
            optionList.add(tvOption11)
            optionList.add(tvOption12)
        }
    }

    private fun setLetter(textView: TextView) {
        val letter = textView.text.toString()
        if (letter.isNotEmpty() && userAnswer.filter { it.first != "" }.size != currentQuestion.answer.length) {
            val pair = Pair(letter, textView)
            val emptyIndex = userAnswer.indexOf(Pair("", binding.tvAnswer1))
            if (emptyIndex == -1) {
                userAnswer.add(pair)
            } else {
                userAnswer[emptyIndex] = pair
            }
            textView.text = ""
            answersList[userAnswer.indexOf(pair)].text = letter
        }

        if (userAnswer.filter { it.first != "" }.size == currentQuestion.answer.length) {
            var answer = ""
            userAnswer.forEach {
                answer += it.first
            }
            if (answer == currentQuestion.answer) {
                answersList.forEach {
                    it.isClickable = false
                }
                binding.apply {
                    nextScreen.hideOrShow(true)
                    lightAnim.hideOrShow(true)
                    submitBtn.setOnClickListener {
                        it.isClickable = false
                        alphaAnimation(binding.nextScreen)
                    }
                }
            } else {
                vibratePhone(200L)
            }
        }
    }

    private fun removeLetter(textView: TextView) {
        val letter = textView.text.toString()
        if (letter.isNotEmpty()) {
            val index = answersList.indexOf(textView)
            val pair = userAnswer[index]
            textView.text = ""
            pair.second.text = pair.first

            // logic
            userAnswer[index] = Pair("", binding.tvAnswer1)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun submitFunc() {
        binding.nextScreen.hideOrShow(false)
        binding.lightAnim.hideOrShow(false)
        when {
            index < questions.size - 1 -> {
                index++
            }
            else -> {
                index = 0
                repeatedCount++
                addIntToSharePrefs("REPEATED_COUNT", repeatedCount)
            }
        }
        answersList.forEach {
            it.isClickable = true
        }

        coins_count = (coins_count + 4)

        addIntToSharePrefs("LAST_QUE", index)
        addIntToSharePrefs("COIN_COUNT", coins_count)
        currentQuestion = questions[index]
        userAnswer.clear()
        setQuestion()
        fillAnswersList()
        binding.tvLevelNumber.text = "${currentQuestion.id + repeatedCount * questions.size}"
        binding.coinCount.text = coins_count.toString()
    }

    private fun removeAllLetters() {
        answersList.forEach {
            val letter = it.text.toString()
            if (letter.isNotEmpty()) {
                val index = answersList.indexOf(it)
                val pair = userAnswer[index]
                it.text = ""
                pair.second.text = pair.first

                // logic
                userAnswer[index] = Pair("", binding.tvAnswer1)
            }
        }
    }

    fun View.hideOrShow(boolean: Boolean) {
        if (boolean) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }


    fun vibratePhone(duration: Long) {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(duration)
            }
        }
    }

    private fun alphaAnimation(layoutt: View) {
        val animation1 = AlphaAnimation(1.0f, 0f)
        animation1.duration = 1000
        layoutt.startAnimation(animation1)
        submitFunc()

        Handler().postDelayed({
            binding.submitBtn.isClickable = true
        }, 5000)


    }

    private fun alphaAnimationDownAdView(layoutt: View) {
        val animation1 = AlphaAnimation(1.0f, 0f)
        animation1.duration = 1000
        layoutt.startAnimation(animation1)
    }

    private fun alphaAnimationUp(layoutt: View) {
        val animation1 = AlphaAnimation(0.0f, 1f)
        animation1.duration = 400
        layoutt.startAnimation(animation1)


    }

    private fun animationScaleUpImageView(id: Int) {
        when (id) {
            1 -> {
                binding.bigImage.setImageResource(currentQuestion.images[id - 1])
                binding.bigImage.visibility = View.VISIBLE
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_anim_photo1
                    )
                )
            }
            2 -> {
                binding.bigImage.setImageResource(currentQuestion.images[id - 1])
                binding.bigImage.visibility = View.VISIBLE
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_anim_photo2
                    )
                )
            }
            3 -> {
                binding.bigImage.setImageResource(currentQuestion.images[id - 1])
                binding.bigImage.visibility = View.VISIBLE
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_anim_photo3
                    )
                )
            }
            4 -> {
                binding.bigImage.setImageResource(currentQuestion.images[id - 1])
                binding.bigImage.visibility = View.VISIBLE
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_anim_photo4
                    )
                )
            }
        }

    }

    private fun animationScaleDownImageView(id: Int) {
        when (id) {
            1 -> {
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_down_anim_1
                    )
                )
            }
            2 -> {
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_down_anim_2
                    )
                )
            }
            3 -> {
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_down_anim_3
                    )
                )
            }
            4 -> {
                binding.bigImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.scale_down_anim_4
                    )
                )
            }
        }
        Handler().postDelayed({
            binding.bigImage.visibility = View.GONE
        }, 180)
    }


    private fun addIntToSharePrefs(name: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(name, value)
        editor.apply()
    }
}
