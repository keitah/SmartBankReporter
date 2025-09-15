CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(32) PRIMARY KEY,
    owner_name TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    account_number VARCHAR(32) REFERENCES accounts(account_number) ON DELETE CASCADE,
    amount NUMERIC(12,2) NOT NULL,
    description TEXT,
    type VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
