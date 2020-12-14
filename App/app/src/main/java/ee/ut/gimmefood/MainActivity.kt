package ee.ut.gimmefood

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    }
}