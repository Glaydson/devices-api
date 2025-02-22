DROP TABLE IF EXISTS devices;

CREATE TABLE devices (
                         id         INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                         name          VARCHAR(100) NOT NULL,
                         brand         VARCHAR(100) NOT NULL,
                         state         VARCHAR(50) NOT NULL,
                         creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);