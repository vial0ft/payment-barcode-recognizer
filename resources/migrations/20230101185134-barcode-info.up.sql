CREATE TABLE IF NOT EXISTS barcodes (
	bill_id bigint  PRIMARY KEY,
	account int  NOT NULL,
	amount numeric(6, 2) NOT NULL,
	barcode_info jsonb,
	created_at timestamp DEFAULT now()
);
