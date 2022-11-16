drop table if exists requests cascade;
drop table if exists compilations_events cascade;
drop table if exists events cascade;
drop table if exists categories cascade;
drop table if exists compilations cascade;
drop table if exists users cascade;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(50) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email) );

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN NOT NULL );

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uq_categories_name UNIQUE (name));

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_id BIGINT,
    title VARCHAR(255) NOT NULL,
    annotation VARCHAR(1000),
    description VARCHAR(1000),
    event_date TIMESTAMP WITHOUT TIME ZONE,
    initiator_id BIGINT,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    state VARCHAR(20),
    paid BOOLEAN NOT NULL,
    request_moderation BOOLEAN,
    participant_limit int4,
    lat float8,
    lon float8,
    CONSTRAINT fk_events_category_id FOREIGN KEY(category_id) REFERENCES categories (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_initiator_id FOREIGN KEY(initiator_id) REFERENCES users (id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status VARCHAR(20),
    event_id BIGINT,
    requester_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_requester_id FOREIGN KEY(requester_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_event_id FOREIGN KEY(event_id) REFERENCES events (id) ON DELETE CASCADE );

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT,
    event_id BIGINT,
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilations_events_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_compilations_events_event_id FOREIGN KEY (event_id) REFERENCES events (id) );

