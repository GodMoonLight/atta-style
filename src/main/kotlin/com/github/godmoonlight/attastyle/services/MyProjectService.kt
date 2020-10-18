package com.github.godmoonlight.attastyle.services

import com.github.godmoonlight.attastyle.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
