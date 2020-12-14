package ee.ut.gimmefood.data

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Datastore {

    companion object {

        private var instance: Datastore? = null

        fun getInstance(): Datastore {
            if (instance == null) {
                instance = Datastore()
            }
            return instance as Datastore
        }
    }

    data class Restaurant(val id: String, val name: String, val menu: List<Food>)

    data class Item(
        val food: Food,
        val quantity: Int
    )

    data class Order(
        val tableNum: Int,
        val items: List<Item>,
        val totalPrice: Float
    )

    val TAG = this::class.java.name
    lateinit var currentRestaurant: Restaurant

    fun getFoodById(id: String): Food? {
        return currentRestaurant.menu.find {
            it.id == id
        }
    }

    var memoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    fun getBitmapFromCache(key: String): Bitmap? {
        return memoryCache.get(key)
    }

    fun addBitmapToCache(key: String, bitmap: Bitmap) {
        memoryCache.put(key, bitmap)
    }

    fun subscribeToRestaraunt(
        restaurantId: String,
        dataChangeHandler: (Restaurant) -> Unit
    ): () -> Unit {
        val db = FirebaseDatabase.getInstance()
        val restaurantRef = db.getReference("restaurants").child(restaurantId)
        val listener = restaurantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menu: List<Food> = snapshot.child("menu").children.mapNotNull { snapshot ->
                    snapshot.getValue(Food::class.java)?.apply {
                        id = snapshot.key ?: ""
                    }
                }
                Log.i(this::class.java.simpleName, menu.toString())
                val name: String = snapshot.child("name").value.toString()
                val id: String = snapshot.key.toString()
                currentRestaurant = Restaurant(id, name, menu)
                dataChangeHandler(Restaurant(id, name, menu))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "Error occured with Firebase")
            }

        })
        return fun() {
            restaurantRef.removeEventListener(listener)
        }
    }

    fun sendOrder(
        restaurantId: String,
        tableNum: Int,
        orderQuantities: MutableMap<Food, Int>,
        totalPrice: Float
    ) {
        val order = Order(
            tableNum,
            orderQuantities.map {
                Item(
                    Food(it.key.id, it.key.name, null, it.key.price),
                    it.value
                )
            },
            totalPrice
        )
        val db = FirebaseDatabase.getInstance()
        Log.i(TAG, "Sending order to firebase")
        Log.i(TAG, order.toString())
        db.getReference("restaurants").child(restaurantId).child("orders").push().setValue(order)
    }

    fun isRestaurantExists(restaurantId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseDatabase.getInstance()
        return db.getReference("restaurants").child(restaurantId)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}