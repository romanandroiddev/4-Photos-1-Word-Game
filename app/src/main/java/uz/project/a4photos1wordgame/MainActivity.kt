package uz.project.a4photos1wordgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import uz.project.a4photos1wordgame.core.Constants
import uz.project.a4photos1wordgame.databinding.ActivityMainBinding


const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
const val COUNTER_TIME = 10L
const val TAG = "TTTT"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var mCountDownTimer: CountDownTimer? = null
    private var mGameOver = false
    private var mGamePaused = false
    private var mIsLoading = false
    private var mRewardedAd: RewardedAd? = null
    private var mTimeRemaining: Long = 0L

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        MobileAds.initialize(this) {}
        loadRewardedAd()
        startGame()

        binding.firstAddBtn.setOnClickListener { showRewardedVideo() }


        sharedPreferences = getSharedPreferences("SHARE_PREF", Context.MODE_PRIVATE)

        val coins_count = sharedPreferences.getInt("COIN_COUNT", 0)
        val last_que = sharedPreferences.getInt("LAST_QUE", 0)
        val repeated_count = sharedPreferences.getInt("REPEATED_COUNT", 0)
        val lastQueText = last_que + 1 + repeated_count * Constants.getQuestions().size
        binding.lastQue.text = lastQueText.toString()

        binding.coinCount.text = coins_count.toString()
        val questions = Constants.getQuestions()

        binding.imgOne.setImageResource(questions[last_que].images[0])
        binding.imgTwo.setImageResource(questions[last_que].images[1])
        binding.imgThree.setImageResource(questions[last_que].images[2])
        binding.imgFour.setImageResource(questions[last_que].images[3])


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

        binding.settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.playBtn.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("LAST_QUE", last_que)
            intent.putExtra("COIN_COUNT", coins_count)
            intent.putExtra("REPEATED_COUNT", repeated_count)
            startActivity(intent)
        }
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


    override fun onResume() {
        super.onResume()
        sharedPreferences = getSharedPreferences("SHARE_PREF", Context.MODE_PRIVATE)
        val coins_count = sharedPreferences.getInt("COIN_COUNT", 0)
        val last_que = sharedPreferences.getInt("LAST_QUE", 0)
        val repeated_count = sharedPreferences.getInt("REPEATED_COUNT", 0)

        binding.lastQue.text = "${last_que + 1 + repeated_count * Constants.getQuestions().size}"
        binding.coinCount.text = coins_count.toString()
        val massiv = Constants.getQuestions()

        binding.imgOne.setImageResource(massiv[last_que].images[0])
        binding.imgTwo.setImageResource(massiv[last_que].images[1])
        binding.imgThree.setImageResource(massiv[last_que].images[2])
        binding.imgFour.setImageResource(massiv[last_que].images[3])

    }

    private fun loadRewardedAd() {
        if (mRewardedAd == null) {
            mIsLoading = true
            var adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                this, AD_UNIT_ID, adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError?.message)
                        mIsLoading = false
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        mRewardedAd = rewardedAd
                        mIsLoading = false
                    }
                }
            )
        }
    }

    private fun addCoins() {
        val currentCoin = sharedPreferences.getInt("COIN_COUNT", 0)
        addIntToSharePrefs("COIN_COUNT", currentCoin + 20)
    }

    private fun startGame() {
        if (mRewardedAd == null && !mIsLoading) {
            loadRewardedAd()
        }
        createTimer(COUNTER_TIME)
        mGamePaused = false
        mGameOver = false
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private fun createTimer(time: Long) {
        mCountDownTimer?.cancel()

        mCountDownTimer = object : CountDownTimer(time * 1000, 50) {
            override fun onTick(millisUnitFinished: Long) {
                mTimeRemaining = millisUnitFinished / 1000 + 1
            }

            override fun onFinish() {
                addCoins()
                mGameOver = true
                binding.coinCount.text = sharedPreferences.getInt("COIN_COUNT", 0).toString()
                binding.coinCountAdsView.text = sharedPreferences.getInt("COIN_COUNT", 0).toString()
            }
        }

        mCountDownTimer?.start()
    }

    private fun showRewardedVideo() {
        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mRewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mRewardedAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }

            mRewardedAd?.show(
                this,
                OnUserEarnedRewardListener() {
                    fun onUserEarnedReward(rewardItem: RewardItem) {
                        addCoins()
                        Log.d("TAG", "User earned the reward.")
                    }
                }
            )
        }
    }

    private fun addIntToSharePrefs(name: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(name, value)
        editor.apply()
    }
}

