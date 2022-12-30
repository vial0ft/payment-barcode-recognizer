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

(defn- recognize-qr-code
  [attrs schemas]
  (when-not (empty? schemas)
      (let [schema (first schemas)
            recognizing-rules (get-in schema [:qr-code :recognizing-scheme])]
        (if (compatible-by-scheme? recognizing-rules attrs)
          {:result (select-keys schema [:group :location :additional-info])}
            (recur attrs (next schemas))))))

(defn- attr-map
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

(defn- convert-sum [sum-str]
  (let [fractional-part-start-position (- (count sum-str) 2)
        integer-part (subs sum-str 0 fractional-part-start-position)
        fractional-part (subs sum-str fractional-part-start-position (count sum-str))]
    (bigdec
     (format "%s.%s" integer-part fractional-part))))

(defn- fetch-code-info [code-attrs-map]
  (let [{account "PersAcc"
         bill-id "QuittID"
         sum "Sum"
         } code-attrs-map]
    {:account account :bill-id bill-id :sum (convert-sum sum)}
    ))

(defn recognize-code
  "Try to recognize input `raw-qr-code-str` by `schemas` list
  Return first matched group as map:
  ```
  {
   :result (keys [:group :location :additional-info :code-info])
   :parsing-schema
  }
  ```
  or `nil`
  `:parsing-schema` has been used for grabbing attributes from string-like qr code"
  [raw-qr-code-str schemas]
  (when-let [code-attrs (attr-map raw-qr-code-str)]
    (let [{company-group :result} (recognize-qr-code code-attrs schemas)]
      {:result (assoc company-group :code-info (fetch-code-info code-attrs))
       :parsing-info code-attrs}
      )))
