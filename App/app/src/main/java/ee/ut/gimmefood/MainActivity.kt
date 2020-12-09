package ee.ut.gimmefood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter

class MainActivity : AppCompatActivity() {
    lateinit var database: MockDatabase
    var orderQuantities: MutableMap<Food, Int> = mutableMapOf()
    lateinit var shoppingAdapter: ShoppingAdapter
    lateinit var totalTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MockDatabase(resources)
        setContentView(R.layout.activity_main)

        shoppingAdapter = ShoppingAdapter(database.getFoodList(),
                                          orderQuantities,
                                          { food -> changeFoodQuantity(food,  1); },
                                          { food -> changeFoodQuantity(food, -1); })

        val recyclerView: RecyclerView = findViewById(R.id.my_recyclerview)
        recyclerView.adapter = shoppingAdapter

        totalTextView = findViewById(R.id.total_textview)
        updateTotalText()
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
    }

    private fun updateTotalText() {
        var sum = 0f
        for ((food, quantity) in orderQuantities)
            sum += food.price * quantity
        totalTextView.text = "${resources.getString(R.string.total_text)} $sum €"
    }
}