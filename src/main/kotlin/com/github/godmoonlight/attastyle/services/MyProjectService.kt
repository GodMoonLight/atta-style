package com.github.godmoonlight.attastyle.services

import com.intellij.openapi.project.Project
import com.github.godmoonlight.attastyle.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
