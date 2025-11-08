# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Enhanced ProGuard Rules for Sketchware Plus v7.1.0
# Last Updated: November 2024

# ================================
# General Optimization Settings
# ================================

# Optimize and shrink code
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signatures for reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ================================
# Android Framework
# ================================

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# Keep View constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep onClick methods
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# Keep Serializable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ================================
# AndroidX and Material Components
# ================================

# Keep AndroidX
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Keep Material Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Keep ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding {
    public static * bind(android.view.View);
    public static * inflate(android.view.LayoutInflater);
}

# ================================
# Kotlin
# ================================

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ================================
# Firebase (if enabled)
# ================================

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ================================
# Reflection-based libraries
# ================================

# Keep Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ================================
# Sketchware Plus Specific
# ================================

# Keep all pro.sketchware classes (main app package)
-keep class pro.sketchware.** { *; }

# Keep compiler classes
-keep class a.a.a.ProjectBuilder { *; }
-keep class a.a.a.Ix { *; }
-keep class a.a.a.Jx { *; }
-keep class a.a.a.Lx { *; }
-keep class a.a.a.Ox { *; }

# Keep mod package
-keep class mod.** { *; }

# Keep build system classes
-keep class mod.jbk.build.** { *; }
-keep class mod.pranav.build.** { *; }

# Keep editor classes
-keep class mod.jbk.editor.** { *; }

# Keep utility classes with performance monitoring
-keep class pro.sketchware.utility.BuildCacheManager { *; }
-keep class pro.sketchware.utility.PerformanceMonitor { *; }

# ================================
# Build Tools and Compilers
# ================================

# Keep Eclipse Compiler for Java
-keep class org.eclipse.jdt.internal.compiler.** { *; }
-dontwarn org.eclipse.jdt.internal.compiler.**

# Keep Kotlin compiler
-keep class org.jetbrains.kotlin.** { *; }
-dontwarn org.jetbrains.kotlin.**

# Keep R8 compiler
-keep class com.android.tools.r8.** { *; }
-dontwarn com.android.tools.r8.**

# Keep DEX tools
-keep class mod.agus.jcoderz.dx.** { *; }
-keep class mod.agus.jcoderz.dex.** { *; }

# ================================
# Code Editor and Syntax Highlighting
# ================================

# Keep Sora Editor
-keep class io.github.rosemoe.sora.** { *; }
-dontwarn io.github.rosemoe.sora.**

# Keep TextMate
-keep class org.eclipse.tm4e.** { *; }
-dontwarn org.eclipse.tm4e.**

# ================================
# Security and Obfuscation
# ================================

# Enable aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ================================
# Crash Reporting
# ================================

# Keep stack traces readable
-keepattributes SourceFile,LineNumberTable

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# ================================
# Native Methods
# ================================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# ================================
# Enum Classes
# ================================

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================
# Resource IDs
# ================================

# Keep R class
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ================================
# Additional Rules
# ================================

# Remove debug code
-assumenosideeffects class pro.sketchware.utility.PerformanceMonitor {
    public void logMemoryUsage(...);
}

# Keep BuildConfig
-keep class pro.sketchware.BuildConfig { *; }

# Suppress warnings for missing classes
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.**
