package ee.ut.gimmefood

import androidx.lifecycle.ViewModel

class MenuViewModel : ViewModel() {

    companion object {
        private val TAG = MenuViewModel::class.java.name
    }

    var orderQuantities: MutableMap<String, Int> = mutableMapOf()
}