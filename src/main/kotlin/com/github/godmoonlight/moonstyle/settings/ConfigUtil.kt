package com.github.godmoonlight.moonstyle.settings

import com.github.godmoonlight.moonstyle.services.MyApplicationService

object ConfigUtil {

    fun get(): AttaSettingConfig {
        return MyApplicationService.getInstance().config
    }
}
