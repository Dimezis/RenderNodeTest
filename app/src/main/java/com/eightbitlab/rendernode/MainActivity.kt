package com.eightbitlab.rendernode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        findViewById<SnapshotView>(R.id.snapshotView).apply {
            init(window.decorView as ViewGroup)
        }

        findViewById<TextView>(R.id.text)
            .animate()
            .setDuration(10_000)
            .x(800f)
            .y(1200f)
    }
}

class SnapshotView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private lateinit var root: ViewGroup
    private lateinit var reader: ImageReader

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val img = reader.acquireNextImage() ?: return
        img.hardwareBuffer?.use {
            val hwBitmap = Bitmap.wrapHardwareBuffer(it, null)!!
            canvas.drawBitmap(hwBitmap, 0f, 0f, null)
            img.close()
        }
    }

    fun init(root: ViewGroup) {
        this.root = root

        root.doOnLayout {
            reader = ImageReader.newInstance(
                width,
                height,
                PixelFormat.RGBA_8888,
                1,
                HardwareBuffer.USAGE_GPU_COLOR_OUTPUT or HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE
            )
            root.viewTreeObserver.addOnPreDrawListener {
                takeSnapshot()
                true
            }
        }
    }

    private fun takeSnapshot() {
        visibility = INVISIBLE
        val canvas = reader.surface.lockHardwareCanvas()
        root.draw(canvas)
        reader.surface.unlockCanvasAndPost(canvas)
        visibility = VISIBLE
    }
}