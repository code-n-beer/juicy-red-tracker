(ns re-frame-pomofront.views
    (:require [re-frame.core :as re-frame]
              [re-frame-pomofront.user-bar :refer [user-bar]]
              [re-frame-pomofront.session :refer [POST-un-authed]]
              [reagent.core :as reagent :refer [atom]]
              [re-frame-pomofront.pomodoro :refer [pomodoro]]))

;; convenience
(defn text-input [data-atom]
  [:input {:type "text" :value @data-atom :on-change #(reset! data-atom (-> % .-target .-value))}])

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

(defn on-error [response]
  (js/console.log (to-json response)))


;; home
(defn home-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div
       [:div "I guess this page could have a bunch of shit about how awesome the service will be once you log in."]
       [:div "Or, we could save pomodoros locally, and once user logs in they're saved into their account"]
       [:div [:a {:href "#/register"} "Register"]]])))


;; login after register. pls refactor dis somewhere else :/
(defn on-success [response]
  (re-frame/dispatch [:login-success (response :token) :user-detail]))

(defn login-post [email passwd]
  (POST-un-authed "/api/session"
        {:email email :password passwd}
        on-success
        on-error))


;; register 
(defn register-success [email password]
  (js/console.log "register success!!!")
  (re-frame/dispatch [:set-active-panel :home-panel])
  (login-post email password))

(defn register-post [email password]
  (POST-un-authed "/api/user" {:email email :password password} #(register-success email password) on-error))

(defn register-panel []
  (let [email (atom "")
        password (atom "")]
    (fn []
      [:div 
       [:h2 "Register???"]
       [:div "Email" [text-input email]]
       [:div "Password" [text-input password]]
       [:input {:type "button" :value "Register!" :on-click #(register-post @email @password)}]
       [:div [:a {:href "#/"} "go to Home Page"]]])))

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :register-panel [] [register-panel])
(defmethod panels :pomodoro-panel [] [pomodoro])
(defmethod panels :default [] [:div])

(defn show-panel
  [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div [user-bar] [show-panel @active-panel]])))
