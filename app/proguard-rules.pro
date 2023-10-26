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
#-renamesourcefileattribute SourceFile

#-keep class com.aarogyaforworkers.aarogyaFDC.** {
#    *;
#}
#
#-keepclassmembers class com.aarogyaforworkers.aarogyaFDC.** {
#    *;
#}

-keep class com.aarogyaforworkers.aarogyaFDC.MainActivity
-keep class com.aarogyaforworkers.aarogyaFDC.VideoCall.VideoConferencing

-dontwarn com.aarogyaforworkers.**
-keep class com.aarogyaforworkers.awsapi.models.**{*;}



-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-keep class com.owlbuddy.www.countrycodechooser.** {*;}
-keep class com.qingniu.**{*;}
-keep class com.qn.device.**{*;}
-keep class com.zego.**{*;}
-keep class com.zegocloud.**{*;}


# Firebase
-keep class com.google.android.gms.** { *; }

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

# Only necessary if you downloaded the SDK jar directly instead of from maven.
-keep class com.shaded.fasterxml.jackson.** { *; }



-dontwarn aQute.bnd.annotation.Resolution
-dontwarn aQute.bnd.annotation.spi.ServiceProvider
-dontwarn com.heytap.msp.push.HeytapPushManager
-dontwarn com.heytap.msp.push.callback.ICallBackResultService
-dontwarn com.heytap.msp.push.mode.DataMessage
-dontwarn com.heytap.msp.push.service.DataMessageCallbackService
-dontwarn com.huawei.hms.aaid.HmsInstanceId
-dontwarn com.huawei.hms.common.ApiException
-dontwarn com.huawei.hms.push.HmsMessageService
-dontwarn com.huawei.hms.push.RemoteMessage$Notification
-dontwarn com.huawei.hms.push.RemoteMessage
-dontwarn com.oracle.svm.core.annotate.AutomaticFeature
-dontwarn com.oracle.svm.core.annotate.Delete
-dontwarn com.oracle.svm.core.annotate.Substitute
-dontwarn com.oracle.svm.core.annotate.TargetClass
-dontwarn com.oracle.svm.core.configure.ResourcesRegistry
-dontwarn com.vivo.push.IPushActionListener
-dontwarn com.vivo.push.PushClient
-dontwarn com.vivo.push.model.UPSNotificationMessage
-dontwarn com.vivo.push.model.UnvarnishedMessage
-dontwarn com.vivo.push.sdk.OpenClientPushMessageReceiver
-dontwarn com.vivo.push.util.VivoPushException
-dontwarn com.xiaomi.mipush.sdk.MiPushClient
-dontwarn com.xiaomi.mipush.sdk.MiPushCommandMessage
-dontwarn com.xiaomi.mipush.sdk.MiPushMessage
-dontwarn com.xiaomi.mipush.sdk.PushMessageReceiver
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn javax.naming.InvalidNameException
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.ldap.LdapName
-dontwarn javax.naming.ldap.Rdn
-dontwarn javax.xml.stream.FactoryConfigurationError
-dontwarn javax.xml.stream.Location
-dontwarn javax.xml.stream.XMLEventFactory
-dontwarn javax.xml.stream.XMLInputFactory
-dontwarn javax.xml.stream.XMLOutputFactory
-dontwarn javax.xml.stream.XMLResolver
-dontwarn javax.xml.stream.XMLStreamConstants
-dontwarn javax.xml.stream.XMLStreamException
-dontwarn javax.xml.stream.XMLStreamReader
-dontwarn javax.xml.stream.XMLStreamWriter
-dontwarn javax.xml.stream.util.StreamReaderDelegate
-dontwarn javax.xml.stream.util.XMLEventAllocator
-dontwarn org.graalvm.nativeimage.ImageSingletons
-dontwarn org.graalvm.nativeimage.hosted.Feature$BeforeAnalysisAccess
-dontwarn org.graalvm.nativeimage.hosted.Feature
-dontwarn org.ietf.jgss.GSSContext
-dontwarn org.ietf.jgss.GSSCredential
-dontwarn org.ietf.jgss.GSSException
-dontwarn org.ietf.jgss.GSSManager
-dontwarn org.ietf.jgss.GSSName
-dontwarn org.ietf.jgss.Oid
-dontwarn javax.servlet.ServletContextEvent
-dontwarn javax.servlet.ServletContextListener
-dontwarn org.apache.avalon.framework.logger.Logger
-dontwarn org.apache.log.Hierarchy
-dontwarn org.apache.log.Logger
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority