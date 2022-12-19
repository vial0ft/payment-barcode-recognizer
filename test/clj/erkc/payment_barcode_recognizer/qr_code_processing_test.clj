(ns erkc.payment-barcode-recognizer.qr-code-processing-test
  (:require
   [erkc.payment-barcode-recognizer.test-utils :as utils]
   [erkc.payment-barcode-recognizer.recognize.qr-code-processing :as qr-proc]
   [clojure.test :refer :all]))



(def ^:private raw-str-qr-code
  "ST00012|Name=some-name|PersonalAcc=123123|BankName=Super-Duper-BankName|BIC=000000|CorrespAcc=00000000|PayeeINN=1111111|Category=Category-name|Category1=3|Category2=1|TechCode=999999|QuittID=99999|PersAcc=123456789|PaymPeriod=000000|Sum=1000")

(deftest qr-code-processing-test
  (testing "A description of the test"
    (let [expectation {"PersonalAcc" "123123",
                       "TechCode" "999999",
                       "PersAcc" "123456789",
                       "Category" "Category-name",
                       "Sum" "1000",
                       "BankName" "Super-Duper-BankName",
                       "PayeeINN" "1111111",
                       "Category1" "3",
                       "Category2" "1",
                       "CorrespAcc" "00000000",
                       "QuittID" "99999",
                       "PaymPeriod" "000000",
                       "Name" "some-name",
                       "BIC" "000000"}]
(is (= (qr-proc/attr-map raw-str-qr-code) expectation))
      )))
