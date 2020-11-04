package com.github.godmoonlight.moonstyle.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSeparator

/**
 * Supports creating and managing a JPanel for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel
    private val toJsonRandom = JBCheckBox("Use random value")
    private val toJsonComment = JBCheckBox("Show comment")
    private val toJsonEnum = JBCheckBox("Show comment")
    private val toYamlEnum = JBCheckBox("Use random value")
    private val toYamlRandomValue = JBCheckBox("Show All Enum values")

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

    var moonSettingConfig: MoonSettingConfig
        get() {
            return MoonSettingConfig(ToJsonConfig(comment, randomValue))
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
            .addComponent(toJsonEnum, 1)
            .addSeparator()
            .addLabeledComponent("To Yaml", JSeparator())
            .addComponent(toYamlEnum, 1)
            .addComponent(toYamlRandomValue, 1)
            .addSeparator()
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
