(ns erkc.payment-barcode-recognizer.linear-code-processing-test
  (:require
   [erkc.payment-barcode-recognizer.test-utils :as utils]
   [erkc.payment-barcode-recognizer.recognize.linear-code-processing :as linear-proc]
   [erkc.payment-barcode-recognizer.recognize.barcode-schema-compatibility :as compatibility]
   [erkc.payment-barcode-recognizer.generators :as gens]
   [clojure.test :refer :all]))





(deftest example-test
  (testing "Check compatibility linear code by length"
    (let [raw-str-code "000000"
          compatible? (compatibility/schema-compatibility :linear :barcode-length 6)]
      (is (compatible? raw-str-code))
    ))

  (testing "Check compatibility linear code by prefix"
    (let [raw-str-code "000000"
          compatible? (compatibility/schema-compatibility :linear :prefix ["000"])]
      (is (compatible? raw-str-code))
      ))

  (testing "Recognize group by schema"
    (let [raw-str-code "00000"
          linear-recognizing-scheme (gens/gen-recognizing-code-scheme :linear {:barcode-length 6
                                                                          :prefix ["000000"]})
          company-group-schema (gens/gen-company-group-schema :some-company linear-recognizing-scheme)
          result (linear-proc/recognize-code "000000"  [company-group-schema])]
      (is (contains? result :result))
    ))
 )
