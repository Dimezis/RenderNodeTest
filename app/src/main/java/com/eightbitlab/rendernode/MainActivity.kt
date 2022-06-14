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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        findViewById<SnapshotView>(R.id.snapshotView).apply {
            init(window.decorView as ViewGroup)
        }

//        findViewById<TextView>(R.id.text)
//            .animate()
//            .setDuration(10_000)
//            .x(800f)
//            .y(1200f)
    }
}

class SnapshotView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private var drawingSnapshot = false
    private lateinit var root: ViewGroup
    private var reader: ImageReader? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Guarded by `drawingSnapshot` to prevent recursive drawing
        if (!drawingSnapshot) {
            // TODO test how long it takes to create all this stuff
            val img = reader!!.acquireLatestImage() ?: return
            img.hardwareBuffer?.let {
                val hwBitmap = Bitmap.wrapHardwareBuffer(it, null)!!
                canvas.drawBitmap(
                    hwBitmap, 0f, 0f, null
                )
                it.close()
                img.close()
            }
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
        val canvas = reader!!.surface.lockHardwareCanvas()
        drawingSnapshot = true
        root.draw(canvas)
        drawingSnapshot = false
        reader!!.surface.unlockCanvasAndPost(canvas)
        // This is causing a constant redraw, but it doesn't really matter
        invalidate()
    }
}