DROP TABLE IF EXISTS users, items;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(253) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requestor_id BIGINT NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT FK_REQUESTOR_ID FOREIGN KEY (REQUESTOR_ID) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_available BOOLEAN,
    owner_id BIGINT NOT NULL,
    booking_id BIGINT,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT FK_OWNER_ID FOREIGN KEY (OWNER_ID) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_REQUEST_ID FOREIGN KEY (REQUEST_ID) REFERENCES requests (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(10),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_ID FOREIGN KEY (ITEM_ID) REFERENCES items (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_BOOKER_ID FOREIGN KEY (BOOKER_ID) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE

);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT FK_ITEM FOREIGN KEY (ITEM_ID) REFERENCES items (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_AUTHOR_ID FOREIGN KEY (AUTHOR_ID) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
