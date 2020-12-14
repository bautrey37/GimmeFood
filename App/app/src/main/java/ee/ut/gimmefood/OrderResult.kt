package ee.ut.gimmefood

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_order_result.*

class OrderResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_result)

        menu_button.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        finish_button.setOnClickListener {
            setResult(MenuActivity.RESULT_FINISH)
            finish()
        }

        intent.getStringExtra("summary")?.let {
            tv_order_summary.text = it
        }
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
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
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}