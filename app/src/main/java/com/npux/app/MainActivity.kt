package com.npux.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasNnapi = Build.VERSION.SDK_INT >= Build.VERSION_CODE.O_MR1 && packageManager.hasSystemFeature(PackageManager.FEATURE_NEURAL_NETWORKS)

        val hasVulkan = packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)
        
        val openCLPaths = arrayOf(
            "/system/vendor/lib64/libOpenCL.so",
            "/vendor/lib64/libOpenCL.so",
            "/system/lib64/libOpenCL.so",
            "/system/vendor/lib/libOpenCL.so"
        )
        val hasOpenCL = openCLPaths.any { File(it).exists() }

        val text = TextView(this)

        val qnnPaths = arrayOf(
            "/vendor/lib64/libQnnHtpStub.so",
            "/vendor/lib64/libQnnHtpAltPrepStub.so",
            "/vendor/lib64/libadsprpc.so",
            "/vendor/lib64/libQnnCpu.so",
            "/vendor/lib64/libQnnHtp.so"
        )
        val hasQnn = qnnPaths.any { File(it).exists() }

        val snpePaths = arrayOf(
            "/vendor/lib64/libSNPE.so",
            "/system/vendor/lib64/libSNPE.so"
        )
        val hasSnpe = snpePaths.any { File(it).exists() }

        val dspPaths = arrayOf(
            "/dev/subsys_adsp",
            "/dev/cdsp1",
            "/vendor/dsp/cdsp",
            "/system/vendor/dsp/cdsp"
        )
        val hasDsp = dspPaths.any { File(it).exists() }

        val platform = getSystemProperty("ro.board.platform")

        text.text = """
            NPU-X

            Qualcomm AI / NPU Diagnostic

            Device:
            Model: ${android.os.Build.MODEL} // Model ou Device
            Manufacturer: ${android.os.Build.MANUFACTURER}
            Hardware: ${android.os.Build.HARDWARE}
            Board: ${android.os.Build.BOARD}
            
            Android:
            Android: ${android.os.Build.VERSION.RELEASE}
            SDK: ${android.os.Build.VERSION.SDK_INT}
            
            AI APIs:
            NNAPI: ${if (hasNnapi) "AVAILABLE" else "UNAVAILABLE"}
            Vulkan: ${if (hasVulkan) "AVAILABLE" else "UNAVAILABLE"}
            OpenCL: ${if (hasOpenCL) "AVAILABLE" else "UNAVAILABLE"}
            
            Qualcomm
             Qualcomm platform: ${if (platform.isNotEmpty()) platform else "UNKNOWN"}
            QNN Lib: ${if (hasQnn) "DETECTED" else "NOT DETECTED"}
            SNPE: ${if (hasSnpe) "DETECTED" else "NOT DETECTED"}
            ADSP/HTP infrastructure: ${if (hasDsp) "DETECTED" else "NOT DETECTED"}
            
            STATUS
            AI infrastructure: ${if (hasQnn || hasSnpe || hasDsp || hasNnapi) "DETECTED" else "NOT DETECTED"}
            NPU inference: NOT TESTED
            
        """.trimIndent()

        text.textSize = 18f
        text.setPadding(32, 32, 32, 32)

        setContentView(text)
    }
    
    private fun getSystemProperty(name: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("getprop", name))
            process.inputStrem.bufferedReader().use { it.readText().trim() }
        } cath (e: Exception) { "" }
    }
}
