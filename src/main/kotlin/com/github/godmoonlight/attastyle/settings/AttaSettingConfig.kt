package com.github.godmoonlight.attastyle.settings

data class ToJsonConfig(var comment: Boolean = false, var randomValue: Boolean = false)
data class ToYamlConfig(var randomValue: Boolean = false)

data class AttaSettingConfig(
    var toJsonConfig: ToJsonConfig = ToJsonConfig(),
    var toYamlConfig: ToYamlConfig = ToYamlConfig()
)
