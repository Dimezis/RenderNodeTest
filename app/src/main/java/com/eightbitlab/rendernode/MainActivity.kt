package com.eightbitlab.rendernode

import android.content.Context
import android.graphics.Canvas
import android.graphics.RenderNode
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        findViewById<SnapshotViewParent>(R.id.snapshotViewParent).apply {
            init(window.decorView as ViewGroup)
        }

        findViewById<TextView>(R.id.text)
            .animate()
            .setDuration(1_000)
            .x(800f)
            .y(1200f)
    }
}

class SnapshotViewParent(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val node = RenderNode("lol")
    private var shouldSkipDrawing = false
    private lateinit var root: ViewGroup

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        if (shouldSkipDrawing) {
            return false
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    fun init(root: ViewGroup) {
        this.root = root
        (getChildAt(0) as SnapshotView).node = node

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

        shouldSkipDrawing = true
        root.draw(canvas)
        shouldSkipDrawing = false
        node.endRecording()

        getChildAt(0).invalidate()
        // FIXME Invalidating itself still crashes and causes stack overflow
        // invalidate()
    }
}

class SnapshotView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    var node: RenderNode? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRenderNode(node!!)
    }
}