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
