package com.github.godmoonlight.moonstyle.services

import com.github.godmoonlight.moonstyle.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
