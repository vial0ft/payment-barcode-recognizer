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

  (testing "Check compatibility linear code by schema"
    (let [raw-str-code "000000"
          compatible-result (linear-proc/compatible-by-scheme? raw-str-code {:prefix ["000"]})]
      (is compatible-result)
      ))

  (testing "Getting attributes from raw-str-code"                                           ;;
    (let [raw-str-code "000001234560000000001234567890012345"                               ;;
          attributes (linear-proc/attr-map raw-str-code {                                   ;;
                                                          :account { :offset 5 :count 6 }   ;;
                                                          :id-bill { :offset 11 :count 18 } ;;
                                                          :sum-bill { :offset 29 :count 7 } ;;
                                                          })]                               ;;
      (is (= (:account attributes) "123456"))                                             ;;
      (is (= (:id-bill attributes) "000000000123456789"))                                 ;;
      (is (= (:sum-bill attributes) "0012345"))                                           ;;
      ))

  (testing "Recognize group by schema"
    (let [raw-str-code "00000"
          linear-recognizing-scheme (gens/gen-recognizing-code-scheme :linear {:barcode-length 6
                                                                          :prefix ["000000"]})
          company-group-schema (gens/gen-company-group-schema :some-company linear-recognizing-scheme)
          result (linear-proc/recognize-linear-code "000000"  [company-group-schema])]
      (is (contains? result :result))
    ))
 )
