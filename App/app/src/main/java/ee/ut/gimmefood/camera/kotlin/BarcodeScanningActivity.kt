package ee.ut.gimmefood.camera.kotlin

import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeScanningActivity : AppCompatActivity() {

    private fun scanBarcoes(image: InputImage) {
        // specifies the types of barcodes to be read
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()

        val scanner = BarcodeScanning.getClient()
    }
}