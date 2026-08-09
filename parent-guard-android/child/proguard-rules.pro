# Add project specific ProGuard rules here.

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Keep model classes (for JSON serialization)
-keep class com.parentguard.child.data.model.** { *; }
-keep class com.parentguard.parent.data.model.** { *; }

# Keep service entry points
-keep class com.parentguard.child.service.** { *; }
-keep class com.parentguard.child.receiver.** { *; }
