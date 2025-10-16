package com.rfdarter.beehunting.beelogging.ui.beelist

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rfdarter.beehunting.R
import com.rfdarter.beehunting.beelogging.data.HoneyBee
import com.rfdarter.beehunting.databinding.ListItemBeeBinding

class BeeAdapter(private val bees: List<HoneyBee>,
                 private val onBeeClick: (HoneyBee) -> Unit
) : RecyclerView.Adapter<BeeAdapter.BeeViewHolder>() {

    class BeeViewHolder(val binding: ListItemBeeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeeViewHolder {
        val binding = ListItemBeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeeViewHolder, position: Int) {
        val bee = bees[position]
        val thoraxView = holder.binding.include.thoraxPreview
        val abdomenView = holder.binding.include.abdomenPreview
        setBeeColor(thoraxView, bee.color.thorax)
        setBeeColor(abdomenView, bee.color.abdomen)
//        holder.binding.beeId.text = "ID: ${bee.id}"
//        val thorax = bee.color.thorax?.let { "#${Integer.toHexString(it)}" } ?: "keine"
//        val abdomen = bee.color.abdomen?.let { "#${Integer.toHexString(it)}" } ?: "keine"
//        holder.binding.beeColor.text = "Thorax: $thorax, Abdomen: $abdomen"
        holder.binding.root.setOnClickListener { onBeeClick(bee) }
    }

    override fun getItemCount() = bees.size

    fun setBeeColor(imageView: ImageView, color: Int?) {
        if (color == null) {
            imageView.setImageDrawable(null)
            imageView.background = ContextCompat.getDrawable(imageView.context, R.drawable.transparent_circle_placeholder)
        } else {
            val gd = GradientDrawable()
            gd.shape = GradientDrawable.OVAL
            gd.setColor(color)
//            gd.alpha = 200
            gd.setStroke((1 * imageView.resources.displayMetrics.density).toInt(), ContextCompat.getColor(imageView.context, android.R.color.white))
            imageView.background = gd
            imageView.setImageDrawable(null)
        }
    }

}