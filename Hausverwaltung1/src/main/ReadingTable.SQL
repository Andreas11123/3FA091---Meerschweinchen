CREATE TABLE Reading (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    meter_value DECIMAL(10, 2) NOT NULL,
    reading_date DATE NOT NULL,
    reading_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Customer(id)
);
