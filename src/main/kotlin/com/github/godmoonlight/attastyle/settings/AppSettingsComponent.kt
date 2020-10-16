package com.github.godmoonlight.attastyle.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSeparator

/**
 * Supports creating and managing a JPanel for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel
    private val myUserNameText = JBTextField()
    private val toJsonRandom = JBCheckBox("Use Random Value")
    private val toJsonComment = JBCheckBox("Show comment")

    val preferredFocusedComponent: JComponent
        get() = toJsonRandom
    private var comment: Boolean
        get() = toJsonComment.isSelected
        set(newText) {
            toJsonComment.isSelected = newText
        }
    private var randomValue: Boolean
        get() = toJsonRandom.isSelected
        set(newStatus) {
            toJsonRandom.isSelected = newStatus
        }

    var attaSettingConfig: AttaSettingConfig
        get() {
            return AttaSettingConfig(ToJsonConfig(comment, randomValue))
        }
        set(value) {
            comment = value.toJsonConfig.comment
            randomValue = value.toJsonConfig.randomValue
        }

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("To Json", JSeparator())
            .addComponent(toJsonComment, 1)
            .addComponent(toJsonRandom, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
