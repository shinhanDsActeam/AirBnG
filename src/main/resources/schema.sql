create table image
(
    created_at  datetime(6)  not null,
    image_id    bigint auto_increment
        primary key,
    updated_at  datetime(6)  null,
    upload_name varchar(255) not null,
    url         varchar(255) not null,
    status      varchar(10)  not null
);

create table jim_type
(
    created_at     datetime(6)  not null,
    jim_type_id    bigint auto_increment
        primary key,
    price_per_hour bigint       not null,
    updated_at     datetime(6)  null,
    description    varchar(255) not null,
    type_name      varchar(255) not null,
    status         varchar(10)  not null
);

create table member
(
    created_at       datetime(6)  not null,
    member_id        bigint auto_increment
        primary key,
    profile_image_id bigint       null,
    updated_at       datetime(6)  null,
    email            varchar(255) not null,
    name             varchar(255) not null,
    nickname         varchar(255) not null,
    password         varchar(255) not null,
    phone            varchar(255) not null,
    status           varchar(10)  not null,
    constraint UK6cheof1rxqhjd4h5wfi308jo2
        unique (profile_image_id),
    constraint FKk6dolck5tod1q6j07u7qpjmrl
        foreign key (profile_image_id) references image (image_id)
);

create table locker
(
    latitude          double             not null,
    longitude         double             not null,
    created_at        datetime(6)        not null,
    locker_id         bigint auto_increment
        primary key,
    member_id         bigint             not null,
    reservation_count bigint default 0   not null,
    updated_at        datetime(6)        null,
    address           varchar(255)       not null,
    address_detail    varchar(255)       not null,
    address_english   varchar(255)       not null,
    locker_name       varchar(255)       not null,
    is_available      enum ('NO', 'YES') not null,
    status            varchar(10)        not null,
    constraint UK4tmnrt2rc1wicjlja20c91h73
        unique (member_id),
    constraint FKcwdw46rsk7jstg14ey1ppkb1h
        foreign key (member_id) references member (member_id)
);

create table locker_image
(
    created_at      datetime(6) not null,
    image_id        bigint      not null,
    locker_id       bigint      not null,
    locker_image_id bigint auto_increment
        primary key,
    updated_at      datetime(6) null,
    status          varchar(10) not null,
    constraint UK7fm59vrlxgjpupyaebrcehhr4
        unique (image_id),
    constraint FKp1345gkjtalhblu5yh5w0akh
        foreign key (locker_id) references locker (locker_id),
    constraint FKpn3pty9tun77tbnsux679ndlf
        foreign key (image_id) references image (image_id)
);

create table locker_jim_type
(
    created_at         datetime(6) not null,
    jimtype_id         bigint      not null,
    locker_id          bigint      not null,
    locker_jim_type_id bigint auto_increment
        primary key,
    updated_at         datetime(6) null,
    status             varchar(10) not null,
    constraint FK6wevqq9drcha4pt9pxmjf10re
        foreign key (locker_id) references locker (locker_id),
    constraint FKgkaag57st2e27mc7bn49fo2ss
        foreign key (jimtype_id) references jim_type (jim_type_id)
);

create table reservation
(
    created_at     datetime(6)                                             not null,
    dropper_id     bigint                                                  not null,
    end_time       datetime(6)                                             not null,
    keeper_id      bigint                                                  not null,
    reservation_id bigint auto_increment
        primary key,
    start_time     datetime(6)                                             not null,
    updated_at     datetime(6)                                             null,
    state          enum ('CANCELLED', 'COMPLETED', 'CONFIRMED', 'PENDING') not null,
    status         varchar(10)                                             not null,

    constraint FK_dropper foreign key (dropper_id) references member (member_id),
    constraint FK_keeper foreign key (keeper_id) references member (member_id)
);

create table reservation_jim_type
(
    count                   bigint      not null,
    created_at              datetime(6) not null,
    jimtype_id              bigint      not null,
    reservation_id          bigint      not null,
    reservation_jim_type_id bigint auto_increment
        primary key,
    updated_at              datetime(6) null,
    status                  varchar(10) not null,
    constraint FK3jio2g63bg3uuqqixgfoop5on
        foreign key (reservation_id) references reservation (reservation_id),
    constraint FKcb5vg9lxkehow6dvcmj5x3hbn
        foreign key (jimtype_id) references jim_type (jim_type_id)
);

create table zzim
(
    created_at datetime(6) not null,
    locker_id  bigint      not null,
    member_id  bigint      not null,
    updated_at datetime(6) null,
    zzim_id    bigint auto_increment
        primary key,
    status     varchar(10) not null,
    constraint FK6pe1vv0etmoe36by7klmclgah
        foreign key (locker_id) references locker (locker_id),
    constraint FKjy5wvuycpr8kvcq7ui0fudqvj
        foreign key (member_id) references member (member_id)
);