package uz.project.a4photos1wordgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import uz.project.a4photos1wordgame.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("TTTT", "TRUE")
                val intent = Intent(this, BackgroundSoundService::class.java)
                startService(intent)
            } else {
                Log.d("TTTT", "FALSE")
                val intent = Intent(this, BackgroundSoundService::class.java)
                stopService(intent)
            }
        }
    }

}