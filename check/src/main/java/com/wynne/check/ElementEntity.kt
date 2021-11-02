package com.wynne.check

import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Element

data class ElementEntity(val context: XmlContext, val element: Element)