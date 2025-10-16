package com.rfdarter.beehunting.beelogging.ui.beelist.dialogs

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.rfdarter.beehunting.R
import com.rfdarter.beehunting.beelogging.data.HoneyBee

class AddBeeEventDialog(
    private val context: Context,
    private val bee: HoneyBee,
    private val onArrivedPressed: () -> Unit,
    private val onLeftPressed: () -> Unit
) {

    private var dialog: Dialog? = null
    private var currentAnimators: List<ValueAnimator>? = null

    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_bee_event, null)
        dialog = Dialog(context).apply {
            setContentView(view)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        val arrived_btn = view.findViewById<Button>(R.id.bee_arrived_btn)
        val left_btn = view.findViewById<Button>(R.id.bee_left_btn)

        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        arrived_btn.setOnClickListener {
            onArrivedPressed()
            Toast.makeText(context, context.getString(R.string.event_added_success), Toast.LENGTH_SHORT).apply {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
            dialog?.dismiss()
        }
        left_btn.setOnClickListener {
            onLeftPressed()
            Toast.makeText(context, context.getString(R.string.event_added_success), Toast.LENGTH_SHORT).apply {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
            dialog?.dismiss()
        }

        dialog?.show()
    }

}