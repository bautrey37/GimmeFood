package ee.ut.gimmefood

import android.os.Bundle
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
    }
}