package com.github.godmoonlight.attastyle.actions

import com.github.godmoonlight.attastyle.utils.ClassMapResult
import com.github.godmoonlight.attastyle.utils.GenerateConverterMethod
import com.github.godmoonlight.attastyle.utils.GenerateMethod
import com.github.godmoonlight.attastyle.utils.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.annotations.NotNull


class BeanConverter : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiClass: PsiClass = ProjectUtil.getPsiClassFromContext(e)!!
        val generateConverterDialog = ConverterDialog(psiClass, from = true, to = true)
        generateConverterDialog.show()
        if (generateConverterDialog.isOK) {
            val classTo: PsiClass = generateConverterDialog.convertToClass
            val classFrom: PsiClass = generateConverterDialog.convertFromClass
            generateConvertAs(
                classTo,
                classFrom,
                generateConverterDialog.isInheritFields(),
                psiClass
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val psiClass: PsiClass? = ProjectUtil.getPsiClassFromContext(e)
        e.presentation.isEnabled = psiClass != null
    }

    private fun generateConvertAs(
        to: PsiClass,
        from: PsiClass,
        inherited: Boolean,
        contentClass: PsiClass
    ) {

        WriteCommandAction.runWriteCommandAction(
            to.project,
            "Convert from " + from.qualifiedName + " to " + to.qualifiedName,
            null,
            getExecute(to, from, inherited, contentClass),
            to.containingFile
        )


    }

    private fun getExecute(
        to: PsiClass,
        from: PsiClass,
        inherited: Boolean,
        contentClass: PsiClass
    ): @NotNull Runnable {
        return Runnable {
            val result: ClassMapResult = ClassMapResult.from(to, from, inherited)
            val action: GenerateMethod = GenerateConverterMethod(result)
            val method: String = action.generate()
            val elementFactory: PsiElementFactory =
                JavaPsiFacade.getElementFactory(contentClass.project)
            val convertAs: PsiMethod = elementFactory.createMethodFromText(method, contentClass)
            val psiElement: PsiElement = contentClass.add(convertAs)
            JavaCodeStyleManager.getInstance(contentClass.project)
                .shortenClassReferences(psiElement)
        }
    }
}