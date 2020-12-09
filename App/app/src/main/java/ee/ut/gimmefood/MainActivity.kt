package ee.ut.gimmefood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val REQUEST_ORDER = 200
    lateinit var database: MockDatabase
    var orderQuantities: MutableMap<Food, Int> = mutableMapOf()
    lateinit var shoppingAdapter: ShoppingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MockDatabase(resources)
        setContentView(R.layout.activity_main)

        shoppingAdapter = ShoppingAdapter(database.getFoodList(),
                                          orderQuantities,
                                          { food -> changeFoodQuantity(food,  1); },
                                          { food -> changeFoodQuantity(food, -1); })

        my_recyclerview.adapter = shoppingAdapter

        button_place_order.setOnClickListener {
            val orderFoodIdsList = mutableListOf<Long>()
            val orderQuantitiesList = mutableListOf<Int>()
            for ((key, value) in orderQuantities) {
                orderFoodIdsList.add(key.id)
                orderQuantitiesList.add(value)
            }

            val intent = Intent(this, OrderDetailsActivity::class.java)
            intent.putExtra("ids", orderFoodIdsList.toLongArray())
            intent.putExtra("quantities", orderQuantitiesList.toIntArray())
            startActivityForResult(intent, REQUEST_ORDER)
        }

        notifyDataSetChanged()
    }

    private fun notifyDataSetChanged() {
        shoppingAdapter.notifyDataSetChanged()
        updateTotalText()
        button_place_order.isEnabled = orderQuantities.isNotEmpty()
    }

    private fun changeFoodQuantity(food: Food, changeDelta: Int) {
        val newQuantity = orderQuantities.getOrElse(food, {0}) + changeDelta
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
            }
        }
    }
}