package com.github.godmoonlight.moonstyle.settings

data class ToJsonConfig(
    var comment: Boolean = false,
    var randomValue: Boolean = false,
    var enumValues: Boolean = false
)

data class ToYamlConfig(
    var randomValue: Boolean = false,
    var enumValues: Boolean = false
)

data class MoonSettingConfig(
    var toJsonConfig: ToJsonConfig = ToJsonConfig(),
    var toYamlConfig: ToYamlConfig = ToYamlConfig()
)
