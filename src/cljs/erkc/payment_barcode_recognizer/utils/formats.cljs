(ns erkc.payment-barcode-recognizer.utils.formats
  (:require
   [erkc.payment-barcode-recognizer.utils.big-decimal-utils :as bigdec]))



(defn amount-map
  [value]
  (bigdec/new (aget value "rep")))

(defn bill-id-map
  [value]
  (aget value "rep"))



