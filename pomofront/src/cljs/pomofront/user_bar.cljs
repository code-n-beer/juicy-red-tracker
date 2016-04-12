(ns pomofront.user-bar
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [reagi.core :as reagi]
            [clojure.core.async :as a :refer [<!! chan]]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST]]
            [accountant.core :as accountant]))

;;interactions handler
;; -----------------------------------------------------------------
(defn interaction (reagent/atom {}))

(defn get-interaction [name-key]
  (if-not (@interaction name-key)
    (swap! interaction assoc name-key (reagent/atom 1)))
  (@interaction name-key))

;; interaction listener returns a function that when called 
;; causes a redraw of components relying on that interaction
(defn interaction-listener [interaction-name]
  (let [ch (chan)
        name-key (keyword interaction-name)]
    (fn [& args]
      (swap! (@interaction name-key) inc))))
  

(defn interactions [interaction-name]
  )


;; UI markup
;; -----------------------------------------------------------------
(defn login-component [{:keys [submit]}] ;; destructured react props
  [:form {:on-submit (fn [e] 
                       (.preventDefault e)
                       (submit e))}
   [:input {:type "text" :name "email"}]
   [:input {:type "text" :name "password"}]
   [:input {:type "submit" :value "Get out!"}]])

(defn user-data-component [{:keys [token]}]
  [:div "terve terve juuser" token])

;; Other stuff
;; -----------------------------------------------------------------
(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

(defn decide-component [props]
  (.log js/console "decide component props")
  (.log js/console props)
  (if (props :token)
    (user-data-component props)
    (login-component props)))

(defn login-handler [email password success fail]
  (let [object (clj->json {:email email :password password})]
    (.log js/console object)
    (POST "http://localhost:3000/api/session" 
          {:body object 
          :headers {:Content-Type "application/json"}
          :error-handler fail
          :handler success})))

;;(defn login-handler-handler [{:keys [email password]}] ;;TODOfindout how to actually get them from the form
(defn login-handler-handler [event-data] ;;TODOfindout how to actually get them from the form
  (.log js/console "called login handler handler ses")
  (.log js/console event-data)
  {:token "penis :D"})
;;(login-handler email password))

(defn get-login-stream-component []
  (let [stream (reagi/events {})
        ;submit #(->> (login-handler-handler %) (r/deliver stream))]
        submit #(->> (login-handler-handler %) (.log js/console "hhhhhhhhh"))]
    (fn []
      (let [token (@stream :token)        
            props {:token token :submit submit}]
        (.log js/console "drawing mister login stream")
        (.log js/console (props :token))
        (decide-component props)))))






;interactions piilottaa tilamutaatiot
; 


interactions.get()
.map()
.ebin()
.startWith()
.map( bines => 
            baynes)

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
