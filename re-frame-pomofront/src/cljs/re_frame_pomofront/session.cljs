(ns re-frame-pomofront.session
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.db :as db]
            [ajax.core :as ajax]))

(def api-url "http://hypsy.fi:3001")


(defn GET [path params on-success on-error]
  (ajax/GET (str api-url path)
        {:params params 
         :headers {:Content-Type "application/json"}
         :error-handler on-error
         :response-format :json
         :keywords? true
         :handler on-success}))

(defn POST [path object on-success on-error]
  (ajax/POST (str api-url path)
        {:body object
         :headers {:Content-Type "application/json"}
         :error-handler on-error
         :response-format :json
         :keywords? true
         :handler on-success}))
