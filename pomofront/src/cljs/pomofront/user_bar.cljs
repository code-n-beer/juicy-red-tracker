(ns pomofront.user-bar
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [reagi.core :as reagi]
            [cljs.core.async :as a :refer [<! chan]]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST]]
            [accountant.core :as accountant]))

;;interactions handler
;; -----------------------------------------------------------------
(def interaction (reagent/atom {}))

;; we create it here since it might be called again afterwards
(defn get-interaction [name-key]
  (.log js/console "soosiit")
  (if-not (@interaction name-key)
    (swap! interaction assoc name-key (reagi/events 1)))
  (.log js/console "aids" (@interaction name-key))
  (@interaction name-key))

;; interaction listener returns a function that when called 
;; causes a redraw of components relying on that interaction
(defn interaction-listener [interaction-name]
  (let [ch (chan)
        name-key (keyword interaction-name)]
    (fn [& args]
      (swap! (@interaction name-key) inc))))
  
; To be able to map things to the stream of 
(defn frp-test-puikula [stufz]
  (let [stream (->> (get-interaction "klikety")
       (reagi/map (fn [i]
                 (.log js/console "ses" i)
                 [:div "hai"])))
        ch (reagi/subscribe stream (chan))
        listener (interaction-listener "klikety")]
    (go 
      (<! ch)
      (listener))
    (.log js/console "asdfasfasfd" @stream)
    [:div "kek" @stream]))
                 ;ackc + 1))
       ;(reagi/map frp-test-component)))

(defn frp-test [asdf]
  [:div "moro"])

;(defn frp-test-component [stufz]
;  (let []
;    [:div
;     [:div "ohai" stufz]
;     [:button :on-click (interaction-listener "klikety")]]))



;interactions.get()
;.map()
;.ebin()
;.startWith()
;.map( bines => 
;            baynes)

;

;(let [stream (get-csv-stream "./resources/hourlist.csv")
;    ch (chan)]
;(r/subscribe stream ch)
;(<!! ch) => ["Scott Scala", "2", "2.3.2014","6:00","14:00"])))
;
;
;
;(defn get-csv-stream [path] 
;  (let [csv-stream (r/events)
;        file-contents (slurp path)
;        csv (parse-csv file-contents)]
;    (go (doall (map #(r/deliver csv-stream %) csv)))
;    csv-stream))
