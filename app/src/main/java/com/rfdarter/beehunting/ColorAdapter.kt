// app/src/main/java/com/example/myapplication2/ColorAdapter.kt
package com.rfdarter.beehunting

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rfdarter.beehunting.databinding.ColorItemBinding
import androidx.core.content.ContextCompat

class ColorAdapter(
    private val context: Context,
    private val colorResList: List<Int?>, // null = transparent
    private val onColorSelected: (Int?) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(val binding: ColorItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val resId = colorResList[position]
        val view = holder.binding.colorCircle

        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        gd.setStroke(2.dpToPx(context), ContextCompat.getColor(context, android.R.color.white))
        if (resId == null) {
            view.background=ContextCompat.getDrawable(context, R.drawable.texture_transparent_oval)
            view.foreground = gd
        } else {
            gd.setColor(ContextCompat.getColor(context, resId))
            view.background = gd
        }

        holder.binding.root.setOnClickListener {
            onColorSelected(resId)
        }
    }

    override fun getItemCount(): Int = colorResList.size
}

private fun Int.dpToPx(ctx: Context): Int =
    (this * ctx.resources.displayMetrics.density).toInt()
