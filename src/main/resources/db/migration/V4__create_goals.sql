CREATE TABLE goals (
    id              UUID          PRIMARY KEY,
    user_id         UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title           VARCHAR(120)  NOT NULL,
    description     VARCHAR(255),
    target_amount   NUMERIC(19,2) NOT NULL CHECK (target_amount > 0),
    current_amount  NUMERIC(19,2) NOT NULL DEFAULT 0,
    completed       BOOLEAN       NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_goals_user ON goals(user_id);
