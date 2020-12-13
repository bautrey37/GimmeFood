package ee.ut.gimmefood

import android.content.res.Resources
import androidx.core.graphics.drawable.toBitmap
import ee.ut.gimmefood.data.Food

class MockDatabase(val resources: Resources) {
    var foodMap: MutableMap<String, Food> = mutableMapOf()

    init {
        val mockFoodIds = resources.getIntArray(R.array.mock_food_ids)
        val mockFoodNames = resources.getStringArray(R.array.mock_food_names)
        val mockFoodImages = resources.obtainTypedArray(R.array.mock_food_images)
        val mockFoodPrices = resources.obtainTypedArray(R.array.mock_food_prices)
        for (i in 0..5) {
            val id = mockFoodIds[i].toLong()
            val name = mockFoodNames[i]
            val image = mockFoodImages.getDrawable(i)
            val price = mockFoodPrices.getFloat(i, 0f)
            foodMap[id.toString()] = Food(id.toString(), name, image?.toBitmap(), price)
        }
    }

    fun getFoodList(): List<Food> {
        return foodMap.values.toList()
    }

    fun getFoodById(id: String): Food? {
        return foodMap[id]
    }
}