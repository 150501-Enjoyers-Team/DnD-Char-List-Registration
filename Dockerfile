# Используем базовый образ с установленной Java
FROM openjdk:17

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл приложения в контейнер
COPY target/dndcharlistreg-0.0.1-SNAPSHOT.jar .

# Определяем команду для запуска приложения
CMD ["java", "-jar", "dndcharlistreg-0.0.1-SNAPSHOT.jar"]