package com.github.godmoonlight.attastyle.utils

import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.util.PsiUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class GenerateConverterMethod(private val mapResult: ClassMapResult) : GenerateMethod {
    private val toClassName: String? = mapResult.to.qualifiedName
    private var toName: String? = null
    private val fromName: String? = SuggestionName[mapResult.from]
    private val fromClassName: @Nullable String? = mapResult.from.qualifiedName

    @NotNull
    private fun buildMethodSignature(): StringBuilder {
        val builder = StringBuilder("public static ")
        builder.append(toClassName)
        builder.append(" to").append(mapResult.to.name)
        builder.append("(").append(fromClassName).append(" ").append(fromName).append(") {\n")
        builder.append("if (").append(fromName).append(" == null) {\nreturn null;\n}\n")
        builder.append(toClassName)
            .append(" ")
            .append(toName)
            .append(" = new ")
            .append(toClassName).append("();\n")
        return builder
    }

    override fun generate(): String {
        val builder = buildMethodSignature()
        builder.append(writeMappedFields())
        builder.append(mapResult.writeNotMappedFields())
        builder.append("return ").append(toName).append(";\n}")
        return builder.toString()
    }

    @NotNull
    private fun writeMappedFields(): String {
        val builder = StringBuilder()

        for (field in mapResult.getMappedFields()) {
            builder.append(toName).append(".")
                .append(field.value.second.name)
                .append("(").append(fromName).append(".")
                .append(field.value.first.name)
                .append("());\n")
        }
        for (field in mapResult.getMappedConvertibleFields()) {
            if (field.value.first.returnType is PsiPrimitiveType) {
                builder.append(toName).append(".")
                    .append(field.value.second.name)
                    .append("(").append(fromName).append(".")
                    .append(field.value.first.name)
                    .append("());\n")
                continue
            }
            builder.append("if(")
                .append(fromName).append(".")
                .append(field.value.first.name)
                .append("()!=null){\n")
            when {
                PsiUtil.resolveClassInClassTypeOnly(field.key.type)?.isEnum!! -> {
                    builder.append(toName).append(".")
                        .append(field.value.second.name)
                        .append("(")
                        .append(field.key.type.presentableText)
                        .append(".valueOf(")
                        .append(fromName).append(".")
                        .append(field.value.first.name)
                        .append("()));\n")
                }
                PsiUtil.resolveClassInClassTypeOnly(field.value.first.returnType)!!.isEnum -> {
                    builder.append(toName).append(".")
                        .append(field.value.second.name)
                        .append("(").append(fromName).append(".")
                        .append(field.value.first.name)
                        .append("().name());\n")
                }
                else -> {
                    builder.append(toName).append(".")
                        .append(field.value.second.name)
                        .append("(").append(fromName).append(".")
                        .append(field.value.first.name)
                        .append("());\n")
                }
            }
            builder.append("}\n")
        }
        return builder.toString()
    }

    init {
        val toName = SuggestionName[mapResult.to]
        if (fromName == toName) {
            this.toName = toName + "1"
        } else {
            this.toName = toName
        }
    }
}