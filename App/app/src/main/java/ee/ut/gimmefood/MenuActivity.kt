package ee.ut.gimmefood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ee.ut.gimmefood.data.Datastore
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    private lateinit var unsubscribeFromDatastore: () -> Unit
    val REQUEST_ORDER = 200
    lateinit var database: MockDatabase
    var orderQuantities: MutableMap<Food, Int> = mutableMapOf()
    lateinit var shoppingAdapter: ShoppingAdapter
    val datastore = Datastore.getInstance()
    val restaurantId: String = "0";
    val tableNum: Int = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MockDatabase(resources)
        setContentView(R.layout.activity_menu)

        shoppingAdapter = ShoppingAdapter(this, database.getFoodList(),
            orderQuantities,
            { food -> changeFoodQuantity(food, 1); },
            { food -> changeFoodQuantity(food, -1); })

        menu_recyclerview.adapter = shoppingAdapter

        button_place_order.setOnClickListener {
            val orderFoodIdsList = mutableListOf<String>()
            val orderQuantitiesList = mutableListOf<Int>()
            for ((key, value) in orderQuantities) {
                orderFoodIdsList.add(key.id)
                orderQuantitiesList.add(value)
            }

            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("ids", orderFoodIdsList.toTypedArray())
            intent.putExtra("quantities", orderQuantitiesList.toIntArray())
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("tableNum", tableNum)
            startActivityForResult(intent, REQUEST_ORDER)
        }

        notifyDataSetChanged()

        unsubscribeFromDatastore = datastore.subscribeToRestaraunt(restaurantId, this::onDataChange)
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeFromDatastore();
    }

    private fun notifyDataSetChanged() {
        shoppingAdapter.notifyDataSetChanged()
        updateTotalText()
        button_place_order.isEnabled = orderQuantities.isNotEmpty()
    }

    private fun changeFoodQuantity(food: Food, changeDelta: Int) {
        val newQuantity = orderQuantities.getOrElse(food, { 0 }) + changeDelta
        if (newQuantity <= 0) {
            orderQuantities.remove(food)
        } else {
            orderQuantities.put(food, newQuantity)
        }
        notifyDataSetChanged()
    }

    private fun updateTotalText() {
        var sum = 0f
        for ((food, quantity) in orderQuantities)
            sum += food.price * quantity
        total_textview.text = getString(R.string.total_text, sum)
    }

    private fun clearQuantities() {
        orderQuantities.clear()
        notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ORDER) {
            if (resultCode == RESULT_OK) {
                clearQuantities()
                Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun onDataChange(restaurant: Datastore.Restaurant) {
        shoppingAdapter.foodList = restaurant.menu;
        notifyDataSetChanged();
    }
}