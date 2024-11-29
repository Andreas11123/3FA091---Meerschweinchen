CREATE TABLE Reading (
    id UUID PRIMARY KEY,
    comment VARCHAR(32),
    customer_id UUID,
    date_of_reading DATE,
    kind_of_meter VARCHAR(32),
    meter_count DOUBLE PRECISION,
    meter_id VARCHAR(32),
    substitute BOOLEAN,
    FOREIGN KEY (customer_id) REFERENCES Customer(id) ON DELETE SET NULL
);