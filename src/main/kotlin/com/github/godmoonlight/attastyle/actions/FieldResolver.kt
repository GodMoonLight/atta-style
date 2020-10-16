package com.github.godmoonlight.attastyle.actions

import com.github.godmoonlight.attastyle.settings.ConfigUtil
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.PsiUtil
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object FieldResolver {

    private const val pattern = "yyyy-MM-dd HH:mm:ss"
    private val df: DateFormat = SimpleDateFormat(pattern)
    var isShowComment: Boolean = ConfigUtil.get().toJsonConfig.comment
    var isRandom: Boolean = ConfigUtil.get().toJsonConfig.randomValue

    @NonNls
    private val normalTypes: MutableMap<String, Any> = HashMap()
    private fun isNormalType(typeName: String): Boolean {
        return normalTypes.containsKey(typeName)
    }

    fun getFields(psiClass: PsiClass?): KV<String, Any> {
        val kv: KV<String, Any> = KV.create()
        val commentKV: KV<String, Any> = KV.create()
        if (psiClass == null) {
            return kv
        }
        for (field in psiClass.allFields) {
            val type = field.type
            val name = field.name

            // doc comment
            if (field.docComment != null && field.docComment!!.text != null) {
                commentKV[name] = field.docComment!!.text
            }
            // primitive Type
            if (type is PsiPrimitiveType) {
                kv[name] = PsiTypesUtil.getDefaultValue(type)
            } else {
                // reference Type
                val fieldTypeName = type.presentableText
                when {
                    isNormalType(fieldTypeName) -> {
                        // normal Type
                        kv[name] = normalTypes[fieldTypeName]!!
                    }
                    type is PsiArrayType -> {
                        // array type
                        kv[name] = processArrayList(type)
                    }
                    fieldTypeName.startsWith("List") -> {
                        // list type
                        kv[name] = processList(type)
                    }
                    PsiUtil.resolveClassInClassTypeOnly(type)!!.isEnum -> {
                        // enum
                        kv[name] = peocesEnum(type)
                    }
                    else -> {
                        // class type
                        kv[name] = getFields(PsiUtil.resolveClassInType(type))
                    }
                }
            }
        }
        if (isShowComment && commentKV.size > 0) {
            kv["@comment"] = commentKV
        }
        return kv
    }

    private fun peocesEnum(type: @NotNull PsiType) =
        PsiUtil.resolveClassInClassTypeOnly(type)!!
            .fields.filterIsInstance<PsiEnumConstant>().map { it.name }

    private fun processList(type: @NotNull PsiType): ArrayList<Any> {
        val iterableType = PsiUtil.extractIterableTypeParameter(type, false)
        val iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType)
        val list = ArrayList<Any>()
        val classTypeName = iterableClass!!.name!!
        if (isNormalType(classTypeName)) {
            normalTypes[classTypeName]?.let { list.add(it) }
        } else {
            list.add(getFields(iterableClass))
        }
        return list
    }

    private fun processArrayList(type: @NotNull PsiType): ArrayList<Any> {
        val deepType = type.getDeepComponentType()
        val list = ArrayList<Any>()
        val deepTypeName = deepType.presentableText
        when {
            deepType is PsiPrimitiveType -> {
                list.add(PsiTypesUtil.getDefaultValue(deepType))
            }
            isNormalType(deepTypeName) -> {
                normalTypes[deepTypeName]?.let { list.add(it) }
            }
            else -> {
                list.add(getFields(PsiUtil.resolveClassInType(deepType)))
            }
        }
        return list
    }

    init {
        normalTypes["Boolean"] = false
        normalTypes["Byte"] = 0
        normalTypes["Short"] = 0
        normalTypes["Integer"] = 0
        normalTypes["Long"] = 0L
        normalTypes["Float"] = 0.0f
        normalTypes["Double"] = 0.0
        normalTypes["String"] = ""
        normalTypes["BigDecimal"] = 0.0
        normalTypes["Date"] = df.format(Date())
        normalTypes["Timestamp"] = System.currentTimeMillis()
        normalTypes["LocalDate"] = LocalDate.now().toString()
        normalTypes["LocalTime"] = LocalTime.now().toString()
        normalTypes["LocalDateTime"] = LocalDateTime.now().toString()
    }
}
