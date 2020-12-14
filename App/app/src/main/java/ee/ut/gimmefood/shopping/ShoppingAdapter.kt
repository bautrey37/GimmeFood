package ee.ut.gimmefood.shopping

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import ee.ut.gimmefood.MenuActivity.Companion.TAG
import ee.ut.gimmefood.R
import ee.ut.gimmefood.blurhash.BlurHashDecoder
import ee.ut.gimmefood.data.Datastore
import ee.ut.gimmefood.data.Food
import java.net.URL
import kotlin.concurrent.thread

class ShoppingAdapter(
    var activity: Activity,
    var foodList: List<Food>,
    private val orderQuantities: Map<String, Int>,
    private val onAddClick: (Food) -> Unit,
    private val onRemoveClick: (Food) -> Unit
) : RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>() {

    class ShoppingViewHolder(
        private val view: View,
        private val orderQuantities: Map<String, Int>,
        private val onAddClick: (Food) -> Unit,
        private val onRemoveClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val foodTextView: TextView = view.findViewById(R.id.food_text)
        private val foodImageView: ImageView = view.findViewById(R.id.food_image)
        private val foodPriceView: TextView = view.findViewById(R.id.food_price)
        private val addButton: Button = view.findViewById(R.id.button_add)
        private val orderQuantityView: TextView = view.findViewById(R.id.food_quantity)
        private val removeButton: Button = view.findViewById(R.id.button_remove)


        fun bind(food: Food) {
//            val prefLargeImages = PreferenceManager.getDefaultSharedPreferences(view.context)
//                .getString("big_images", view.context.packageName)
//            Log.d("debug", "prefLargeImages: $prefLargeImages")

            foodTextView.text = food.name
            foodImageView.setImageBitmap(
                food.image ?: BlurHashDecoder.decode(
                    food.image_hash,
                    100,
                    100
                )
            )
            foodPriceView.text = "${food.price}"
            addButton.setOnClickListener { onAddClick(food) }
            val quantity = orderQuantities.getOrElse(food.id, { 0 })
            orderQuantityView.text = "${quantity}"
            removeButton.isEnabled = quantity > 0
            removeButton.setOnClickListener { onRemoveClick(food) }
        }

        fun bindImage(image: Bitmap?) {
            image?.let {
                foodImageView.setImageBitmap(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_item_layout, parent, false)
        return ShoppingViewHolder(view, orderQuantities, onAddClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        holder.bind(foodList[position])

        if (foodList[position].image == null) {
            val imageUrl = foodList[position].image_url

            if (imageUrl.isNotEmpty()) {
                Datastore.getInstance().getBitmapFromCache(imageUrl)?.also {
                    holder.bindImage(it)
                } ?: run {
                    thread(start = true) {
                        val url = URL(imageUrl)
                        val fullBitmap =
                            BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        activity.runOnUiThread {
                            holder.bindImage(fullBitmap)
                        }
                        foodList[position].image = fullBitmap
                        Datastore.getInstance().addBitmapToCache(imageUrl, fullBitmap)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}