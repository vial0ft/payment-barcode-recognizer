(ns erkc.payment-barcode-recognizer.generators
  (:require [clojure.test.check.generators :as gen]))



(def companies (gen/elements ["John & John" "Рога и Копыта" "Sam and sons" ]))

(defn gen-company-name
  [] (first (gen/sample companies 1)))

(defn gen-qr-code-recognizing-rule-ny-company-name
  ([] {:company-name (gen-company-name)})
  ([name] {:company-name name}))

(defn gen-qr-code-scheme
  ([] {:qr-code {:recognizing-scheme (gen-qr-code-recognizing-rule-ny-company-name)}})
  ([schema-trait] {:qr-code {:recognizing-scheme schema-trait}})
  ([schema-trait & other-traits] {:qr-code {:recognizing-scheme (merge schema-trait (into (hash-map) other-traits))}}))


(defn gen-company-group-schema
  [group-name qr-code-recognizing-schema]
  (merge {:group group-name} qr-code-recognizing-schema))
