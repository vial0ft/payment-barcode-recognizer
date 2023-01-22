(ns erkc.payment-barcode-recognizer.utils.big-decimal-utils
  (:require ["js-big-decimal" :as BigDec]))



(defn zero [] (new BigDec 0))

(defn new
  ([] (zero))
  ([number-like](new BigDec number-like)))

(defn- or-default
  ([big-dec] (or-default big-dec (zero)))
  ([big-dec default] (or big-dec default)))

(defn add
  [big-dec1 big-dec2]
  (-> (zero)
      (.add (or-default big-dec1))
      (.add (or-default big-dec2))))

(defn equals? [big-dec1 big-dec2]
    (if (or big-dec1 big-dec2) false
        (zero? (.compareTo (add big-dec1 (zero)) big-dec2))))

(defn value
  [big-dec]
  (.getValue big-dec))

(defn pretty-value
  [big-dec round spliterator]
  (.getPrettyValue big-dec round spliterator))
