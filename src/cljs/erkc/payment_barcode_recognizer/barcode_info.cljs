(ns erkc.payment-barcode-recognizer.barcode-info)


(def grid-item-style {
                      :background-color "rgba(255, 255, 255, 0.8)"
                      :border "1px solid rgba(0, 0, 0, 0.8)"
                      :padding "10px"
                      :font-size "16px"
                      :text-align "center"
                      })


(def grid-item-company-style grid-item-style)

(def grid-style {
                 :display "grid"
                 :grid-template-columns "auto auto auto"
                 :margin-right "20px"
                  })

(defn- HeaderRow []
  [
   [:div {:style grid-item-company-style} "Company"]
   [:div {:style grid-item-style} "Barcode Info"]
   [:div {:style grid-item-style} "Additional Info"]])

(defn- TableRow [company barcode-info additional-info]
  [
   [:div {:style grid-item-company-style} company]
   [:div {:style grid-item-style} barcode-info]
   [:div {:style grid-item-style} additional-info]])

(defn BarcodeInfo [group code-info additional-info]
  (-> [:div {:style grid-style}]
      (into (HeaderRow))
      (into (TableRow group code-info additional-info))
      ))
