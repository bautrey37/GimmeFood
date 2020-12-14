package ee.ut.gimmefood

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ee.ut.gimmefood.camera.kotlin.CameraXLivePreviewActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_scan.setOnClickListener {
            Log.d("Main", "button clicked")
            startActivity(Intent(this, CameraXLivePreviewActivity::class.java))
        }

        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDisplayShowTitleEnabled(false)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.your_item_id -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}