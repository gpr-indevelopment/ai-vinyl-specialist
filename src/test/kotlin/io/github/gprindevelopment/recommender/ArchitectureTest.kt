package io.github.gprindevelopment.recommender

import com.tngtech.archunit.base.Predicates.alwaysTrue
import com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.modules.syntax.AllowedModuleDependencies.allow
import com.tngtech.archunit.library.modules.syntax.ModuleDependencyScope.consideringOnlyDependenciesBetweenModules
import com.tngtech.archunit.library.modules.syntax.ModuleRuleDefinition.modules
import org.junit.jupiter.api.Test


class ArchitectureTest {

    @Test
    fun `Modules should respect their declared dependencies`() {
        val prodClasses = ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("io.github.gprindevelopment.recommender")
        val rule = modules()
            .definedByPackages("io.github.gprindevelopment.recommender.(*)..")
            .should().respectTheirAllowedDependencies(
                allow()
                    .fromModule("server").toModules("discogs", "assistant", "domain")
                    .fromModule("assistant").toModules("domain")
                    .fromModule("discogs").toModules("domain"),
                    consideringOnlyDependenciesBetweenModules()
            )
            .ignoreDependency(alwaysTrue(), belongToAnyOf(AiVinylRecommenderApplication::class.java))
        rule.check(prodClasses)
    }

    @Test
    fun `LangChain library must only be used by assistant and server modules`() {
        val langChainClasses = ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("..dev.langchain4j..", "io.github.gprindevelopment.recommender")
        val rule = classes()
            .that().resideInAPackage("..dev.langchain4j..")
            .should().onlyBeAccessed().byAnyPackage(
                "..dev.langchain4j..",
                "io.github.gprindevelopment.recommender.assistant",
                "io.github.gprindevelopment.recommender.server"
            )
        rule.check(langChainClasses)
    }
}