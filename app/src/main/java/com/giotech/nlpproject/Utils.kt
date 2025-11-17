package com.giotech.nlpproject

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.File

fun <T> LifecycleOwner.lifecycleAwareLazy(initializer: () -> T) = LifecycleAwareLazy(lifecycle, initializer)

class LifecycleAwareLazy<T>(lifecycle: Lifecycle, private val initializer: () -> T)
    : Lazy< T>, LifecycleObserver {

    @Suppress("ClassName")
    private object INITIALIZED_VALUE
    private var _value: Any? = INITIALIZED_VALUE

    @get:Synchronized
    override val value: T
        get() {
            if (_value === INITIALIZED_VALUE) {
                _value = initializer.invoke()
            }
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }

    init {
        lifecycle.addObserver(this)
    }


    override fun isInitialized(): Boolean = _value !== INITIALIZED_VALUE

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        _value = INITIALIZED_VALUE
    }

}

fun View.visible() {
    visibility = View.VISIBLE
}
fun View.gone() {
    visibility = View.GONE
}

fun Uri.toFile(context: Context): File {

    // Get the original file name from Uri
    val fileName: String = queryFileName(context) ?: "temp.pdf"
    val finalName = if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf"

    val tempFile = File(context.cacheDir, finalName)
    return tempFile
}

// Helper extension to get filename from Uri
fun Uri.queryFileName(context: Context): String? {
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            cursor.getString(nameIndex)
        }
        null
    }
}

fun TextView.animateText(newText: String, duration: Long = 200L) {
    this.animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.text = newText
            this.animate()
                .alpha(1f)
                .setDuration(duration)
                .start()
        }.start()
}

fun TextView.typewriterText(
    content: String,
    minDelay: Long = 2L,   // Faster minimum
    maxDelay: Long = 7L   // Faster maximum
) {
    text = ""

    val length = content.length

    // Faster adaptive curve
    val delayPerChar = when {
        length < 40  -> minDelay
        length < 120 -> minDelay + 3
        length < 300 -> minDelay + 5
        else         -> maxDelay
    }

    var index = 0
    val handler = Handler(Looper.getMainLooper())

    val runnable = object : Runnable {
        override fun run() {
            if (index <= content.length) {
                text = content.substring(0, index)
                index++
                handler.postDelayed(this, delayPerChar)
            }
        }
    }

    handler.post(runnable)
}

