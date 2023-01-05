(ns erkc.payment-barcode-recognizer.recognize.core
  (:require
   [clojure.string :as str]
   [erkc.payment-barcode-recognizer.recognize.qr-code-processing :as qr-code]
   [erkc.payment-barcode-recognizer.recognize.linear-code-processing :as linear]))


(defn- convert-amount [{amount-str :amount :as code-info}]
  (let [amount-str-length (count amount-str)
        fractional-part-start-position (- amount-str-length 2)
        integer-part (subs amount-str 0 fractional-part-start-position)
        fractional-part (subs amount-str fractional-part-start-position amount-str-length)]
    (assoc code-info
           :amount
           (bigdec (format "%s.%s" integer-part fractional-part)))))

(defn- convert-account [{account-str :account :as code-info}]
  (assoc code-info
         :account 
         (parse-long account-str)))

(defn- convert-bill-id [{bill-id-str :bill-id :as code-info}]
  (assoc code-info
         :bill-id 
         (bigint bill-id-str)))


(defn code-processing [code recognizing-schemas]
  (when-let [recognized-barcode ((if (str/includes? code "|")
                                   qr-code/recognize-code
                                   linear/recognize-code)
                                 code recognizing-schemas)]
    (assoc-in recognized-barcode [:result :code-info]
              (-> (get-in recognized-barcode [:result :code-info])
                  (convert-bill-id)
                  (convert-account)
                  (convert-amount)))))
