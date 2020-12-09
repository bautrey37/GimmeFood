package ee.ut.gimmefood.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ee.ut.gimmefood.R
import ee.ut.gimmefood.data.Food

class ShoppingAdapter(private val foodList: List<Food>,
                      private val orderQuantities: Map<Food, Int>,
                      private val onAddClick: (Food) -> Unit,
                      private val onRemoveClick: (Food) -> Unit)
    : RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>()
{

    class ShoppingViewHolder(private val view: View,
                             private val orderQuantities: Map<Food, Int>,
                             private val onAddClick: (Food) -> Unit,
                             private val onRemoveClick: (Food) -> Unit)
        : RecyclerView.ViewHolder(view)
    {
        private val foodTextView: TextView = view.findViewById(R.id.food_text)
        private val foodImageView: ImageView = view.findViewById(R.id.food_image)
        private val foodPriceView: TextView = view.findViewById(R.id.food_price)
        private val addButton: Button = view.findViewById(R.id.button_add)
        private val orderQuantityView: TextView = view.findViewById(R.id.food_quantity)
        private val removeButton: Button = view.findViewById(R.id.button_remove)


        fun bind(food: Food) {
            foodTextView.text = food.name
            foodImageView.setImageDrawable(food.image)
            foodPriceView.text = "${food.price}"
            addButton.setOnClickListener{ onAddClick(food) }
            val quantity = orderQuantities.getOrElse(food, {0})
            orderQuantityView.text = "${quantity}"
            removeButton.isEnabled = quantity > 0
            removeButton.setOnClickListener{ onRemoveClick(food) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_item_layout, parent, false)
        return ShoppingViewHolder(view, orderQuantities, onAddClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}