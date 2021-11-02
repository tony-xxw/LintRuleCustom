package com.wynne.check

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element
import java.util.*

class FixOrientationTransDetector : Detector(), XmlScanner {
    companion object {
        val IMPLEMENTATION = Implementation(
            FixOrientationTransDetector::class.java,
            EnumSet.of(Scope.MANIFEST, Scope.ALL_RESOURCE_FILES)
        )
        val ISSUE = Issue.create(
            "FixOrientationTransError",
            "不要在 AndroidManifest.xml 文件里同时设置方向和透明主题",
            "Activity 同时设置方向和透明主题在 Android 8.0 手机会 Crash",
            Category.CORRECTNESS, 8, Severity.ERROR, IMPLEMENTATION
        )
    }

    private val mThemeMap = mutableMapOf<ElementEntity, String>()


    override fun getApplicableElements(): Collection<String> {
        return listOf(SdkConstants.TAG_ACTIVITY, SdkConstants.TAG_STYLE)
    }

    override fun visitElement(context: XmlContext, element: Element) {
        when (element.tagName) {
            SdkConstants.TAG_ACTIVITY -> {
                if (isFixedOrientation(element)) {
                    val theme =
                        element.getAttributeNS(SdkConstants.ANDROID_URI, SdkConstants.ATTR_THEME)
                    if ("@style/Theme.AppTheme.Transparent" == theme) {
                        reportError(context, element)
                    } else {
                        mThemeMap[ElementEntity(context, element)] = theme.substring(theme.indexOf("/") + 1)
                    }
                }
            }
            SdkConstants.TAG_STYLE-> {
                val styleName = element.getAttribute(SdkConstants.ATTR_NAME)
                mThemeMap.forEach { (elementEntity, theme) ->
                    if(theme == styleName){
                        if(isTranslucentOrFloating(element)){
                            reportError(elementEntity.context,elementEntity.element)
                        }else if(element.hasAttribute(SdkConstants.ATTR_PARENT)){
                            mThemeMap[elementEntity] = element.getAttribute(SdkConstants.ATTR_PARENT)
                        }
                    }
                }
            }

        }
    }

    private fun isFixedOrientation(element: Element): Boolean =
        when (element.getAttributeNS(SdkConstants.ANDROID_URI, "screenOrientation")) {
            "landscape", "sensorLandscape", "reverseLandscape", "userLandscape", "portrait", "sensorPortrait", "reversePortrait", "userPortrait", "locked" -> true
            else -> false
        }

    fun isTranslucentOrFloating(element: Element): Boolean {
        var child = element.firstChild
        while (child != null && element.nextSibling != null) {
            if (child is Element && SdkConstants.TAG_ITEM == child.tagName && child.firstChild != null && SdkConstants.VALUE_TRUE == child.firstChild.nodeValue) {
                when (child.getAttribute(SdkConstants.ATTR_NAME)) {
                    "android:windowIsTranslucent",
                    "android:windowSwipeToDismiss",
                    "android:windowIsFloating" -> return true
                }
            }
            child = element.nextSibling
        }
        return "Theme.AppTheme.Transparent" == element.getAttribute(SdkConstants.ATTR_PARENT)
    }


    private fun reportError(context: XmlContext, element: Element) {
        context.report(
            ISSUE,
            element,
            context.getLocation(element),
            "请不要在 AndroidManifest.xml 文件里同时设置方向和透明主题"
        )
    }
}