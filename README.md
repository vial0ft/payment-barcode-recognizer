# payment-barcode-recognizer
Web service recognizing barcode of payment receipt. It works fine with linear barcodes and QR-codes.

Barcodes recognize according information in the file `resources/companies-schemas.edn`. It's possible to add new schemas and describe **how to recognize** some barcode by some **trait** (e.g. the name of the company) and also describe **parsing-schema** in the case of linear barcode. Specific of payment receipt recognizing is that the receipt have to contain `account`, `bill-id` and `amount`.


## Dependensies
### Server
- kit-clj
- PosgreSQL DB

### Frontend
- Reagent
- html5-qrcode
- cljs-ajax

## Build

`clojure -Sforce -T:build all`


## P.S.
That is naive implementation my previous java project with clojure. And it defenetly is !NOT READY FOR PRODUCTION USAGE! 
Unlike the previous desktop version, recognizing is a clojure web services with clojurescript and [Reagent](https://reagent-project.github.io/) on a frontend.
It can be deployed as a docker container.
