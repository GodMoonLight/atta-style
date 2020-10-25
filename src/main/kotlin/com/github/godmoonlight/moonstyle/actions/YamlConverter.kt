package com.github.godmoonlight.moonstyle.actions

import com.github.godmoonlight.moonstyle.settings.ConfigUtil
import com.github.godmoonlight.moonstyle.settings.ToYamlConfig
import com.github.godmoonlight.moonstyle.utils.ProjectUtil
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class YamlConverter : AnAction() {

    companion object {
        private var notificationGroup: NotificationGroup = NotificationGroup(
            "Java2Yaml.NotificationGroup",
            NotificationDisplayType.BALLOON,
            true
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.dataContext.getData(CommonDataKeys.EDITOR)!!
        val project: Project = editor.project!!

        val selectedClass: PsiClass = ProjectUtil.getPsiClassFromContext(e)!!
        val toJson: ToYamlConfig = ConfigUtil.get().toYamlConfig

        val kv: KV<String, Any> =
            FieldResolver(false, toJson.randomValue, toJson.enumValues)
                .getFields(selectedClass)
        val json: String = kv.toYaml(selectedClass.qualifiedName)
        val selection = StringSelection(json)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
        val message = "Convert " + selectedClass.name + " to Yaml success, copied to clipboard."
        val success = notificationGroup.createNotification(message, NotificationType.INFORMATION)
        Notifications.Bus.notify(success, project)
    }
}
