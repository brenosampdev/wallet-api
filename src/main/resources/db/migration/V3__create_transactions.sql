CREATE TABLE transactions (
    id            UUID          PRIMARY KEY,
    user_id       UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id   UUID          NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    type          VARCHAR(10)   NOT NULL CHECK (type IN ('INPUT','OUTPUT')),
    amount        NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    date_time     TIMESTAMPTZ   NOT NULL,
    description   VARCHAR(255),
    installments  INTEGER       NOT NULL DEFAULT 1 CHECK (installments > 0),
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_user_datetime ON transactions(user_id, date_time DESC);
CREATE INDEX idx_transactions_category ON transactions(category_id);
