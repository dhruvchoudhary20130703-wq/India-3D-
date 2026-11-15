# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep WebView JavaScript bridge methods intact if R8/ProGuard minification is enabled
-keepclassmembers class com.example.ui.components.ThreeJsWeatherBridge {
    @android.webkit.JavascriptInterface <methods>;
    public *;
}

# Keep Retrofit & Moshi Kotlin reflection DTOs intact if minification is enabled
-keep class com.example.data.remote.** { *; }
-keepclassmembers class com.example.data.remote.** { *; }
-keepattributes RuntimeVisibleAnnotations,Signature,InnerClasses,EnclosingMethod
