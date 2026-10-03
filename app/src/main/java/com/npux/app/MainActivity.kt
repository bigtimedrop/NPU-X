package com.npux.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

data class NnapiDevice(
	val name: String,
	val type: Int,
	val version: String,
	val featureLevel: Int,
) {
	fun getTypeName(): String = when (type) {
		1 -> "UNKNOWN"
		2 -> "CPU"
		3 -> "GPU"
		4 -> "ACCELERATOR (NPU/DSP)"
		else -> "OTHER ($type)"
	}
}

class MainActivity : Activity() {

    private external fun getNnapiDevices(): Array<NnapiDevice>
    
    companion object {
        private var isNativeLoaded = false
        init {
            try {
                System.loadLibrary("nnapi_devices")
                isNativeLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                isNativeLoaded = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //  Variaveis usadas na tela
        val hasNnapi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
        val hasVulkan = packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)
        
        val openCLPaths = arrayOf(
            "/system/vendor/lib64/libOpenCL.so",
            "/vendor/lib64/libOpenCL.so",
            "/system/lib64/libOpenCL.so",
            "/system/vendor/lib/libOpenCL.so"
        )
        val hasOpenCL = openCLPaths.any { File(it).exists() }
        
        val text = TextView(this)
        
        val qnnPaths = arrayOf( //Possiveis caminhos pro QNN
            "/vendor/lib64/libQnnHtpStub.so",
            "/vendor/lib64/libQnnHtpAltPrepStub.so",
            "/vendor/lib64/libadsprpc.so",
            "/vendor/lib64/libQnnCpu.so",
            "/vendor/lib64/libQnnHtp.so"
        )
        val hasQnn = qnnPaths.any { File(it).exists() }
        
        val snpePaths = arrayOf( // Possiveis caminhos pra SNPE
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

	val nnapiDeviceText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
		if (isNativeLoaded) {
			try {
				val devices = getNnapiDevices()
				if (devices.isNotEmpty()) {
					devices.joinToString(separator = "\n") { dev ->
						" • ${dev.name}\n" +
						"   Type: ${dev.getTypeName()}\n" +
						"   Version: ${dev.version}\n" +
						"   Feature Level: ${dev.featureLevel}"
					}
				} else {"  No NNAPI devices reported"}
			} catch (e: Exception) {"Error querying NNAPI devices: ${e.message}"}
		} else {
			"  Native enumeration not available in this build\n  (Library libnnapi_devices.so not found)"
		}
	} else {"  NNAPI Device Enumeration requires Android 10+ (API 29)"}
        
        text.text = """
            NPU-X
            Qualcomm AI / NPU Diagnostic
            Device:
             Model: ${android.os.Build.MODEL}
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

	    NNAPI ACCELERATORS:
	    $nnapiDeviceText
        """.trimIndent()
        
        text.textSize = 18f
        text.setPadding(32, 32, 32, 32)
        setContentView(text)
    }

    private fun getSystemProperty(name: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("getprop", name))
            // Correção 2: inputStream escrito corretamente
            process.inputStream.bufferedReader().use { it.readText().trim() }
        // Correção 3: catch escrito corretamente
        } catch (e: Exception) { "" }
    }
}
