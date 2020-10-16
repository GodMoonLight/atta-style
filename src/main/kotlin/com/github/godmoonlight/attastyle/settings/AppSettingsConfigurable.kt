package com.github.godmoonlight.attastyle.settings

import com.intellij.openapi.options.Configurable
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String? {
        return "Atta Style"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings: AttaSettingConfig = ConfigUtil.get()
        val modified: AttaSettingConfig? = mySettingsComponent?.attaSettingConfig

        return modified != settings
    }

    override fun apply() {
        val settings: AttaSettingConfig = ConfigUtil.get()
        val modified: AttaSettingConfig? = mySettingsComponent?.attaSettingConfig
        if (modified != null) {
            XmlSerializerUtil.copyBean(modified, settings)
        }
    }

    override fun reset() {
        val settings: AttaSettingConfig = ConfigUtil.get()
        mySettingsComponent?.attaSettingConfig = settings
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
