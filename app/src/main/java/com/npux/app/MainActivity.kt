package com.npux.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)

        text.text = """
            NPU-X

            Qualcomm AI / NPU Diagnostic

            Device: ${android.os.Build.MODEL}
            Manufacturer: ${android.os.Build.MANUFACTURER}
            Android: ${android.os.Build.VERSION.RELEASE}
            SDK: ${android.os.Build.VERSION.SDK_INT}
            Hardware: ${android.os.Build.HARDWARE}
            Board: ${android.os.Build.BOARD}
        """.trimIndent()

        text.textSize = 18f
        text.setPadding(32, 32, 32, 32)

        setContentView(text)
    }
}
