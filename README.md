# payment-barcode-recognizer
Web service recognizing barcode of payment receipt. It works fine with linear barcodes and QR-codes.

Barcodes recognize according information in the file `resources/companies-schemas.edn`. It's possible to add new schemas and describe **how to recognize** some barcode by some **trait** (e.g. the name of the company) and also describe **parsing-schema** in the case of linear barcode. Specific of payment receipt recognizing is that the receipt have to contain `account`, `bill-id` and `amount`.


## Dependensies
### Server
- [kit-clj](https://kit-clj.github.io/) 
- [PosgreSQL](https://www.postgresql.org/) from [kit-postgres](https://clojars.org/io.github.kit-clj/kit-postgres) 

### Frontend
- [Reagent](https://reagent-project.github.io/)
- [html5-qrcode](https://github.com/mebjas/html5-qrcode)
- [cljs-ajax](https://github.com/JulianBirch/cljs-ajax)

## Build

As a standalone application `clojure -Sforce -T:build all`

## Production environment variables:

`PORT` - by default `3000`

`HTTP_HOST` - by default `0.0.0.0`

## Docker

For run as a docker container you have to set environment valiable `JDBC_URL` which is url to postgreSQL DB e.g:
```
jdbc:postgresql://<db-host>:<db-port>/<db-name>?user=<user>&password=<password>
```
For more details look into `Dockerfile`


## P.S.
That is naive implementation my previous java project with clojure. And it defenetly is !NOT READY FOR PRODUCTION USAGE! 
Unlike the previous desktop version, recognizing is a clojure web services with clojurescript and [Reagent](https://reagent-project.github.io/) on a frontend.
