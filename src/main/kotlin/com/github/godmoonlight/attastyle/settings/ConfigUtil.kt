package com.github.godmoonlight.attastyle.settings

import com.github.godmoonlight.attastyle.services.MyApplicationService

object ConfigUtil {

    fun get(): AttaSettingConfig {
        return MyApplicationService.getInstance().config
    }
}
