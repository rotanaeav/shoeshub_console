-- ========================================================
-- ShoesHub E-Commerce Database Schema
-- DBMS: PostgreSQL
-- ========================================================

-- Custom Enum Types
CREATE TYPE user_role AS ENUM ('ADMIN', 'SELLER', 'CUSTOMER');
ALTER TYPE user_role OWNER TO postgres;

CREATE TYPE order_status AS ENUM ('PENDING', 'PAID', 'CANCELLED');
ALTER TYPE order_status OWNER TO postgres;

CREATE TYPE payment_method AS ENUM ('CASH', 'KHQR');
ALTER TYPE payment_method OWNER TO postgres;

CREATE TYPE payment_status AS ENUM ('SUCCESS', 'FAILED', 'PENDING');
ALTER TYPE payment_status OWNER TO postgres;

-- 1. Users Table
CREATE TABLE users
(
    id            UUID                     DEFAULT gen_random_uuid() NOT NULL
        PRIMARY KEY,
    role          user_role                                          NOT NULL,
    full_name     VARCHAR(255)                                       NOT NULL,
    username      VARCHAR(100)                                       NOT NULL
        UNIQUE,
    password_hash VARCHAR(255)                                       NOT NULL,
    phone         VARCHAR(20),
    date_of_birth DATE,
    gender        VARCHAR(10)
        CONSTRAINT users_gender_check
            CHECK ((gender)::TEXT = ANY ((ARRAY ['MALE'::CHARACTER VARYING, 'FEMALE'::CHARACTER VARYING])::TEXT[])),
    address       VARCHAR(255),
    is_active     BOOLEAN                  DEFAULT TRUE              NOT NULL,
    is_deleted    BOOLEAN                  DEFAULT FALSE             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
ALTER TABLE users OWNER TO postgres;

-- 2. Categories Table
CREATE TABLE categories
(
    id          SMALLINT GENERATED ALWAYS AS IDENTITY
        PRIMARY KEY,
    name        VARCHAR(100)          NOT NULL
        UNIQUE,
    description VARCHAR(255),
    is_deleted  BOOLEAN DEFAULT FALSE NOT NULL
);
ALTER TABLE categories OWNER TO postgres;

-- 3. Products Table
-- SKU generator for products (SH-0001, SH-0002, ...)
CREATE SEQUENCE product_sku_seq START 1;
ALTER SEQUENCE product_sku_seq OWNER TO postgres;
CREATE TABLE products
(
    id          UUID                     DEFAULT gen_random_uuid() NOT NULL
        PRIMARY KEY,
    category_id SMALLINT                                           NOT NULL
        REFERENCES categories,
    sku         VARCHAR(100)                                       NOT NULL
        UNIQUE,
    name        VARCHAR(255)                                       NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2)                                     NOT NULL
        CONSTRAINT products_price_check
            CHECK (price >= (0)::NUMERIC),
    is_active   BOOLEAN                  DEFAULT TRUE              NOT NULL,
    is_deleted  BOOLEAN                  DEFAULT FALSE             NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
ALTER TABLE products OWNER TO postgres;

-- 4. Product Variants Table
CREATE TABLE product_variants
(
    id             UUID    DEFAULT gen_random_uuid() NOT NULL
        PRIMARY KEY,
    product_id     UUID                              NOT NULL
        REFERENCES products,
    size           NUMERIC(4, 1)                     NOT NULL
        CONSTRAINT product_variants_size_check
            CHECK (size > (0)::NUMERIC),
    color          VARCHAR(50)                       NOT NULL,
    stock_quantity INTEGER DEFAULT 0                 NOT NULL
        CONSTRAINT product_variants_stock_quantity_check
            CHECK (stock_quantity >= 0),
    is_deleted     BOOLEAN DEFAULT FALSE             NOT NULL,
    UNIQUE (product_id, size, color)
);
ALTER TABLE product_variants OWNER TO postgres;

-- 5. Cart Items Table
CREATE TABLE cart_items
(
    id         UUID    DEFAULT gen_random_uuid() NOT NULL
        PRIMARY KEY,
    user_id    UUID                              NOT NULL
        REFERENCES users,
    variant_id UUID                              NOT NULL
        REFERENCES product_variants,
    quantity   INTEGER                           NOT NULL
        CONSTRAINT cart_items_quantity_check
            CHECK (quantity > 0),
    is_deleted BOOLEAN DEFAULT FALSE             NOT NULL,
    UNIQUE (user_id, variant_id)
);
ALTER TABLE cart_items OWNER TO postgres;

-- 6. Wishlists Table
CREATE TABLE wishlists
(
    user_id    UUID                  NOT NULL
        REFERENCES users,
    product_id UUID                  NOT NULL
        REFERENCES products,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    PRIMARY KEY (user_id, product_id)
);
ALTER TABLE wishlists OWNER TO postgres;

-- 7. Orders Table
CREATE TABLE orders
(
    id           UUID                     DEFAULT gen_random_uuid()       NOT NULL
        PRIMARY KEY,
    customer_id  UUID                                                     NOT NULL
        REFERENCES users,
    status       order_status             DEFAULT 'PENDING'::order_status NOT NULL,
    total_amount NUMERIC(10, 2)                                           NOT NULL
        CONSTRAINT orders_total_amount_check
            CHECK (total_amount >= (0)::NUMERIC),
    is_deleted   BOOLEAN                  DEFAULT FALSE                   NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP       NOT NULL
);
ALTER TABLE orders OWNER TO postgres;

-- 8. Order Items Table
CREATE TABLE order_items
(
    id         UUID    DEFAULT gen_random_uuid() NOT NULL
        PRIMARY KEY,
    order_id   UUID                              NOT NULL
        REFERENCES orders,
    variant_id UUID                              NOT NULL
        REFERENCES product_variants,
    quantity   INTEGER                           NOT NULL
        CONSTRAINT order_items_quantity_check
            CHECK (quantity > 0),
    unit_price NUMERIC(10, 2)                    NOT NULL
        CONSTRAINT order_items_unit_price_check
            CHECK (unit_price >= (0)::NUMERIC),
    is_deleted BOOLEAN DEFAULT FALSE             NOT NULL
);
ALTER TABLE order_items OWNER TO postgres;

-- 9. Payments Table
CREATE TABLE payments
(
    id         UUID                     DEFAULT gen_random_uuid()         NOT NULL
        PRIMARY KEY,
    order_id   UUID                                                       NOT NULL
        UNIQUE
        REFERENCES orders,
    method     payment_method                                             NOT NULL,
    amount     NUMERIC(10, 2)                                             NOT NULL
        CONSTRAINT payments_amount_check
            CHECK (amount >= (0)::NUMERIC),
    status     payment_status           DEFAULT 'PENDING'::payment_status NOT NULL,
    is_deleted BOOLEAN                  DEFAULT FALSE                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP         NOT NULL
);
ALTER TABLE payments OWNER TO postgres;

-- 10. Reviews Table
CREATE TABLE reviews
(
    id         UUID                     DEFAULT gen_random_uuid() NOT NULL
        PRIMARY KEY,
    product_id UUID                                               NOT NULL
        REFERENCES products,
    user_id    UUID                                               NOT NULL
        REFERENCES users,
    rating     SMALLINT                                           NOT NULL
        CONSTRAINT reviews_rating_check
            CHECK ((rating >= 1) AND (rating <= 5)),
    comment    TEXT,
    is_deleted BOOLEAN                  DEFAULT FALSE             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (user_id, product_id)
);
ALTER TABLE reviews OWNER TO postgres;
