(ns re-frame-pomofront.pomodoro
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.users-pomodoros :refer [old-pomodoros]]
            [re-frame-pomofront.session :refer [GET POST]]
            ))

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

(defn on-error [response]
  (js/console.log (to-json response)))

(defn text-input [data-atom]
  [:input {:type "text" :value @data-atom :on-change #(reset! data-atom (-> % .-target .-value))}])

(defn start-pomodoro [name category length]
  (re-frame/dispatch [:start-pomodoro name category length]))
; (fn [db [name category time]]


(defn new-pomodoro []
  (let [length (atom 25)
        pomodoro-name (atom "")]
    (fn []
      [:div
       [:h4 "New pomodoro"]

       [:div "Length: " [text-input length] " minutes. "]

       [:div "Name: " [text-input pomodoro-name]]

       [:input {:type "button" :value "start" :on-click #(start-pomodoro @pomodoro-name "ses" @length)}]])))


(defn get-categories []
  (GET "/api/user/category/" nil #(re-frame/dispatch [:update-categories %]) on-error))

(defn new-category-success [response cat-name]
  (js/console.log (to-json response))
  (get-categories))

(defn post-category [cat-name]
  (POST "/api/user/category/" {:name cat-name} #(new-category-success % cat-name) on-error))

(defn new-category []
  (let [category-name (atom "")]
    (fn []
      [:div
       [:h4 "New category"]
       [:div "Name: " 
        [text-input category-name]]
       [:input {:type "button" :value "create" :on-click #(post-category @category-name)}]])))

(defn running-pomodoro []
  (let [running-pomodoro (re-frame/subscribe [:running-pomodoro])]
    (fn []
      [:div
       [:h4 "Running pomodoro"]
       (if (some? @running-pomodoro)
         [:div "has current pomo " @running-pomodoro])
       [:div ""]
       ])))

(defn pomodoro []
  [:div 
   [new-pomodoro]
   [running-pomodoro]
   [new-category]
   [old-pomodoros]])
