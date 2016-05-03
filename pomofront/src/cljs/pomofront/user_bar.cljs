(ns pomofront.user-bar
  (:require [pomofront.interactions :refer [get-interaction interactions]]
            [ajax.core :refer [GET POST]]
            [reagent.session :as session]
            [reagent.core :as reagent]))

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

 (defn login-response-success [[[response]] email] ; interactions lib currently nests the arguments to an arr...
   (.log js/console "login response success" (clj->json response))
   (session/put! :token (response :token))
   (declare user-detail)
   [user-detail email])

(defn user-detail [email]
  [:div [:p "user: "[:strong email] ", juu aar nau lokked in " ]])

(defn login-form []
  (declare login-handler)
  (let [email (reagent/atom "")
        password (reagent/atom "")
        listener (interactions "user-bar" (fn [x] [login-handler @email @password]))]
    (fn []
      [:div
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Log in!" :on-click #(listener)}]])))

(defn login-response-fail [response]
  (.log js/console "login response fail" (clj->json response))
  [login-form])

(defn login-post [object on-success on-error]
  (POST "http://localhost:3000/api/session"
          {:body object
          :headers {:Content-Type "application/json"}
          :error-handler on-error
          :handler on-success}))

;; if the component always on first create runs the POST like it does now it will overwrite rejuvenated state
;; with a login using empty email and password
(defn login-handler [email password]
  (let [object (clj->json {:email email :password password})
        login-state (get-interaction {:name "login-state" :init-val [:div "Logging in..."]})
        on-success (interactions "login-state" (fn [x response] [login-response-success response email]))
        on-error (interactions "user-bar" (fn [x response] [login-response-fail response]))]
    (login-post object on-success on-error)
    (fn []
      @login-state)))

;; it all begins by rendering this component
(defn user-bar []
  @(get-interaction {:name "user-bar" :init-val [login-form]}))

