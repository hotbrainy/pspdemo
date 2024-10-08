CREATE TABLE payment_request
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    card_number VARCHAR NOT NULL,
    expiry_date VARCHAR NOT NULL,
    cvv VARCHAR NOT NULL,
    amount FLOAT NOT NULL,
    currency VARCHAR NOT NULL,
    merchant_id VARCHAR NOT NULL
);

CREATE TABLE transaction
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    card_number VARCHAR NOT NULL,
    status VARCHAR NOT NULL,
    merchant_id VARCHAR NOT NULL
);