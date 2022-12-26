(ns erkc.payment-barcode-recognizer.recognize.barcode-schema-compatibility
  (:require [clojure.string :as str]))


(defmulti schema-compatibility (fn [schema-code-type schema-key _schema-value] [schema-code-type schema-key]))

(defmethod schema-compatibility [:qr-code :company-name] [_ _ schema-name]
  (fn [code-attr]
    (let [code-name (get code-attr "Name")]
      (if (nil? code-name) false
          (= code-name schema-name)))))

(defmethod schema-compatibility [:linear :barcode-length] [_ _ expected-barcode-length]
  (fn [raw-linear-barcode]
    (= (count raw-linear-barcode) expected-barcode-length)))


(defmethod schema-compatibility [:linear :prefix] [_ _ prefixes]
  (fn [raw-linear-barcode]
    (some #(str/starts-with? % raw-linear-barcode) prefixes)))

(defmethod schema-compatibility :default [_ _ _] (fn [_] false))

