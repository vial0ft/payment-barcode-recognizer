(ns erkc.payment-barcode-recognizer.recognize.qr-code-processing
  (:require [clojure.string :as str]))

(defn attr-map
  "Transform string-like qr-code info to map of attributes
  Format of string:
  ```<some-code>|<attr-key>=<attr-value>|...```
  "
  [raw-qrcode-str]
  (->>
   (-> (str/trim raw-qrcode-str) (str/split #"\|")) ;; split to "key=value" pairs
   (filter #(str/includes? % "="))
   (map #(str/split % #"=")) ;; split every pair by "="
   (flatten)
   (apply hash-map)))
