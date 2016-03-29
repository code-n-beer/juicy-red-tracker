(ns pomofront.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST]]
            [accountant.core :as accountant]))

;; Derpy functions
;; -------------------------

(defn register-handler [email password]
  (let [object (clj->js {:email email :password password})]
    (.log js/console object)
    (POST "http://localhost:3000/api/user" 
          {:body object})))

(defn login-handler [email password]
  (let [object (clj->js {:email email :password password})]
    (.log js/console object)
    (POST "http://localhost:3000/api/session" 
          {:body object})))


(defn atom-trim-str [strink]
  (-> @strink str clojure.string/trim))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to pomofront"]
   [:div 
    [:div [:a {:href "/about"} "go to about page"]]
    [:div [:a {:href "/register"} "go to register page"]]
    [:div [:a {:href "/login"} "go to login"]]
    ]])

(defn about-page []
  [:div [:h2 "About pomofront"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn register-page[]
  (let [email (reagent/atom "")
        password (reagent/atom "")
        register #(register-handler (atom-trim-str email) (atom-trim-str password))]
    (fn [props]
      [:div [:h2 "register?"]
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Register!" :on-click register}]
       [:div [:a {:href "/"} "go to the home page"]]])))

(defn login-page[]
  (let [email (reagent/atom "")
        password (reagent/atom "")
        login #(login-handler (atom-trim-str email) (atom-trim-str password))]
    (fn [props]
      [:div [:h2 "Login page"]
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Log in!" :on-click login}]
       [:div [:a {:href "/"} "go to the home page"]]])))


(defn current-page []
  [:div 
   [:div [(session/get :header)]]
  [:div [(session/get :current-page)]])
         ]]

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
  (mount-root))
