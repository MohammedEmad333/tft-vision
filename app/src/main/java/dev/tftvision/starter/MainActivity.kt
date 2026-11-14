package dev.tftvision.starter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var preview: ImageView
    private val screenshotRequest = 312

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 60, 30, 30)
            setBackgroundColor(Color.rgb(16, 20, 37))
        }
        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
        root.addView(label("TFT VISION", 28f))
        root.addView(label("Static floating guide • prototype v0.1", 16f))
        root.addView(action("1. Allow floating windows") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        })
        root.addView(action("2. Start floating guide") {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Allow floating windows first", Toast.LENGTH_LONG).show()
            } else {
                startForegroundService(Intent(this, OverlayService::class.java))
            }
        })
        root.addView(action("Stop floating guide") {
            stopService(Intent(this, OverlayService::class.java))
        })
        root.addView(label("After a match", 20f))
        root.addView(label("Import a screenshot for local preview. AI recognition is not implemented.", 15f))
        root.addView(action("Import screenshot") {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }, screenshotRequest)
        })
        preview = ImageView(this).apply {
            adjustViewBounds = true
            contentDescription = "Imported post-game screenshot"
        }
        root.addView(preview, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(label("This app does not inspect live TFT gameplay.", 14f))
    }

    @Deprecated("Simple local image picker for the starter prototype")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == screenshotRequest && resultCode == RESULT_OK) {
            data?.data?.let { preview.setImageURI(it) }
        }
    }

    private fun label(value: String, size: Float) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(Color.WHITE)
        setPadding(0, 0, 0, 22)
    }

    private fun action(title: String, callback: () -> Unit) = Button(this).apply {
        text = title
        isAllCaps = false
        setOnClickListener { callback() }
    }
}
