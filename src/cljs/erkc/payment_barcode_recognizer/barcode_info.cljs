(ns erkc.payment-barcode-recognizer.barcode-info)

(defn- HeaderRow []
  [
   [:div.last-barcode-grid-item "Company"]
   [:div.last-barcode-grid-item "Barcode Info"]
   [:div.last-barcode-grid-item "Additional Info"]])

(defn- TableRow [company barcode-info additional-info]
  [
   [:div.last-barcode-grid-item company]
   [:div.last-barcode-grid-item barcode-info]
   [:div.last-barcode-grid-item additional-info]])

(defn LastBarcodeInfo [{:keys [group-info code-info additional-info]}]
  [:div {:style {
                 :position "fixed"
                 :bottom "1%"
                 :width "100%"
                 :opacity 1
                 }}
   [:h4 "Last recognized:"]
   (-> [:div.last-barcode-grid-container]
       (into (HeaderRow))
       (into (TableRow group-info code-info additional-info))
       )
   ])
