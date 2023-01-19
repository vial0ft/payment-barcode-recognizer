-- Place your queries here. Docs available https://www.hugsql.org/


-- :name get-barcode-by-bill-id :? :1
-- :doc returns a barcode by bill-id
SELECT bill_id, account, amount, created_at, "group", location
 FROM barcodes
 WHERE bill_id = :id::bigint


-- :name get-barcodes-by-account :? :*
-- :doc returns a barcodes by account
SELECT bill_id, account, amount, created_at, "group", location
 FROM barcodes
 WHERE account  = :account::int

-- :name get-barcodes-by-period :? *
-- :doc returns a barcodes by period from :begin-date to :end-date
SELECT bill_id as "bill-id", account, amount, created_at as "created-at", "group", location
 FROM barcodes
 WHERE created_at BETWEEN :begin-date::timestamp AND :end-date::timestamp


-- :name add-barcode :! :n
-- :doc add scanned barcode
INSERT INTO barcodes (bill_id, account, amount, barcode_info, created_at, "group", location)
            VALUES (:bill-id, :account, :amount, :barcode-info::jsonb, :created-at, :group, :location)
