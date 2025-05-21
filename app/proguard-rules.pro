# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.


# Keep all classes in the com.behnamuix.tenserpingx package and its subpackages
# This is a general rule that might be too broad, but ensures your core app logic isn't stripped/obfuscated.
# Consider being more specific if you encounter further issues or want more aggressive shrinking.
-keep class com.behnamuix.tenserpingx.** { *; }

# --- Android Components ---
# Keep all public classes that extend Activity, Service, BroadcastReceiver, ContentProvider, Application, Fragment
# This is crucial for Android's component model to work correctly.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Fragment
-keep public class * extends android.webkit.WebView
-keep public class * extends androidx.fragment.app.Fragment

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep annotations
-keepattributes Signature
-keepattributes *Annotation*

# --- Kotlin Specific Rules ---
# Keep Kotlin metadata for proper reflection and interoperability
-keep class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.Continuation
-keep class kotlin.jvm.internal.** { *; }
-keep class kotlin.reflect.** { *; }

# --- Third-party Libraries ---

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn com.squareup.picasso.**
-keep class com.squareup.picasso.** { *; }
-keep interface com.squareup.picasso.** { *; }

# Lottie (for LottieAnimationView)
# Lottie uses reflection to animate properties.
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }
-keep interface com.airbnb.lottie.** { *; }

# Myket Billing Client (ir.myket.billingclient.util.Purchase, ir.myket.billingclient.util.Security, IabHelper)
# Billing libraries often use reflection and need their classes/methods kept.
-dontwarn ir.myket.**
-keep class ir.myket.** { *; }
-keep interface ir.myket.** { *; }

# Retrofit (com.behnamuix.tenserpingx.Retrofit.ApiResponse, ApiResponseCheckVerifyJson, RetrofitClient)
# Retrofit uses reflection for interfaces and data models.
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn com.google.gson.** # Assuming Gson is used with Retrofit for JSON parsing
-keep class retrofit2.Retrofit
-keep class retrofit2.Call
-keep class retrofit2.Callback
-keep class retrofit2.http.** { *; }

-keep class com.behnamuix.tenserpingx.Retrofit.** { *; } # Your API interfaces and data models
-keep class com.behnamuix.tenserpingx.Retrofit.ApiResponse { *; }
-keep class com.behnamuix.tenserpingx.Retrofit.ApiResponseCheckVerifyJson { *; }

# Keep model classes used by Retrofit/Gson from obfuscation (adjust package as needed)
-keep class com.behnamuix.tenserpingx.Retrofit.model.** { *; } # If you have a separate model package

# Material Design Components (com.google.android.material.button.MaterialButton)
# Usually handled by Android Gradle Plugin, but good to be explicit if issues arise.
-keep class com.google.android.material.** { *; }

# Lifecycle (androidx.lifecycle.lifecycleScope)
# Generally handled by AndroidX, but good to be explicit.
-keep class androidx.lifecycle.** { *; }

# androidx.activity.enableEdgeToEdge, androidx.appcompat.app.AppCompatActivity, etc.
# These are typically covered by default AndroidX rules, but including them for completeness.
-keep class androidx.activity.** { *; }
-keep class androidx.appcompat.** { *; }
-keep class androidx.core.** { *; }
-keep class androidx.constraintlayout.** { *; }

# Keep enums for correct serialization/deserialization if you are using them anywhere
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep classes with default constructors that are instantiated dynamically
# حفظ همه سازنده‌ها (عمومی، خصوصی و حفاظت‌شده)
-keepclasseswithmembers class * {
    <init>(...);
}

# فقط برای کلاس‌های خاص پکیج شما
-keepclasseswithmembers class com.behnamuix.tenserpingx.** {
    <init>();
}

# Keep fields of Parcelable classes if you have any
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# To avoid issues with custom classes used in layouts (like custom views)
-keep class com.behnamuix.tenserpingx.MyTools.MoToast { *; }
-keep class com.behnamuix.tenserpingx.Dialog.HistoryDialogFragment { *; }
-keep class com.behnamuix.tenserpingx.Dialog.NoInternetDialogFragment { *; }
-keep class com.behnamuix.tenserpingx.Network.NetworkCheck { *; }
-keep class com.behnamuix.tenserpingx.Network.InternetSpeedTester { *; }
-keep class com.behnamuix.tenserpingx.Network.UploadTester { *; }
-keep class com.behnamuix.tenserpingx.Network.IpAddress.getIpAddress { *; }
-keep class com.behnamuix.tenserpingx.AndroidWraper.DeviceInfo { *; }
-keep class com.behnamuix.tenserpingx.MyTools.Object.ConverterX { *; }
-keep class com.behnamuix.tenserpingx.MyTools.Object.VpnChecker { *; }
-keep class com.behnamuix.tenserpingx.MyketRate.MyketRate { *; }
-keep class com.behnamuix.tenserpingx.util.IabHelper { *; }
