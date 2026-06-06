CREATE TABLE categories (
    id           UUID         PRIMARY KEY,
    user_id      UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title        VARCHAR(80)  NOT NULL,
    description  VARCHAR(255),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_categories_user_title UNIQUE (user_id, title)
);

CREATE INDEX idx_categories_user ON categories(user_id);
