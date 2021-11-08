package com.wynne.check

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

class MyIssueRegistry :IssueRegistry() {

    override val issues: List<Issue>
        get() = mutableListOf<Issue>().apply {
            add(FixOrientationTransDetector.ISSUE)
            add(ParseColorDetector.ISSUE)
            add(LogUtilsDetector.ISSUE)
        }

    override val api: Int
        get() = 5
}