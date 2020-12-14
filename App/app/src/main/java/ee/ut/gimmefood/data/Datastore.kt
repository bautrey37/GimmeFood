package ee.ut.gimmefood.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KFunction1

class Datastore {

    companion object {

        private var instance: Datastore? = null

        fun getInstance() : Datastore {
            if (instance == null) {
                instance = Datastore()
            }
            return instance as Datastore
        }
    }
    data class Restaurant(val id: String, val name: String, val menu: List<Food>)

    data class Item (
        val food: Food,
        val quantity: Int
    )

    data class Order (
        val tableNum: Int,
        val items: List<Item>,
        val totalPrice: Float
    )
    val TAG = this::class.java.name
    lateinit var currentRestaurant: Restaurant;

    fun getFoodById(id: String): Food? {
        return currentRestaurant.menu.find {
            it.id == id
        }
    }

    fun subscribeToRestaraunt(restaurantId : String, dataChangeHandler: (Restaurant) -> Unit): () -> Unit {
        val db = FirebaseDatabase.getInstance();
        val restaurantRef = db.getReference("restaurants").child(restaurantId);
        val listener = restaurantRef.addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menu: List<Food> = snapshot.child("menu").children.mapNotNull { snapshot ->
                    snapshot.getValue(Food::class.java)?.apply {
                        id = snapshot.key ?: ""
                    }
                }
                Log.i(this::class.java.simpleName, menu.toString())
                val name: String = snapshot.child("name").value.toString();
                val id: String = snapshot.key.toString()
                currentRestaurant = Restaurant(id, name, menu)
                dataChangeHandler(Restaurant(id, name, menu))
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return fun() {
            restaurantRef.removeEventListener(listener)
        }
    }

    fun sendOrder(restaurantId: String, tableNum: Int, orderQuantities: MutableMap<Food, Int>, totalPrice: Float) {
        val order = Order(tableNum, orderQuantities.map { Item(Food(it.key.id, it.key.name, null, it.key.price), it.value) }, totalPrice)
        val db = FirebaseDatabase.getInstance();
        Log.i(TAG, "Sending order to firebase")
        Log.i(TAG, order.toString());
        db.getReference("restaurants").child(restaurantId).child("orders").push().setValue(order);
    }
}