# DnD-Char-List-Registration

__DnD-registration.postman_collection.json__ - содержит запросы для *postman*.

1. Для получения файла *.jar нужно выполнить следующую команду в корневом каталоге проекта:

    Windows: __.\mvnw clean package__

    Linux: __./mvnw clean package__

2. Для сборки запуска докера:

    __docker-compose up --build__

Пример SQL запросов для инициализации данных:

insert into roles (name)
values
    ('ROLE_USER'), ('ROLE_ADMIN');


insert into users (username, password, email)
values
    ('user', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'user@gmail.com'),
    ('admin', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'admin@gmail.com');


insert into users_roles (user_id, role_id)
values
    (1, 1),
    (2, 2);
