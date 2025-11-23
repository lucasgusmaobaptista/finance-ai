CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    username   VARCHAR(50) UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)



CREATE TABLE categories
(
    id          UUID PRIMARY KEY,
    name        VARCHAR     NOT NULL,
    description VARCHAR(255),
    color       VARCHAR(7),
    icon        VARCHAR(255),
    type        VARCHAR(50) NOT NULL,
    user_id     UUID,
    is_default  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transactions
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255),
    notes       varchar(255),
    amount      DECIMAL(15, 2),
    category_id UUID,
    user_id     UUID,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users (id)
);

create table goals
(
    id             uuid primary key not null,
    achieved       boolean,
    created_at     timestamp(6),
    current_amount numeric(38, 2),
    description    varchar(255),
    end_date       date,
    goal_amount    numeric(38, 2),
    name           varchar(255),
    start_date     date,
    updated_at     timestamp(6),
    user_id        uuid,
    constraint fk_goal_user foreign key (user_id) references users (id)
)
