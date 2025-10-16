package com.rfdarter.beehunting.beelogging

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rfdarter.beehunting.R
import com.rfdarter.beehunting.SettingsActivity
import com.rfdarter.beehunting.beelogging.data.HoneyBee
import com.rfdarter.beehunting.beelogging.data.HoneyBeeFactory
import com.rfdarter.beehunting.beelogging.ui.beelist.BeeAdapter
import com.rfdarter.beehunting.beelogging.ui.beelist.BeeListViewModel
import com.rfdarter.beehunting.beelogging.ui.beelist.dialogs.AddBeeDialog
import com.rfdarter.beehunting.beelogging.ui.beelist.dialogs.AddBeeEventDialog
import com.rfdarter.beehunting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var beeAdapter: BeeAdapter

    private val viewModel by viewModels<BeeListViewModel>()

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

        beeAdapter = BeeAdapter(HoneyBeeFactory.getAllBees(),
            onBeeClick = { bee ->
                AddBeeEventDialog(
                    this,
                    bee,
                    onArrivedPressed = {
                        viewModel.OnBeeArrivedPressed(bee)
                    },
                    onLeftPressed = {
                        viewModel.OnBeeLeftPressed(bee)
                    }
                ).show()
            },
            onBeeLongClick = { anchorView, bee ->
                showBeeItemMenu(anchorView, bee)
                true
            }
        )
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

            AddBeeDialog(
                this,
                colorResIds = colorResIds
            ) { beeColor, done ->
                // synchroner Versuch: gibt null zurück, wenn Kombination existiert
                val created = viewModel.AddNewBee(beeColor) != null
                done(created) // Dialog schließt nur bei true
                beeAdapter.notifyDataSetChanged()
            }.show()

//            createRandomBee()
//            AddBeeDialog(this, colorResIds) { beeColor ->
//                if( viewModel.AddNewBee(beeColor) == null){
//                    return false
//                } else {
//                    return true
//                    beeAdapter.notifyDataSetChanged()
//                }
//            }.show()
        }

    }

    private fun showBeeItemMenu(anchor: View, bee: HoneyBee) {
        val idDetails = 1
        val idDelete = 2
        PopupMenu(this, anchor).apply {
            menu.add(0, idDetails, 0, "Details")
            menu.add(0, idDelete, 1, "Delete")
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    idDetails -> {
                        showBeeInfoOverlay(bee)
                        true
                    }
                    idDelete -> {
                        confirmAndDeleteBee(bee)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun confirmAndDeleteBee(bee: HoneyBee) {
        AlertDialog.Builder(this)
            .setTitle("Delete Bee?")
            .setMessage("This action can not be reversed!")
            .setNegativeButton("Canlce", null)
            .setPositiveButton("Delete") { _, _ ->
                if (viewModel.DeleteBee(bee)) {
                    beeAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Bee deleted", Toast.LENGTH_SHORT).apply {
                        setGravity(Gravity.CENTER, 0, 0)
                        show()
                    }
                }
            }
            .show()
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