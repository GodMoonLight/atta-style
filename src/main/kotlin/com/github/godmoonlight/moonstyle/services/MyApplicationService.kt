package com.github.godmoonlight.moonstyle.services

import com.github.godmoonlight.moonstyle.MyBundle
import com.github.godmoonlight.moonstyle.settings.MoonSettingConfig
import com.github.godmoonlight.moonstyle.settings.ToJsonConfig
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.godmoonlight.moonstyle.services.MyApplicationService",
    storages = [Storage("Moon-SettingsPlugin.xml")]
)
class MyApplicationService : PersistentStateComponent<MyApplicationService> {

    init {
        println(MyBundle.message("applicationService"))
    }

    var config: MoonSettingConfig = MoonSettingConfig(ToJsonConfig())

    companion object {
        fun getInstance(): MyApplicationService {
            return ServiceManager.getService(MyApplicationService::class.java)
        }
    }

    override fun getState(): MyApplicationService {
        return this
    }

    override fun loadState(state: MyApplicationService) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
