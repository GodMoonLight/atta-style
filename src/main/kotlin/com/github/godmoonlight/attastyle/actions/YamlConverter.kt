package com.github.godmoonlight.attastyle.actions

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
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
        val editor = e.dataContext.getData(CommonDataKeys.EDITOR)
        val project = editor!!.project
        val referenceAt =
            e.dataContext.getData(CommonDataKeys.PSI_FILE)!!.findElementAt(editor.caretModel.offset)

        val selectedClass: PsiClass =
            PsiTreeUtil.getContextOfType<PsiElement>(referenceAt, PsiClass::class.java) as PsiClass
        val kv: KV<String, Any> = FieldResolver().getFields(selectedClass)
        val json: String = kv.toYaml(selectedClass.qualifiedName)
        val selection = StringSelection(json)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
        val message = "Convert " + selectedClass.name + " to Yaml success, copied to clipboard."
        val success = notificationGroup.createNotification(message, NotificationType.INFORMATION)
        Notifications.Bus.notify(success, project)
    }
}
