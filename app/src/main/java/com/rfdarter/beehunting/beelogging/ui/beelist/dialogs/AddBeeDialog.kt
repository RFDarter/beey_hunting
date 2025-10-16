package com.rfdarter.beehunting.beelogging.ui.beelist.dialogs

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rfdarter.beehunting.beelogging.data.BeeColor
import com.rfdarter.beehunting.beelogging.ui.common.ColorAdapter
import com.rfdarter.beehunting.R

class AddBeeDialog(
    private val context: Context,
    private val colorResIds: List<Int>,
    private val onBeeCreated: (BeeColor) -> Unit
) {

    private var dialog: Dialog? = null
    private var currentAnimators: List<ValueAnimator>? = null

    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_bee, null)
        dialog = Dialog(context).apply {
            setContentView(view)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val thoraxPreview = view.findViewById<ImageView>(R.id.thorax_preview)
        val abdomenPreview = view.findViewById<ImageView>(R.id.abdomen_preview)
        val thoraxRing = view.findViewById<View>(R.id.thorax_ring)
        val abdomenRing = view.findViewById<View>(R.id.abdomen_ring)
        val colorGrid = view.findViewById<RecyclerView>(R.id.color_grid)
        val addBtn = view.findViewById<Button>(R.id.add_btn)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)

        var activeTarget = 0
        var selectedThoraxRes: Int? = null
        var selectedAbdomenRes: Int? = null

        fun stopCurrentPulse() {
            currentAnimators?.forEach { it.cancel() }
            currentAnimators = null
            thoraxRing.visibility = View.GONE
            abdomenRing.visibility = View.GONE
        }

        fun startPulseOn(view: View) {
            stopCurrentPulse()
            view.visibility = View.VISIBLE
            val sx = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.25f, 1f)
            val sy = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.25f, 1f)
            val a = ObjectAnimator.ofFloat(view, "alpha", 0.9f, 0.3f, 0.9f)
            listOf(sx, sy, a).forEach { anim ->
                anim.duration = 900
                (anim as ValueAnimator).repeatCount = ValueAnimator.INFINITE
                anim.interpolator = AccelerateDecelerateInterpolator()
                anim.start()
            }
            currentAnimators = listOf(sx, sy, a)
        }

        fun updatePreview(imageView: ImageView, resId: Int?) {
            if (resId == null) {
                imageView.setImageDrawable(null)
                imageView.background = ContextCompat.getDrawable(context, R.drawable.transparent_circle_placeholder)
            } else {
                val gd = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ContextCompat.getColor(context, resId))
                    alpha = 200
                    setStroke((2 * context.resources.displayMetrics.density).toInt(),
                        ContextCompat.getColor(context, android.R.color.white))
                }
                imageView.background = gd
            }
        }

        val palette = listOf<Int?>(null) + colorResIds
        colorGrid.layoutManager = GridLayoutManager(context, 6)
        colorGrid.adapter = ColorAdapter(context, palette) { chosenRes ->
            if (activeTarget == 0) {
                selectedThoraxRes = chosenRes
                updatePreview(thoraxPreview, chosenRes)
                abdomenPreview.performClick()
            } else {
                selectedAbdomenRes = chosenRes
                updatePreview(abdomenPreview, chosenRes)
                thoraxPreview.performClick()
            }
        }

        thoraxPreview.setOnClickListener {
            activeTarget = 0
            startPulseOn(thoraxRing)
        }
        abdomenPreview.setOnClickListener {
            activeTarget = 1
            startPulseOn(abdomenRing)
        }

        cancelBtn.setOnClickListener {
            stopCurrentPulse()
            dialog?.dismiss()
        }

        addBtn.setOnClickListener {
            if (selectedThoraxRes == null && selectedAbdomenRes == null) {
                AlertDialog.Builder(context)
                    .setTitle("Not Allowed")
                    .setMessage("Please specify at least one color!")
                    .setNegativeButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            val thoraxColor = selectedThoraxRes?.let { ContextCompat.getColor(context, it) }
            val abdomenColor = selectedAbdomenRes?.let { ContextCompat.getColor(context, it) }

            onBeeCreated(BeeColor(thoraxColor, abdomenColor))
            stopCurrentPulse()
            dialog?.dismiss()
        }

        updatePreview(thoraxPreview, null)
        updatePreview(abdomenPreview, null)

        dialog?.setOnDismissListener { stopCurrentPulse() }
        startPulseOn(thoraxRing)
        dialog?.show()
    }
}
