(ns erkc.payment-barcode-recognizer.recognize.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [erkc.payment-barcode-recognizer.recognize.qr-code-processing :as qr-code]
            [erkc.payment-barcode-recognizer.recognize.linear-code-processing :as linear]))


(def schema-file (io/file "./companies-schemas.edn"))
(def recognizing-schemas (edn/read (java.io.PushbackReader. (io/reader schema-file))))

(defn code-processing [code]
  ((if (str/includes? code "|")
     qr-code/recognize-code
     linear/recognize-code) code recognizing-schemas))
