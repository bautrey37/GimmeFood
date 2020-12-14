package ee.ut.gimmefood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ee.ut.gimmefood.data.Datastore
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    companion object {
        val TAG = MenuActivity::class.java.name
        val RESULT_FINISH = 12121
    }

    private lateinit var unsubscribeFromDatastore: () -> Unit
    val REQUEST_ORDER = 200
    lateinit var shoppingAdapter: ShoppingAdapter
    private val datastore = Datastore.getInstance()
    private var restaurantId: String? = null
    private var tableNum: Int = -1

    private val model: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        loadIntentData()

        shoppingAdapter = ShoppingAdapter(this, listOf(),
            model.orderQuantities,
            { food -> changeFoodQuantity(food, 1); },
            { food -> changeFoodQuantity(food, -1); })

        menu_recyclerview.adapter = shoppingAdapter

        button_place_order.setOnClickListener {
            val orderFoodIdsList = mutableListOf<String>()
            val orderQuantitiesList = mutableListOf<Int>()
            for ((id, value) in model.orderQuantities) {
                orderFoodIdsList.add(id)
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

        restaurantId?.let {
            unsubscribeFromDatastore = datastore.subscribeToRestaraunt(it, this::onDataChange)
        }
    }

    private fun loadIntentData() {
        Log.i(TAG, intent.extras?.keySet().toString())
        restaurantId = intent.getStringExtra("restaurantId")
        tableNum = intent.getIntExtra("tableNum", -1)
        Log.i(TAG, "restaurant: $restaurantId, table: $tableNum")
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeFromDatastore()
    }

    private fun notifyDataSetChanged() {
        shoppingAdapter.notifyDataSetChanged()
        updateTotalText()
        button_place_order.isEnabled = model.orderQuantities.isNotEmpty()
    }

    private fun changeFoodQuantity(food: Food, changeDelta: Int) {
        val newQuantity = model.orderQuantities.getOrElse(food.id, { 0 }) + changeDelta
        if (newQuantity <= 0) {
            model.orderQuantities.remove(food.id)
        } else {
            model.orderQuantities[food.id] = newQuantity
        }
        notifyDataSetChanged()
    }

    private fun updateTotalText() {
        var sum = 0f
        for ((id, quantity) in model.orderQuantities)
            sum += shoppingAdapter.foodList.find { it.id == id }?.price?.times(quantity) ?: 0f
        total_textview.text = getString(R.string.total_text, sum)
    }

    private fun clearQuantities() {
        model.orderQuantities.clear()
        notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ORDER) {
            if (resultCode == RESULT_OK) {
                clearQuantities()
            } else if (resultCode == RESULT_FINISH) {
                finish()
            }
        }
    }


    fun onDataChange(restaurant: Datastore.Restaurant) {
        shoppingAdapter.foodList = restaurant.menu
        tv_restaurant_name.text = restaurant.name
        notifyDataSetChanged()
    }
}