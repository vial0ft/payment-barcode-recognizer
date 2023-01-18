(ns erkc.payment-barcode-recognizer.recognize.linear-code-processing
  (:require [erkc.payment-barcode-recognizer.recognize.barcode-schema-compatibility :as compatibility]
            [clojure.string :as str]))

(defn- compatible-by-scheme? [raw-str-linear-code recognizing-schema]
  (if (empty? recognizing-schema) false
      (loop [schema-checkers  recognizing-schema
             raw-str-code raw-str-linear-code]
        (if (empty? schema-checkers) true
            (let [[rule-name args] (first schema-checkers)
                  checker-func (compatibility/schema-compatibility :linear rule-name args)
                  comparatible? (checker-func raw-str-code)]
              (if (not comparatible?) false
                  (recur (next schema-checkers) raw-str-code))
              )))
      ))

(defn- recognize-linear-code
  "Try to recognize input `raw-str-linear-code` by `schemas` list
  Return first matched group as map:
  ```
  {
   :result (keys [:group :location :additional-info :code-info])
   :parsing-schema
  }
  ```
  or `nil`

  `:parsing-schema` has been used for grabbing attributes from string-like linear code"

  [raw-str-linear-code schemas]
  (when-not (empty? schemas)
    (let [schema (first schemas)
          recognizing-rules (get-in schema [:linear :recognizing-scheme])]
      (if (compatible-by-scheme? raw-str-linear-code recognizing-rules)
        {:result {
                  :group-info (select-keys schema [:group :location])
                  :additional-info (:additional-info schema)
                  }
         :parsing-schema (get-in schema [:linear :parsing-scheme])}
        (recur raw-str-linear-code (next schemas))))))

(defn- attr-map
  "Grabbing information from string-like linear code by `parsing-schema`
   Example of parsing-schema:
   ```
   {
    :account { :offset 5 :count 6 }
    :id-bill { :offset 11 :count 18 }
    :amount  { :offset 29 :count 7 }
   }
   ```
   as result return a map with keys `[:account :id-bill :amount]`
   or `nil`"

  [raw-str-linear-code parsing-schema]
  (when parsing-schema
      (reduce-kv
       (fn [m k v]
         (let [{offset :offset
                count :count} v]
           (assoc m k (subs raw-str-linear-code offset (+ offset count)))))
       {} parsing-schema)))

(defn recognize-code
  "Try to recognize input `raw-str-linear-code` by `schemas` list
  Return first matched group as map:
  ```
  {:result (keys [:group :location :additional-info :code-info])
   :parsing-info ;; all information has been gotten from linear-code by schema
  }```
  or `nil`"
  [raw-str-linear-code schemas]
  (when-let [recognized-code-group (recognize-linear-code raw-str-linear-code schemas)]
    (let [{ company-group :result
            parsing-schema :parsing-schema } recognized-code-group
          code-attrs (attr-map raw-str-linear-code parsing-schema)]
      {:result (assoc company-group :code-info code-attrs)
       :parsing-info code-attrs})
    ))
