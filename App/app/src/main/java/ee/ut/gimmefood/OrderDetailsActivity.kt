package ee.ut.gimmefood

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ee.ut.gimmefood.data.Datastore
import ee.ut.gimmefood.data.Food
import ee.ut.gimmefood.shopping.ShoppingAdapter
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailsActivity : AppCompatActivity() {
    lateinit var database: MockDatabase
    lateinit var shoppingAdapter: ShoppingAdapter
    private val foods = mutableListOf<Food>()
    private val orderQuantities = mutableMapOf<String, Int>()
    private var restaurantId: String? = null
    private var tableNum: Int = -1

    val REQUEST_ORDER = 201

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = MockDatabase(resources)
        setContentView(R.layout.activity_order_details)

        loadIntentData()

        shoppingAdapter = ShoppingAdapter(this, foods,
            orderQuantities,
            { food -> changeFoodQuantity(food, 1); },
            { food -> changeFoodQuantity(food, -1); })

        order_recyclerview.adapter = shoppingAdapter

        cancel_button.setOnClickListener { finish() }
        order_button.setOnClickListener {
            submitOrder()
        }

        updateTotalText()

        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.your_item_id -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun submitOrder() {
        restaurantId?.let {
            Datastore.getInstance().sendOrder(
                it,
                tableNum,
                orderQuantities
                    .map { (id, quantity) -> foods.find { it.id == id }!! to quantity }
                    .toMap()
                    .toMutableMap(),
                getTotalPrice()
            )
            val intent = Intent(this, OrderResult::class.java)
            intent.putExtra("summary", generateSummary())
            startActivityForResult(intent, REQUEST_ORDER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ORDER) {
            setResult(resultCode)
            finish()
        }
    }


    private fun generateSummary(): String {
        return foods
            .filter { orderQuantities[it.id] != null }
            .joinToString("\n") { "${it.name} x${orderQuantities[it.id]}" }
    }

    private fun loadIntentData() {
        val foodIds = intent.getStringArrayExtra("ids")
        if (foodIds != null) {
            intent.getIntArrayExtra("quantities")?.forEachIndexed { index, quantity ->
                orderQuantities[foodIds[index]] = quantity
            }
            foodIds.forEach { id -> Datastore.getInstance().getFoodById(id)?.let { foods += it } }
        }
        restaurantId = intent.getStringExtra("restaurantId")
        tableNum = intent.getIntExtra("tableNum", -1)
    }

    private fun changeFoodQuantity(food: Food, changeDelta: Int) {
        val newQuantity = orderQuantities.getOrElse(food.id, { 0 }) + changeDelta
        if (newQuantity <= 0) {
            orderQuantities.remove(food.id)
        } else {
            orderQuantities[food.id] = newQuantity
        }
        shoppingAdapter.notifyDataSetChanged()
        updateTotalText()

        order_button.isEnabled = orderQuantities.isNotEmpty()
    }

    private fun updateTotalText() {
        val sum = getTotalPrice()
        total_textview.text = getString(R.string.total_text, sum)
    }

    private fun getTotalPrice(): Float {
        var sum = 0f
        for ((id, quantity) in orderQuantities)
            sum += foods.find { it.id == id }?.price?.times(quantity) ?: 0f
        return sum
    }
}