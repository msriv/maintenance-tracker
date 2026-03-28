# Keep Room entities
-keep class com.moto.tracker.data.local.entity.** { *; }

# Keep Hilt generated classes
-keep class * extends dagger.hilt.android.internal.managers.** { *; }

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer { *; }

# Keep WorkManager workers
-keep class com.moto.tracker.data.worker.** { *; }

# Vico charts
-keep class com.patrykandpatrick.vico.** { *; }
