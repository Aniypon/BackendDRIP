package org.example;

/**
 * Микро-резюме кандидата в формате Javadoc.
 *
 * <p>Имя: Арсений Пономарев</p>
 * <p>Роль: Java Backend Developer (Junior)</p>
 * <p>Навыки: Java, Spring Boot, SQL, Docker, Git</p>
 * <p>Цель: Развиваться в backend-разработке и проектировать надежные API.</p>
 */
public class Resume {

    /**
     * Возвращает краткую информацию о кандидате одной строкой.
     *
     * @return краткое микро-резюме
     */
    public String shortSummary() {
        return "Арсений Пономарев | Java Backend Junior | Java, Spring Boot, SQL, Docker, Git";
    }
}
