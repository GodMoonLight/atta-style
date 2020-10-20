package com.github.godmoonlight.moonstyle.utils

import com.github.godmoonlight.moonstyle.utils.ProjectUtil.getProjectIndentation
import com.intellij.lang.jvm.JvmModifier
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiUtil
import org.jetbrains.annotations.NotNull
import java.util.LinkedList
import kotlin.collections.LinkedHashMap

class ClassMapResult(var from: PsiClass, var to: PsiClass) {

    private var useInherited: Boolean = false

    private var autoConvert: Boolean = true

    val mappedFields: LinkedHashMap<PsiField, Pair<PsiMethod, PsiMethod>> = LinkedHashMap()

    val mappedConvertibleFields: LinkedHashMap<PsiField, Pair<PsiMethod, PsiMethod>> =
        LinkedHashMap()

    private val notMappedToFields: MutableList<String> = LinkedList()

    private val notMappedFromFields: MutableList<String> = LinkedList()

    private fun addMappedField(field: PsiField, getter: PsiMethod, setter: PsiMethod) {
        this.mappedFields[field] = Pair(getter, setter)
    }

    private fun processToFields() {
        for (toField in getFields(this.to, useInherited)) {
            val fieldName = toField.name
            val toSetter = findSetter(this.to, fieldName, useInherited)
            val fromGetter = findGetter(this.from, fieldName, useInherited)
            when {
                toSetter == null || fromGetter == null -> {
                    this.notMappedToFields.add(fieldName)
                }
                isMatchingFieldType(toField, fromGetter) -> {
                    this.addMappedField(toField, fromGetter, toSetter)
                }
                canConvert(toField, fromGetter) -> {
                    this.mappedConvertibleFields[toField] = Pair(fromGetter, toSetter)
                }
                else -> {
                    this.notMappedToFields.add(fieldName)
                }
            }
        }
    }

    private fun processFromFields() {
        for (fromField in getFields(from, useInherited)) {
            val fromFieldName = fromField.name
            if (!this.mappedFields.contains(fromField) &&
                !this.mappedConvertibleFields.containsKey(fromField)
            ) {
                this.notMappedFromFields.add(fromFieldName)
            }
        }
    }

    companion object {

        fun from(to: PsiClass, from: PsiClass, inherited: Boolean): ClassMapResult {
            val result = ClassMapResult(from, to)
            result.useInherited = inherited
            result.processToFields()
            result.processFromFields()
            return result
        }

        private fun getFields(clazz: PsiClass, useInherited: Boolean): List<PsiField> {
            return if (useInherited) {
                clazz.allFields
            } else {
                clazz.fields
            }.filter { !it.hasModifier(JvmModifier.STATIC) }
        }

        private fun findSetter(
            psiClass: PsiClass,
            fieldName: String,
            useInherited: Boolean
        ): PsiMethod? {
            val name = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
            val setters = psiClass.findMethodsByName(name, useInherited)
            return if (setters.size == 1) {
                setters[0]
            } else null
        }

        private fun findGetter(
            psiClass: PsiClass,
            fieldName: String,
            useInherited: Boolean
        ): PsiMethod? {
            val methodSuffix = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
            var getters: Array<out @NotNull PsiMethod> =
                psiClass.findMethodsByName("get$methodSuffix", useInherited)
            if (getters.isNotEmpty()) {
                return getters[0]
            }
            getters = psiClass.findMethodsByName("is$methodSuffix", false)
            return if (getters.isNotEmpty()) {
                getters[0]
            } else null
        }
    }

    private fun isMatchingFieldType(toField: PsiField, fromGetter: PsiMethod): Boolean {
        val fromType = fromGetter.returnType
        val toType = toField.type
        if (fromType != null && toType.isAssignableFrom(fromType)) {
            return true
        }
        return false
    }

    private fun canConvert(toField: PsiField, fromGetter: PsiMethod): Boolean {
        val fromType = fromGetter.returnType
        val toType = toField.type
        var result = false
        if (fromType != null) {
            when {
                fromType.isConvertibleFrom(fromType) -> {
                    result = true
                }
                autoConvert -> {
                    val a = PsiUtil.resolveClassInClassTypeOnly(toType)!!.isEnum
                    result = a || PsiUtil.resolveClassInClassTypeOnly(fromType)!!.isEnum
                }
            }
        }
        return result
    }

    private fun writeNotMappedFields(notMappedFields: List<String>, psiClass: PsiClass?): String {
        val indentation = getProjectIndentation(psiClass!!)
        val builder = StringBuilder()
        if (notMappedFields.isNotEmpty()) {
            builder.append("\n")
                .append(indentation)
                .append("// Not mapped ")
                .append(psiClass.name)
                .append(" fields: \n")
        }
        for (notMappedField in notMappedFields) {
            builder.append(indentation)
                .append("// ")
                .append(notMappedField)
                .append("\n")
        }
        return builder.toString()
    }

    fun writeNotMappedFields(): String? {
        val a = writeNotMappedFields(notMappedFromFields, from)
        val b = writeNotMappedFields(notMappedToFields, to)
        return "$a$b"
    }
}
