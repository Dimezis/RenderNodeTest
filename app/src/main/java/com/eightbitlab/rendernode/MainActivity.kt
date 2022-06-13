package com.eightbitlab.rendernode

import android.content.Context
import android.graphics.Canvas
import android.graphics.RenderNode
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
            // Works with the direct parent as the root view + workaround in `takeSnapshot`
            init(parent as ViewGroup)
            // Crashes with the decorView as root.
            // If the root is NOT a direct parent of SnapshotView, stack overflow happens on RenderThread
//            init(window.decorView as ViewGroup)
        }

        findViewById<TextView>(R.id.text)
            .animate()
            .setDuration(1_000)
            .x(800f)
            .y(1200f)
    }
}

class SnapshotView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val node = RenderNode("lol")
    private var drawingSnapshot = false
    private lateinit var root: ViewGroup

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Guarded by `drawingSnapshot` to prevent recursive drawing
        if (!drawingSnapshot && node.hasDisplayList()) {
            canvas.drawRenderNode(node)
        }
    }

    fun init(root: ViewGroup) {
        this.root = root

        root.doOnLayout {
            node.setPosition(0, 0, width, height)
            root.viewTreeObserver.addOnPreDrawListener {
                takeSnapshot()
                true
            }
        }
    }


    private fun takeSnapshot() {
        val canvas = node.beginRecording()
        drawingSnapshot = true
        visibility = GONE
        root.draw(canvas)
        visibility = VISIBLE
        drawingSnapshot = false
        node.endRecording()
    }
}