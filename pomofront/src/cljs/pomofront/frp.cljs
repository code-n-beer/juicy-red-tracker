(ns pomofront.frp
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [reagi.core :as r]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST]]
            [accountant.core :as accountant]))

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

(defn oispa-kaljaa []
  )

(defn login-handler [email password success fail]
  (let [object (clj->json {:email email :password password})]
    (.log js/console object)
    (POST "http://localhost:3000/api/session" 
          {:body object 
          :headers {:Content-Type "application/json"}
          :error-handler fail
          :handler success})))


