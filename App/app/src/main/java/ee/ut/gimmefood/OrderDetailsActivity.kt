package ee.ut.gimmefood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ee.ut.gimmefood.data.Datastore
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailsActivity : AppCompatActivity() {
    lateinit var database: MockDatabase
    lateinit var shoppingAdapter: ShoppingAdapter
    private val orderQuantities = mutableMapOf<Food, Int>()
    private var restaurantId: String? = null
    private var tableNum: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MockDatabase(resources)
        setContentView(R.layout.activity_order_details)

        loadIntentData()

        shoppingAdapter = ShoppingAdapter(this, orderQuantities.keys.toList(),
            orderQuantities,
            { food -> changeFoodQuantity(food, 1); },
            { food -> changeFoodQuantity(food, -1); })

        order_recyclerview.adapter = shoppingAdapter

        cancel_button.setOnClickListener { finish() }
        order_button.setOnClickListener {
            submitOrder()
            setResult(RESULT_OK)
            finish()
        }

        updateTotalText()
    }

    private fun submitOrder() {
        restaurantId?.let {
            Datastore.getInstance().sendOrder(it, tableNum, orderQuantities, getTotalPrice())
        }
    }

    private fun loadIntentData() {
        val foodIds = intent.getStringArrayExtra("ids")
        if (foodIds != null) {
            intent.getIntArrayExtra("quantities")?.forEachIndexed { index, quantity ->
                Datastore.getInstance().getFoodById(foodIds[index])?.let {
                    orderQuantities[it] = quantity
                }
            }
        }
        restaurantId = intent.getStringExtra("restaurantId")
        tableNum = intent.getIntExtra("tableNum", -1)
    }

    private fun changeFoodQuantity(food: Food, changeDelta: Int) {
        val newQuantity = orderQuantities.getOrElse(food, { 0 }) + changeDelta
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
        val sum = getTotalPrice();
        total_textview.text = getString(R.string.total_text, sum)
    }

    private fun getTotalPrice(): Float {
        var sum = 0f
        for ((food, quantity) in orderQuantities)
            sum += food.price * quantity
        return sum
    }
}