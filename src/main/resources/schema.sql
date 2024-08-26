create table SECURITY
(
    TICKER_ID         varchar(50)    not null,
    SECURITY_TYPE     varchar(30)    not null,
    LAST_STOCK_PRICE  decimal(10, 5) not null,
    LAST_TRADED_PRICE decimal(10, 5) not null,
    STOCK_RETURN      decimal(5, 2),
    ANNUAL_STD_DEV    decimal(5, 2),
    MATURITY          int,
    PRIMARY KEY (TICKER_ID)
);