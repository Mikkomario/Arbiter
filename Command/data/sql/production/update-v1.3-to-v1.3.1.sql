--
-- Type: Update
-- Origin: v1.3
-- Version: v1.3.1
--

-- Invoice product delivery is now a date range instead of a single date
ALTER TABLE invoice
    ADD `product_delivery_begin` DATE AFTER payment_duration_days,
    ADD `product_delivery_end` DATE AFTER product_delivery_begin;

UPDATE invoice SET product_delivery_begin=product_delivery_date, product_delivery_end=product_delivery_date;

ALTER TABLE invoice
    DROP COLUMN product_delivery_date;