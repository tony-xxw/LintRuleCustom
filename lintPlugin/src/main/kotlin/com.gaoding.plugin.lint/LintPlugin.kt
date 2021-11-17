package com.gaoding.plugin.lint

import org.gradle.api.Plugin
import org.gradle.api.Project

class LintPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("greeting") {
            print("1111111111")
        }



    }

}