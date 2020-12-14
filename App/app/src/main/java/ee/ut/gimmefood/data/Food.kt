package ee.ut.gimmefood.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.firebase.database.PropertyName

data class Food(var id: String = "", val name: String = "", var image: Bitmap? = null, val price: Float = 0f, val image_url: String = "", val image_hash: String = "")
