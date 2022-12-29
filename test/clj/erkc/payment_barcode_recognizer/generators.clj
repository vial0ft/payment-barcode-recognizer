(ns erkc.payment-barcode-recognizer.generators
  (:require [clojure.test.check.generators :as gen]))



(def companies (gen/elements ["John & John" "Рога и Копыта" "Sam and sons" ]))

(defn gen-company-name
  [] (first (gen/sample companies 1)))

(defn gen-qr-code-recognizing-rule-ny-company-name
  ([] {:company-name (gen-company-name)})
  ([name] {:company-name name}))

(defn gen-recognizing-code-scheme
  ([code-type schema-trait] {code-type {:recognizing-scheme schema-trait}})
  ([code-type schema-trait & other-traits] {code-type
                                            {:recognizing-scheme
                                             (merge schema-trait (into (hash-map) other-traits))}}))


(defn gen-company-group-schema
  [group-name code-recognizing-schema]
  (merge {:group group-name} code-recognizing-schema))
