package com.example.prayertimeswidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class PrayerTimeWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) updateAppWidget(context, manager, id)
    }

    private fun updateAppWidget(context: Context, manager: AppWidgetManager, id: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        // Placeholder times; replace with API logic
        val times = mapOf("Fajr" to "04:07", "Sunrise" to "05:31", "Dhuhr" to "12:07",
            "Asr" to "16:48", "Maghrib" to "18:44", "Isha" to "20:08")
        val idsMap = mapOf("Fajr" to R.id.time_fajr, "Sunrise" to R.id.time_sunrise,
            "Dhuhr" to R.id.time_dhuhr, "Asr" to R.id.time_asr,
            "Maghrib" to R.id.time_maghrib, "Isha" to R.id.time_isha)
        val now = SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
        var current = ""
        var next: String? = null // Make 'next' nullable

        val sorted = times.entries.sortedBy { it.value }
        for (i in sorted.indices) {
            if (now < sorted[i].value) {
                next = sorted[i].key
                if (i > 0) current = sorted[i - 1].key
                break
            }
        }
        if (current.isEmpty() && sorted.isNotEmpty()) current = sorted.last().key

        for ((k, v) in times) {
            views.setTextViewText(idsMap[k]!!, v)
            views.setTextColor(idsMap[k]!!, when (k) {
                current -> Color.GREEN
                next -> Color.YELLOW // Now 'next' can be null
                else -> Color.WHITE
            })
        }
        val intent = Intent(context, PrayerTimeWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(id))
        }
        val pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_root, pi)
        manager.updateAppWidget(id, views)
    }
}
