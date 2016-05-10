(ns re-frame-pomofront.users-pomodoros
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.session :refer [GET POST]]))

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))


(defn on-success [response]
 (re-frame/dispatch [:set-user response]))

(defn on-error [response]
  (js/console.log (clj->json response)))

(defn old-pomodoros []
  (GET "/api/user/" nil on-success on-error)
  (let [user-data (re-frame/subscribe [:user-data])]
    (fn []
      [:div 
       [:h2 "Your pomodoros"]
       (clj->json @user-data)])))

  
