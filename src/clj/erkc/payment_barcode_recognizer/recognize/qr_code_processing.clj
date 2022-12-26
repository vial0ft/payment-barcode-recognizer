(ns erkc.payment-barcode-recognizer.recognize.qr-code-processing
  (:require [clojure.string :as str]
            [erkc.payment-barcode-recognizer.recognize.barcode-schema-compatibility :as compatibility]))

(defn- compatible-by-scheme? [schema attrs]
  (if (empty? schema) false
  (loop [schema-checkers  schema
         attr-map attrs]
    (if (empty? schema-checkers) true
      (let [[rule-name args] (first schema-checkers)
            checker-func (compatibility/schema-compatibility :qr-code rule-name args)
            comparatible? (checker-func attr-map)]
        (if (not comparatible?) false
            (recur (next schema-checkers) attr-map))
        ))
   )))

(defn attr-map
  "Transform string-like qr-code info to map of attributes
  Format of string:
  ```<some-code>|<attr-key>=<attr-value>|...```
  "
  [raw-qr-code-str]
  (->>
   (-> (str/trim raw-qr-code-str) (str/split #"\|")) ;; split to "key=value" pairs
   (filter #(str/includes? % "="))
   (map #(str/split % #"=")) ;; split every pair by "="
   (flatten)
   (apply hash-map)))


(defn recognize-qr-code
  "Try to recognize input `raw-rq-code-str` by `schemas` list
  Return first matched group as map:
  ```{:result (keys [:group :location :additional-info])}}```
  or ```{:error \"Cannot recognize code\"}```"
  [attrs schemas]
  (if (or (empty? schemas) (empty? attrs)) {:error "Cannot recognize code"}
      (let [schema (first schemas)
            recognizing-rules (get-in schema [:qr-code :recognizing-scheme])]
        (if (compatible-by-scheme? recognizing-rules attrs)
          {:result (select-keys schema [:group :location :additional-info])}
            (recur attrs (next schemas))))))