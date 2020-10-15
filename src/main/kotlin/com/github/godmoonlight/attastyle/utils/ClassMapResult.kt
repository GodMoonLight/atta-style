package com.github.godmoonlight.attastyle.utils

import com.github.godmoonlight.attastyle.utils.ProjectUtil.getProjectIndentation
import com.intellij.lang.jvm.JvmModifier
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiUtil
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.collections.LinkedHashMap


class ClassMapResult(var from: PsiClass, var to: PsiClass) {

    private var useInherited: Boolean = false

    private var autoConvert: Boolean = true

    private val mappedFields: LinkedHashMap<PsiField, Pair<PsiMethod, PsiMethod>> = LinkedHashMap()


    private val mappedConvertibleFields: LinkedHashMap<PsiField, Pair<PsiMethod, PsiMethod>> =
        LinkedHashMap()

    private val notMappedToFields: MutableList<String> = LinkedList()

    private val notMappedFromFields: MutableList<String> = LinkedList()

    fun getMappedFields(): Map<PsiField, Pair<PsiMethod, PsiMethod>> {
        return mappedFields
    }

    fun getMappedConvertibleFields(): Map<PsiField, Pair<PsiMethod, PsiMethod>> {
        return mappedConvertibleFields
    }


    private fun addMappedField(field: PsiField, getter: PsiMethod, setter: PsiMethod) {
        this.mappedFields[field] = Pair(getter, setter)

    }

    private fun addNotMappedToField(toField: String) {
        notMappedToFields.add(toField)
    }

    private fun addNotMappedFromField(fromField: String) {
        notMappedFromFields.add(fromField)
    }

    private fun processToFields() {
        for (toField in getFields(this.to, useInherited)) {

            val fieldName = toField.name
            val toSetter = findSetter(this.to, fieldName, useInherited)
            val fromGetter = findGetter(this.from, fieldName, useInherited)
            if (toSetter == null || fromGetter == null) {
                this.addNotMappedToField(fieldName)
            } else if (isMatchingFieldType(toField, fromGetter)) {
                this.addMappedField(toField, fromGetter, toSetter)
            } else if (canConvert(toField, fromGetter)) {
                this.mappedConvertibleFields[toField] = Pair(fromGetter, toSetter)
            } else {
                this.addNotMappedToField(fieldName)
            }

        }
    }

    private fun processFromFields() {
        for (fromField in getFields(from, useInherited)) {
            val fromFieldName = fromField.name
            if (!this.getMappedFields().contains(fromField)
                && !this.mappedConvertibleFields.containsKey(fromField)
            ) {
                this.addNotMappedFromField(fromFieldName)
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
            val setters = psiClass.findMethodsByName(
                "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1),
                useInherited
            )
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

        if (fromType == null) {
            return false
        }
        if (toType.isAssignableFrom(fromType)) {
            return true
        }

        return false
    }

    private fun canConvert(toField: PsiField, fromGetter: PsiMethod): Boolean {
        val fromType = fromGetter.returnType
        val toType = toField.type
        if (fromType == null) {
            return false
        }
        if (!autoConvert) {
            return false
        }
        if (fromType.isConvertibleFrom(fromType)) {
            return true
        }
        if (autoConvert && (PsiUtil.resolveClassInClassTypeOnly(toType)!!.isEnum
                    || PsiUtil.resolveClassInClassTypeOnly(fromType)!!.isEnum)
        ) {
            return true
        }
        return false
    }


    fun getGetter(field: String): String? {
        val methodSuffix = field.substring(0, 1).toUpperCase() + field.substring(1)
        var getters = from.findMethodsByName("get$methodSuffix", true)
        if (getters.isNotEmpty()) {
            return getters[0].name
        }
        getters = from.findMethodsByName("is$methodSuffix", true)
        return if (getters.isNotEmpty()) {
            getters[0].name
        } else null
    }

    fun getSetter(field: String): String? {
        val methodSuffix = field.substring(0, 1).toUpperCase() + field.substring(1)
        val getters = from.findMethodsByName("set$methodSuffix", true)
        return if (getters.isNotEmpty()) {
            getters[0].name
        } else null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ClassMapResult
        return EqualsBuilder()
            .append(mappedFields, that.mappedFields)
            .append(notMappedToFields, that.notMappedToFields)
            .append(notMappedFromFields, that.notMappedFromFields)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
            .append(mappedFields)
            .append(notMappedToFields)
            .append(notMappedFromFields)
            .toHashCode()
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
        return writeNotMappedFields(notMappedFromFields, from) + writeNotMappedFields(
            notMappedToFields,
            to
        )
    }
}