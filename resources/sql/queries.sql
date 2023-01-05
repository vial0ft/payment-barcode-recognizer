-- Place your queries here. Docs available https://www.hugsql.org/


-- :name get-barcode-by-bill-id :? :1
-- :doc returns a barcode by bill-id
SELECT bill_id, account, amount, created_at  FROM barcodes WHERE bill_id = :id


-- :name get-barcodes-by-account :? :*
-- :doc returns a barcodes by account
SELECT * FROM barcodes WHERE account  = :account

-- :name add-barcode :! :n
-- :doc add scanned barcode
INSERT INTO barcodes (bill_id, account, amount, barcode_info, created_at)
            VALUES (:bill_id, :account, :amount, :barcode_info::jsonb, :created_at)
