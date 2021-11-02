package com.wynne.check


import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTryStatement
import com.intellij.psi.impl.source.tree.java.MethodElement
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UTryExpression
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.kotlin.KotlinUFile
import java.util.*


class ParseColorDetector : Detector(), Detector.UastScanner {

    companion object {
        val IMPLEMENTATION = Implementation(ParseColorDetector::class.java, Scope.JAVA_FILE_SCOPE)

        val ISSUE = Issue.create(
            "ParseColorError", "Color.parseColor 解析可能 crash", "后端下发的色值可能无法解析，导致 crash",
            Category.CORRECTNESS, 8, Severity.ERROR, IMPLEMENTATION
        ).setAndroidSpecific(true)
    }


    override fun getApplicableMethodNames(): List<String>? {
        return Collections.singletonList("parseColor")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!context.evaluator.isMemberInClass(method, "android.graphics.Color")) {
            return
        }
        if (isConstColor(node)) return
        if (isWrappedByTryCatch(node, context)) return
        reportError(context, node)
    }

    fun isConstColor(node: UCallExpression): Boolean {
        return node.valueArguments[0].evaluate().toString().startsWith("#")
    }


    private fun isWrappedByTryCatch(node: UCallExpression, context: JavaContext): Boolean {
        if (context.uastFile is KotlinUFile) {
            return node.uastParent?.getParentOfType<UTryExpression>(UTryExpression::class.java) != null
        }
        var parent = node.sourcePsi!!.parent
        while (parent != null && parent !is MethodElement) {
            if (parent is PsiTryStatement) {
                return true
            }
            parent = parent.parent
        }
        return false
    }

    private fun reportError(context: JavaContext, node: UCallExpression) {
        context.report(
            ISSUE,
            node,
            context.getCallLocation(node, includeReceiver = false, includeArguments = false),
            "Color.parseColor 解析后端下发的值可能导致 crash，请 try catch"
        )
    }

}