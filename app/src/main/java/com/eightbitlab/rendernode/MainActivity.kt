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
            init(window.decorView as ViewGroup)
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
        root.draw(canvas)
        drawingSnapshot = false
        node.endRecording()
        // This is causing a constant redraw, but it doesn't really matter
        invalidate()
    }
}