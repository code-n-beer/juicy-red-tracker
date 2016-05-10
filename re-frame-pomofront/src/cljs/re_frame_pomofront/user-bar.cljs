(ns re-frame-pomofront.user-bar
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.session :refer [POST-un-authed]]))
            ;[ajax.core :refer [GET POST]]))

(def host (str js/window.location.host))

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

(defn on-error [response]
  (js/console.log "don't do much I guess")
  (js/console.log response))

(defn on-success [response]
  (re-frame/dispatch [:login-success (response :token) :user-detail]))

(defn login-post [email passwd]
  (POST-un-authed "/api/session"
        {:email email :password passwd}
        on-success
        on-error))

(defn login-form []
  (let [email (atom "")
        password (atom "")]
    (fn []
      [:div
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Log in!" :on-click #(login-post @email @password)}]])))

(defn logged-in []
  (let [token (re-frame/subscribe [:token])]
    (fn []
      [:div "Hi user, here's your accesstoken: " @token])))

(defmulti bars identity)
(defmethod bars :login [] [login-form])
(defmethod bars :user-detail [] [logged-in])
(defmethod bars :default [] [:div "Loading user..."])

(defn show-bar
  [bar-name]
  [bars bar-name])

(defn user-bar [props]
  (let [current-bar (re-frame/subscribe [:current-bar])]
    (fn []
      [:div "heyoo, I'm an obnoxious bar that is always visible at the top of the page"
       [show-bar @current-bar]])))
