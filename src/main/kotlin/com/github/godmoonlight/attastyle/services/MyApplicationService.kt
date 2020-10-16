package com.github.godmoonlight.attastyle.services

import com.github.godmoonlight.attastyle.MyBundle
import com.github.godmoonlight.attastyle.settings.AttaSettingConfig
import com.github.godmoonlight.attastyle.settings.ToJsonConfig
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.godmoonlight.attastyle.services.MyApplicationService",
    storages = [Storage("Atta-SettingsPlugin.xml")]
)
class MyApplicationService : PersistentStateComponent<MyApplicationService> {

    init {
        println(MyBundle.message("applicationService"))
    }

    var config: AttaSettingConfig = AttaSettingConfig(ToJsonConfig())

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
