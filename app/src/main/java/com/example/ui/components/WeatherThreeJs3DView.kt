package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.BiomeType
import com.example.data.model.WeatherCondition
import com.example.ui.theme.MonsoonCyan
import org.json.JSONObject

private class OrbitableWebView(context: Context) : WebView(context) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> parent?.requestDisallowInterceptTouchEvent(false)
        }
        return super.onTouchEvent(event)
    }
}

private class ThreeJsWeatherBridge(
    private val onReady: () -> Unit,
    private val onFallback: (String) -> Unit,
) {
    @JavascriptInterface
    fun onSceneReady() {
        onReady()
    }

    @JavascriptInterface
    fun onSceneFallback(reason: String) {
        onFallback(reason)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WeatherThreeJs3DView(
    condition: WeatherCondition,
    biome: BiomeType,
    isDay: Boolean,
    windSpeedKmh: Double,
    windDirectionDeg: Int,
    cloudCoverPercent: Int,
    tempCelsius: Double,
    cityName: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var useThreeJsEngine by remember { mutableStateOf(true) }
    var isThreeJsReady by remember { mutableStateOf(false) }
    var threeJsUnavailable by remember { mutableStateOf(false) }

    val weatherPayloadJson = remember(
        condition,
        biome,
        isDay,
        windSpeedKmh,
        windDirectionDeg,
        cloudCoverPercent,
        tempCelsius,
        cityName,
    ) {
        JSONObject().apply {
            put("condition", condition.name)
            put("biome", biome.name)
            put("isDay", isDay)
            put("windSpeedKmh", windSpeedKmh)
            put("windDirectionDeg", windDirectionDeg)
            put("cloudCoverPercent", cloudCoverPercent)
            put("tempCelsius", tempCelsius)
            put("cityName", cityName)
        }.toString()
    }

    val webView = remember {
        OrbitableWebView(context).apply {
            setBackgroundColor(AndroidColor.TRANSPARENT)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                @Suppress("DEPRECATION")
                allowFileAccessFromFileURLs = true
                @Suppress("DEPRECATION")
                allowUniversalAccessFromFileURLs = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mediaPlaybackRequiresUserGesture = false
            }

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.evaluateJavascript(
                        "if (window.updateWeatherState) { window.updateWeatherState($weatherPayloadJson); }",
                        null,
                    )
                }
            }

            addJavascriptInterface(
                ThreeJsWeatherBridge(
                    onReady = {
                        post { isThreeJsReady = true }
                    },
                    onFallback = {
                        post {
                            threeJsUnavailable = true
                            isThreeJsReady = false
                        }
                    },
                ),
                "AndroidBridge",
            )

            loadUrl("file:///android_asset/weather_3d_scene.html")
        }
    }

    DisposableEffect(lifecycleOwner, webView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView.stopLoading()
            webView.removeJavascriptInterface("AndroidBridge")
            webView.destroy()
        }
    }

    LaunchedEffect(weatherPayloadJson, isThreeJsReady, useThreeJsEngine) {
        if (useThreeJsEngine && isThreeJsReady) {
            webView.evaluateJavascript(
                "if (window.updateWeatherState) { window.updateWeatherState($weatherPayloadJson); }",
                null,
            )
        }
    }

    val showThreeJsCanvas = useThreeJsEngine && isThreeJsReady && !threeJsUnavailable
    val threeJsAlpha by animateFloatAsState(
        targetValue = if (showThreeJsCanvas) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "threejs_fade",
    )

    Box(
        modifier = modifier
            .semantics {
                contentDescription = "$cityName 3D ${condition.displayName} visualization"
            }
            .testTag("weather_3d_visualizer"),
    ) {
        // Native 3D Stage serves as instant zero-latency layer & offline fallback
        if (!showThreeJsCanvas) {
            Weather3DStage(
                condition = condition,
                biome = biome,
                isDay = isDay,
                windSpeedKmh = windSpeedKmh,
                cityName = cityName,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Three.js WebGL 3D Engine
        if (useThreeJsEngine && !threeJsUnavailable) {
            AndroidView(
                factory = { webView },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(threeJsAlpha)
                    .testTag("threejs_webgl_view"),
            )
        }

        // Engine Status & Switcher Chip
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.34f),
                border = BorderStroke(1.dp, MonsoonCyan.copy(alpha = 0.38f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = !threeJsUnavailable) {
                        useThreeJsEngine = !useThreeJsEngine
                    }
                    .testTag("threejs_engine_toggle"),
            ) {
                Text(
                    text = when {
                        threeJsUnavailable -> "Native 3D"
                        showThreeJsCanvas -> "Three.js WebGL"
                        useThreeJsEngine -> "Loading 3D…"
                        else -> "Native 3D"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MonsoonCyan,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
            }
        }

        if (showThreeJsCanvas) {
            Surface(
                color = Color.Black.copy(alpha = 0.28f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
            ) {
                Text(
                    text = "Drag to orbit 3D · Double-tap to reset",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.84f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
            }
        }
    }
}
