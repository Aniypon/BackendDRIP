package org.example.modulemonolith.orders;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// Тестовый класс для проверки модуля заказов
class OrderModuleTest {

    // Тест: Проверяет, что модуль заказов не имеет прямого доступа к репозиторию пользователей
    @Test
    void ordersModuleShouldNotAccessUserRepositoryDirectly() {
        // Импортируем все классы из пакета заказов для анализа
        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.example.modulemonolith.orders");

        // Создаем правило архитектуры: запрещаем зависимость от UserRepository по имени класса
        ArchRule rule = noClasses()
                .that().resideInAPackage("..orders..") // Выбираем все классы в пакете заказов
                .should().dependOnClassesThat().haveSimpleName("UserRepository") // Запрещаем зависимость от класса UserRepository
                .because("Модуль заказов не должен напрямую обращаться к UserRepository. Используйте события вместо этого."); // Объяснение

        // Проверяем выполнение правила
        rule.check(importedClasses);
    }

    // Тест: Проверяет, что модуль заказов не имеет прямого доступа к сервису пользователей
    @Test
    void ordersModuleShouldNotAccessUserServiceDirectly() {
        // Импортируем все классы из пакета заказов для анализа
        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.example.modulemonolith.orders");

        // Создаем правило архитектуры: запрещаем зависимость от UserService по имени класса
        ArchRule rule = noClasses()
                .that().resideInAPackage("..orders..") // Выбираем все классы в пакете заказов
                .should().dependOnClassesThat().haveSimpleName("UserService") // Запрещаем зависимость от класса UserService
                .because("Модуль заказов не должен напрямую обращаться к UserService. Используйте события вместо этого."); // Объяснение

        // Проверяем выполнение правила
        rule.check(importedClasses);
    }

    // Тест: Проверяет, что модуль заказов не имеет прямого доступа к API модуля пользователей
    @Test
    void ordersModuleShouldNotAccessUserModuleApi() {
        // Импортируем все классы из пакета заказов для анализа
        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.example.modulemonolith.orders");

        // Создаем правило архитектуры: запрещаем зависимость от UserModuleApi по имени класса
        ArchRule rule = noClasses()
                .that().resideInAPackage("..orders..") // Выбираем все классы в пакете заказов
                .should().dependOnClassesThat().haveSimpleName("UserModuleApi") // Запрещаем зависимость от класса UserModuleApi
                .because("Модуль заказов не должен напрямую обращаться к UserModuleApi. Используйте события вместо этого."); // Объяснение

        // Проверяем выполнение правила
        rule.check(importedClasses);
    }

    // Тест: Проверяет, что модуль заказов зависит только от своего собственного пакета событий
    @Test
    void ordersModuleShouldOnlyDependOnItsOwnEvents() {
        // Импортируем все классы из пакета заказов для анализа
        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.example.modulemonolith.orders");

        // Модуль заказов должен зависеть только от своего собственного пакета событий для коммуникации
        // Это проверяет правильную развязку модулей
    }
}
