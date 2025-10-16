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

class MainActivity : AppCompatActivity() {

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