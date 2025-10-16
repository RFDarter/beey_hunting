package com.rfdarter.beehunting.beelogging.ui.beelist

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rfdarter.beehunting.beelogging.data.BeeColor
import com.rfdarter.beehunting.beelogging.ui.common.ColorAdapter
import com.rfdarter.beehunting.beelogging.data.HoneyBee
import com.rfdarter.beehunting.beelogging.data.HoneyBeeFactory
import com.rfdarter.beehunting.R
import com.rfdarter.beehunting.SettingsActivity
import com.rfdarter.beehunting.beelogging.ui.beelist.dialogs.AddBeeDialog
import com.rfdarter.beehunting.databinding.ActivityMainBinding

class BeeListScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var beeAdapter: BeeAdapter

    private val colorNames = listOf(
        "Rot", "Gelb", "Orange", "Hellblau", "Dunkelblau", "Hellbraun", "Dunkelbraun", "Schwarz", "Hellgrün", "Dunkelgrün", "Lila", "Pink"
    )
    private val colorResIds = listOf(
        R.color.bee_red,
        R.color.bee_yellow,
        R.color.bee_orange,
        R.color.bee_light_blue,
        R.color.bee_dark_blue,
        R.color.bee_light_brown,
        R.color.bee_dark_brown,
        R.color.bee_black,
        R.color.bee_light_green,
        R.color.bee_dark_green,
        R.color.bee_lila,
        R.color.bee_pink
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val overflowDrawable = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_menu_overflow_material)
        overflowDrawable?.setTint(ContextCompat.getColor(this, android.R.color.white)) // Farbe anpassen falls nötig
        binding.toolbar.overflowIcon = overflowDrawable

        beeAdapter = BeeAdapter(HoneyBeeFactory.getAllBees()) { bee ->
            showBeeInfoOverlay(bee)
        }
        binding.beeList.layoutManager = LinearLayoutManager(this)
        binding.beeList.adapter = beeAdapter

        binding.beeButton.setOnClickListener {
            // kleine Einfeder-Animation
            binding.beeButton.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    binding.beeButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

//            createRandomBee()
            AddBeeDialog(this, colorResIds) { beeColor ->
                if (HoneyBeeFactory.createBee(beeColor) == null) {
                    AlertDialog.Builder(this)
                        .setTitle("Not Allowed")
                        .setMessage("This color combination already exists!")
                        .setNegativeButton("OK", null)
                        .show()
                } else {
                    beeAdapter.notifyDataSetChanged()
                }
            }.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showColorPickerOverlay() {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_color_picker_overlay, null)
        dialog.setContentView(view)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

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

        // aktuelle laufende Animatoren (cancellen wenn nötig)
        var currentAnimators: List<ValueAnimator>? = null

        fun stopCurrentPulse() {
            currentAnimators?.forEach { it.cancel() }
            currentAnimators = null
            thoraxRing.visibility = View.GONE
            abdomenRing.visibility = View.GONE
        }

        fun startPulseOn(view: View) {
            stopCurrentPulse()
            view.visibility = View.VISIBLE
            view.scaleX = 1f
            view.scaleY = 1f
            view.alpha = 1f

            val sx = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.25f, 1f)
            val sy = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.25f, 1f)
            val a = ObjectAnimator.ofFloat(view, "alpha", 0.9f, 0.3f, 0.9f)

            listOf(sx, sy, a).forEach { anim ->
                anim.duration = 900
                (anim as ValueAnimator).repeatCount = ValueAnimator.INFINITE
                anim.repeatMode = ValueAnimator.RESTART
                anim.interpolator = AccelerateDecelerateInterpolator()
                anim.start()
            }
            currentAnimators = listOf(sx, sy, a)
        }

        fun updatePreview(imageView: ImageView, resId: Int?) {
            if (resId == null) {
                imageView.setImageDrawable(null)
                imageView.background = ContextCompat.getDrawable(this, R.drawable.transparent_circle_placeholder)
            } else {
                val gd = GradientDrawable()
                gd.shape = GradientDrawable.OVAL
                gd.setColor(ContextCompat.getColor(this, resId))
                gd.alpha = 200
                gd.setStroke((2 * resources.displayMetrics.density).toInt(), ContextCompat.getColor(this, android.R.color.white))
                imageView.background = gd
                imageView.setImageDrawable(null)
            }
        }

        val palette = listOf<Int?>(null) + colorResIds
        colorGrid.layoutManager = GridLayoutManager(this, 6)
        val adapter = ColorAdapter(this, palette) { chosenRes ->
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
        colorGrid.adapter = adapter

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
            dialog.dismiss()
        }

        addBtn.setOnClickListener {
            if (selectedThoraxRes == null && selectedAbdomenRes == null) {
                val dialogView = layoutInflater.inflate(R.layout.dialog_color_picker, null)
                AlertDialog.Builder(this)
                    .setTitle("Not Allowed")
                    .setMessage("Please specify at least one color (thorax or abdomen)!")
                    .setNegativeButton("OK", null)
                    .show()
                return@setOnClickListener
            }
            val thoraxColor = selectedThoraxRes?.let { ContextCompat.getColor(this, it) }
            val abdomenColor = selectedAbdomenRes?.let { ContextCompat.getColor(this, it) }
            if(HoneyBeeFactory.createBee(BeeColor(thoraxColor, abdomenColor)) == null) {
                AlertDialog.Builder(this)
                    .setTitle("Not Allowed")
                    .setMessage("This color combination already exists!")
                    .setNegativeButton("OK", null)
                    .show()
                return@setOnClickListener
            }
            beeAdapter.notifyDataSetChanged()
            stopCurrentPulse()
            dialog.dismiss()
        }

        updatePreview(thoraxPreview, null)
        updatePreview(abdomenPreview, null)

        dialog.setOnDismissListener {
            stopCurrentPulse()
        }

        startPulseOn(thoraxRing)
        dialog.show()
    }

    private fun showBeeInfoOverlay(bee: HoneyBee) {
        val info = """
        ID: ${bee.id}
        Thorax: ${bee.color.thorax?.let { "#${Integer.toHexString(it)}" } ?: "keine"}
        Abdomen: ${bee.color.abdomen?.let { "#${Integer.toHexString(it)}" } ?: "keine"}
        Status: ${bee.status}
        Events: ${bee.events.size}
        Feeder-Zeiten: ${bee.feederPeriods.size}
        Away-Zeiten: ${bee.awayPeriods.size}
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Bienen-Info")
            .setMessage(info)
            .setPositiveButton("OK", null)
            .show()
    }


}