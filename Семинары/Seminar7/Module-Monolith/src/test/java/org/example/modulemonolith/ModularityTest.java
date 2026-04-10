package org.example.modulemonolith;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// Тестовый класс для проверки модульной структуры приложения
class ModularityTest {

    // Тест: Проверяет, что модуль заказов не зависит от модуля пользователей
    @Test
    void verifyOrdersModuleDoesNotDependOnUsers() {
        // Импортируем все классы из пакета заказов для анализа
        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.example.modulemonolith.orders");

        // Создаем правило архитектуры: никакие классы из пакета заказов не должны зависеть от классов из пакета пользователей
        ArchRule ordersShouldNotDependOnUsers = noClasses()
                .that().resideInAPackage("..orders..") // Выбираем все классы в пакете заказов
                .should().dependOnClassesThat().resideInAPackage("..users..") // Запрещаем зависимости от пакета пользователей
                .because("Модуль заказов должен быть полностью независим от модуля пользователей"); // Объяснение причины правила

        // Проверяем выполнение правила архитектуры
        ordersShouldNotDependOnUsers.check(importedClasses);
    }
}
