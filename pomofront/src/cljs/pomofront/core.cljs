(ns pomofront.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST]]
            [pomofront.user-bar :refer [get-login-stream-component]]
            [accountant.core :as accountant]))

;; Derpy functions
;; -------------------------

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

(defn login-response-success [response]
  (.log js/console (clj->json response))
  (session/put! :token (response :token))
  (declare user-header)
  (session/put! :header #'user-header))

(defn register-response-success [response]
  (.log js/console (clj->json response)))

(defn response-fail [response]
  (.log js/console (clj->json response)))

(defn register-handler [email password]
  (let [object (clj->json {:email email :password password})]
    (.log js/console object)
    (POST "http://localhost:3000/api/user" 
          {:body object
          :headers {:Content-Type "application/json"}
          :error-handler response-fail
          :handler register-response-success})))

(defn login-handler [email password]
  (let [object (clj->json {:email email :password password})]
    (.log js/console object)
    (POST "http://localhost:3000/api/session" 
          {:body object 
          :headers {:Content-Type "application/json"}
          :error-handler response-fail
          :handler login-response-success})))


(defn atom-trim-str [strink]
  (-> @strink str clojure.string/trim))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Clojodoro"]
   [:div 
    [:div [:a {:href "/about"} "About"]]
    [:div [:a {:href "/register"} "Register"]]
    ]])

(defn user-header []
  [:div [:h6 "Hai user, TODO: show username"]])

(defn about-page []
  [:div [:h2 "About Clojodoro"]
   [:div [:a {:href "/"} "Back home"]]])

(defn register-page[]
  (let [email (reagent/atom "")
        password (reagent/atom "")
        register #(register-handler (atom-trim-str email) (atom-trim-str password))]
    (fn [props]
      [:div [:h2 "register?"]
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Register!" :on-click register}]
       [:div [:a {:href "/"} "Back home"]]])))

(defn login-page[]
  (let [email (reagent/atom "")
        password (reagent/atom "")
        login #(login-handler (atom-trim-str email) (atom-trim-str password))]
    (fn [props]
      [:div
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Log in!" :on-click login}]])))


(defn current-page []
  [:div 
   [:div [(session/get :header)]]
   [:div [(session/get :current-page)]]
   ])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

(secretary/defroute "/register" []
  (session/put! :current-page #'register-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  ;(session/put! :header #'login-page)
  (session/put! :header #'get-login-stream-component)
  (mount-root))
