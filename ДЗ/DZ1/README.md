# DZ1: Микро-резюме + Dockerfile + Mermaid

Java-приложение, выводящее микро-резюме в консоль.

## Состав
- Микро-резюме: `Резюме.md`, `Резюме.wiki`, javadoc в `Resume.java`
- Блок-схема (Mermaid): `Блок-схема.mmd`
- Dockerfile: multi-stage сборка (`gradle installDist`) и запуск JRE-образа

## Запуск через Docker
```bash
docker build -t dz1 .
docker run --rm dz1
```
Ожидаемый вывод:
```
Арсений Пономарев | Java Backend Junior | Java, Spring Boot, SQL, Docker, Git
```

## Тесты
```bash
gradle test
```
`ResumeTest` проверяет содержимое и формат микро-резюме.
