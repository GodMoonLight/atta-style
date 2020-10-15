package com.github.godmoonlight.attastyle.actions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.util.*


class KV<K, V> : LinkedHashMap<K, V>() {
    operator fun set(key: K, value: V): KV<*, *> {
        super.put(key, value)
        return this
    }

    fun set(map: Map<K, V>?): KV<K, V> {
        super.putAll(map!!)
        return this
    }

    fun set(KV: KV<K, V>?): KV<K, V> {
        super.putAll(KV!!)
        return this
    }

    fun delete(key: Any?): KV<*, *> {
        remove(key)
        return this
    }

    fun <T> getAs(key: Any?): T? {
        return get(key) as T?
    }

    fun getStr(key: Any?): String? {
        return get(key) as String?
    }

    fun getInt(key: Any?): Int? {
        return get(key) as Int?
    }

    fun getLong(key: Any?): Long? {
        return get(key) as Long?
    }

    fun getBoolean(key: Any?): Boolean? {
        return get(key) as Boolean?
    }

    fun getFloat(key: Any?): Float? {
        return get(key) as Float?
    }

    /**
     * key 存在，并且 value 不为 null
     */
    fun notNull(key: Any?): Boolean {
        return get(key) != null
    }

    /**
     * key 不存在，或者 key 存在但 value 为null
     */
    fun isNull(key: Any?): Boolean {
        return get(key) == null
    }

    /**
     * key 存在，并且 value 为 true，则返回 true
     */
    fun isTrue(key: Any?): Boolean {
        val value: Any? = get(key)
        return value is Boolean && value == true
    }

    /**
     * key 存在，并且 value 为 false，则返回 true
     */
    fun isFalse(key: Any?): Boolean {
        val value: Any? = get(key)
        return value is Boolean && value == false
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun toPrettyJson(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

    fun toYaml(className: String?): String {
        val yaml = Yaml()
        val ymlString= yaml.dumpAs(this, null, DumperOptions.FlowStyle.BLOCK)
        return if (className != null) {
            "!!$className\n$ymlString"
        } else {
            ymlString
        }
    }

    override fun equals(KV: Any?): Boolean {
        return KV is KV<*, *> && super.equals(KV)
    }

    companion object {
        fun <K, V> by(key: K, value: V): KV<*, *> {
            return KV<Any?, Any?>().set(key, value)
        }

        fun <K, V> create(): KV<K, V> {
            return KV<K, V>()
        }
    }
}