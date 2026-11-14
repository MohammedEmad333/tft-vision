package dev.tftvision.starter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {
    private var manager: WindowManager? = null
    private var panel: View? = null
    private var guideIndex = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = "tft_vision_guide"
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            NotificationChannel(channel, "TFT Vision floating guide", NotificationManager.IMPORTANCE_LOW)
        )
        val intentBack = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(this, channel)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("TFT Vision floating guide")
            .setContentText("Tap to return to the app")
            .setContentIntent(intentBack)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
        if (panel == null && Settings.canDrawOverlays(this)) {
            try { showGuide() } catch (_: Exception) { stopSelf() }
        } else if (!Settings.canDrawOverlays(this)) {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun showGuide() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        manager = wm
        val params = WindowManager.LayoutParams(
            dp(290), WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(12)
            y = dp(100)
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(10), dp(10), dp(10))
            setBackgroundColor(Color.rgb(24, 34, 56))
            elevation = dp(8).toFloat()
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = text("TFT VISION", 15f)
        header.addView(title, LinearLayout.LayoutParams(0, dp(48), 1f))
        val collapse = Button(this).apply { text = "−" }
        val close = Button(this).apply { text = "×" }
        header.addView(collapse, LinearLayout.LayoutParams(dp(48), dp(48)))
        header.addView(close, LinearLayout.LayoutParams(dp(48), dp(48)))
        root.addView(header)
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val name = text("", 18f)
        val detail = text("", 14f)
        body.addView(name)
        body.addView(detail)
        val nav = LinearLayout(this)
        val prev = Button(this).apply { text = "‹" }
        val next = Button(this).apply { text = "›" }
        nav.addView(prev, LinearLayout.LayoutParams(0, dp(45), 1f))
        nav.addView(next, LinearLayout.LayoutParams(0, dp(45), 1f))
        body.addView(nav)
        body.addView(text("Static reference • no live tracking", 11f))
        root.addView(body)
        fun refresh() {
            name.text = GuideData.guides[guideIndex].title
            detail.text = GuideData.guides[guideIndex].detail
        }
        prev.setOnClickListener {
            guideIndex = (guideIndex - 1 + GuideData.guides.size) % GuideData.guides.size
            refresh()
        }
        next.setOnClickListener {
            guideIndex = (guideIndex + 1) % GuideData.guides.size
            refresh()
        }
        collapse.setOnClickListener {
            body.visibility = if (body.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            collapse.text = if (body.visibility == View.VISIBLE) "−" else "+"
            wm.updateViewLayout(root, params)
        }
        close.setOnClickListener { stopSelf() }
        var startX = 0
        var startY = 0
        var touchX = 0f
        var touchY = 0f
        title.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = (startX + event.rawX - touchX).toInt().coerceAtLeast(0)
                    params.y = (startY + event.rawY - touchY).toInt().coerceAtLeast(0)
                    wm.updateViewLayout(root, params)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> true
                else -> false
            }
        }
        refresh()
        wm.addView(root, params)
        panel = root
    }

    private fun text(value: String, size: Float) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(Color.WHITE)
        setPadding(dp(3), dp(3), dp(3), dp(7))
    }
    private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        panel?.let { try { manager?.removeView(it) } catch (_: IllegalArgumentException) {} }
        panel = null
        super.onDestroy()
    }
}
