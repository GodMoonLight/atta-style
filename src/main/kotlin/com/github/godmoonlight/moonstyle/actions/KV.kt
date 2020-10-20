package com.github.godmoonlight.moonstyle.actions

import com.google.gson.GsonBuilder
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

class KV<K, V> : LinkedHashMap<K, V>() {
    operator fun set(key: K, value: V): KV<*, *> {
        super.put(key, value)
        return this
    }

    fun set(map: Map<K, V>?): KV<K, V> {
        super.putAll(map!!)
        return this
    }

    fun toPrettyJson(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

    fun toYaml(className: String?): String {
        val yaml = Yaml()
        val ymlString = yaml.dumpAs(this, null, DumperOptions.FlowStyle.BLOCK)
        return if (className != null) {
            "!!$className\n$ymlString"
        } else {
            ymlString
        }
    }

    companion object {
        fun <K, V> create(): KV<K, V> {
            return KV<K, V>()
        }
    }
}
