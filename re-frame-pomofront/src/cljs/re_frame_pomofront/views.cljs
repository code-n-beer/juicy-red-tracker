(ns re-frame-pomofront.views
    (:require [re-frame.core :as re-frame]
              [re-frame-pomofront.user-bar :refer [user-bar]]
              [re-frame-pomofront.pomodoro :refer [pomodoro]]))

;; home

(defn home-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div
       [:div "I guess this page could have a bunch of shit about how awesome the service will be once you log in."]
       [:div "Or, we could save pomodoros locally, and once user logs in they're saved into their account"]
       [:div [:a {:href "#/about"} "go to About Page"]]])))


;; about

(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :pomodoro-panel [] [pomodoro])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div [user-bar] [show-panel @active-panel]])))
