(ns pomofront.core
  (:require [om.core :as om :include-macros true]
            [ajax.core :refer [GET POST]]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)


(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))


(GET "http://localhost:3000/api/user/dummy"
     {:handler handler
      :error-handler error-handler
      :keywords? true
      :response-format :json})


(defonce app-state (atom {:text "Hello Chestnut!"}))

(defn root-component [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               [(dom/div nil (dom/h1 nil (:text app)))
                (dom/div nil "ses")]))))

(om/root
 root-component
 app-state
 {:target (. js/document (getElementById "app"))})
