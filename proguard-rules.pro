-dontwarn kotlinx.serialization.**

-dontwarn sun.font.CFont
-dontwarn sun.swing.SwingUtilities2$AATextInfo
-dontwarn net.miginfocom.swing.MigLayout

-dontnote kotlinx.serialization.**
-dontnote META-INF.**
-dontnote kotlinx.serialization.internal.PlatformKt

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep all serializable classes with their @Serializable annotation
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep serializers
-keepclasseswithmembers class **$$serializer {
    static **$$serializer INSTANCE;
}


# Keep serializable classes and their properties
-if @kotlinx.serialization.Serializable class **
-keep class <1> {
    static <1>$Companion Companion;
}

# Keep specific serializer classes
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serialization descriptors
-keep class kotlinx.serialization.descriptors.** { *; }

# Specifically keep AppSettings and its serializer
-keep class AppSettings { *; }
-keep class AppSettings$$serializer { *; }

# Keep local data classes and enums
-keep class Snippet { *; }
-keep class Snippet$$serializer { *; }
-keep class UiState { *; }
-keep class Languages { *; }
-keep class MainViewModel { *; }
-keep class Database { *; }

# Keep syntax highlighting library classes
-keep class dev.snipme.highlights.** { *; }
-keep class dev.snipme.highlights.model.** { *; }
-keep class dev.snipme.highlights.Highlights { *; }
-keep class dev.snipme.highlights.model.SyntaxLanguage { *; }
-keep class dev.snipme.highlights.model.SyntaxThemes { *; }

# Don't warn about syntax highlighting library
-dontwarn dev.snipme.highlights.**
-dontwarn dev.snipme.kodeview.**

# Keep extension functions for Languages enum
-keep class LanguagesKt { *; }

# Keep ViewModels and their methods
-keepclassmembers class **ViewModel {
    public <methods>;
}

# Keep Compose navigation classes
-dontwarn moe.tlaster.precompose.**
-keep class moe.tlaster.precompose.** { *; }