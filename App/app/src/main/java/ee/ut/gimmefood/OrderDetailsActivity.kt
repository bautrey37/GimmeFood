package ee.ut.gimmefood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailsActivity : AppCompatActivity() {
    lateinit var database: MockDatabase
    lateinit var shoppingAdapter: ShoppingAdapter
    val foodsById = mutableMapOf<Long, Food>()
    val orderQuantities = mutableMapOf<Food, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MockDatabase(resources)
        setContentView(R.layout.activity_order_details)

        loadIntentData()

        shoppingAdapter = ShoppingAdapter(foodsById.values.toList(),
                orderQuantities,
                { food -> changeFoodQuantity(food,  1); },
                { food -> changeFoodQuantity(food, -1); })

        order_recyclerview.adapter = shoppingAdapter

        cancel_button.setOnClickListener{ finish() }
        order_button.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }

        updateTotalText()
    }

    private fun loadIntentData() {
        val foodIds = intent.getLongArrayExtra("ids")
        if (foodIds != null) {
            foodIds.forEach {
                val food = database.getFoodById(it)
                if (food != null)
                    foodsById.put(it, food)
            }
            intent.getIntArrayExtra("quantities")?.forEachIndexed { index, quantity ->
                val foodId = foodIds[index]
                val food = foodsById[foodId]
                if (food != null)
                    orderQuantities.put(food, quantity)
            }
        }
    }

    private fun changeFoodQuantity(food: Food, changeDelta: Int) {
        val newQuantity = orderQuantities.getOrElse(food, {0}) + changeDelta
        if (newQuantity <= 0) {
            orderQuantities.remove(food)
        } else {
            orderQuantities.put(food, newQuantity)
        }
        shoppingAdapter.notifyDataSetChanged()
        updateTotalText()

        order_button.isEnabled = orderQuantities.isNotEmpty()
    }

    private fun updateTotalText() {
        var sum = 0f
        for ((food, quantity) in orderQuantities)
            sum += food.price * quantity
        total_textview.text = getString(R.string.total_text, sum)
    }
}