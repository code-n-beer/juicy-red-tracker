(ns re-frame-pomofront.session
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :as ajax]))

(def api-url "http://localhost:3000")

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

(defn GET [path params on-success on-error]
  (let [token (re-frame/subscribe [:token])]
    (ajax/GET (str api-url path)
              {:params (to-json params)
               :headers {:Content-Type "application/json"
                         :accesstoken @token}
               :response-format :json
               :keywords? true
               :error-handler on-error
               :handler on-success})))

(defn POST [path object on-success on-error]
  (let [token (re-frame/subscribe [:token])]
    (ajax/POST (str api-url path)
               {:body (to-json object)
                :headers {:Content-Type "application/json"
                          :accesstoken @token}
                :response-format :json
                :keywords? true
                :error-handler on-error
                :handler on-success})))

(defn POST-un-authed [path object on-success on-error]
  (ajax/POST (str api-url path)
             {:body (to-json object)
              :headers {:Content-Type "application/json"}
              :response-format :json
              :keywords? true
              :error-handler on-error
              :handler on-success}))
