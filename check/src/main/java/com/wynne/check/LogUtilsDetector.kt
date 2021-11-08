package com.wynne.check

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

class LogUtilsDetector : Detector(), Detector.UastScanner {

    companion object {
        val IMPLEMENTATION = Implementation(LogUtilsDetector::class.java, Scope.JAVA_FILE_SCOPE)

        val ISSUE = Issue.create(
            "LogError",
            "请使用LogUtils来打印日志",
            "请使用LogUtils来打印日志,避免release输出信息",
            Category.CORRECTNESS,
            7,
            Severity.FATAL,
            IMPLEMENTATION
        )
    }

    override fun getApplicableMethodNames(): List<String>? {
        return Collections.singletonList("d,e,i,w,wtf,print")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!context.evaluator.isMemberInClass(method, "android.util.Log")) {
            return
        }
        reportError(context, node)
    }


    private fun reportError(context: JavaContext, node: UCallExpression) {
        context.report(
            ISSUE,
            node,
            context.getCallLocation(node, includeReceiver = false, includeArguments = false),
            "不能使用Log类进行日志输出"
        )
    }
}