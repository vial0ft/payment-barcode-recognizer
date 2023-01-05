(ns erkc.payment-barcode-recognizer.qr-code-processing-test
  (:require
   [erkc.payment-barcode-recognizer.test-utils :as utils]
   [erkc.payment-barcode-recognizer.recognize.qr-code-processing :as qr-proc]
   [erkc.payment-barcode-recognizer.recognize.barcode-schema-compatibility :as compatibility]
   [erkc.payment-barcode-recognizer.generators :as gens]
   [clojure.test :refer :all]))



(def ^:private raw-str-qr-code
  "ST00012|Name=some-name|PersonalAcc=123123|BankName=Super-Duper-BankName|BIC=000000|CorrespAcc=00000000|PayeeINN=1111111|Category=Category-name|Category1=3|Category2=1|TechCode=999999|QuittID=99999|PersAcc=123456789|PaymPeriod=000000|Sum=1000")


(deftest qr-code-processing-test
  (testing "Scheme compatibility by name of company"
    (let [{company-name :company-name} (gens/gen-qr-code-recognizing-rule-ny-company-name)
          fun (compatibility/schema-compatibility :qr-code :company-name company-name)
          compatible? (fun {"Name" company-name})]
      (is (= compatible? true)))
    )

  (testing "Recognizing qr-code raw string"
    (let [raw-str-qr-code "BRAH|Name=BLA BLA NAME|Sum=10000"
          qr-code-recognizing-scheme (gens/gen-recognizing-code-scheme :qr-code  {:company-name "BLA BLA NAME"})
          result (qr-proc/recognize-code raw-str-qr-code [qr-code-recognizing-scheme])]
      (is (contains? result :result)))
    )

 (testing "nil if there is no compatible scheme"
   (let [raw-str-qr-code "BRAH|Name=Unknown Company Name|Sum=10000"
         qr-code-recognizing-scheme (gens/gen-recognizing-code-scheme :qr-code  {:company-name "BLA BLA NAME"})
         result (qr-proc/recognize-code raw-str-qr-code [qr-code-recognizing-scheme])]
     (is (nil? result)))
   )
)
