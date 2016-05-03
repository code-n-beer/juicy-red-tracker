(ns pomofront.user-bar
  (:require [pomofront.interactions :refer [get-interaction interactions]]
            [ajax.core :refer [GET POST]]
            [reagent.session :as session]
            [reagent.core :as reagent]))
;
;(defn register-handler [email password]
;  (let [object (clj->json {:email email :password password})]
;    (.log js/console object)
;    (POST "http://localhost:3000/api/user" 
;          {:body object
;          :headers {:Content-Type "application/json"}
;          :error-handler response-fail
;          :handler register-response-success})))
;
;(defn register-response-success [response]
;  (.log js/console (clj->json response)))
;

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

 (defn login-response-success [response email]
   (.log js/console "login success!")
   (.log js/console (clj->json response))
   (.log js/console "print response")
   ;(session/put! :token (response :token))
   (.log js/console "session put")
   (declare user-detail)
   (.log js/console "return user-detail")
   [user-detail email]
   ;(session/put! :header #'user-header)
   )

(defn user-detail [email]
  (fn []
      [:div [:p "juu aar nau lokked in " [:strong email]]]))

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

(defn response-fail [response]
  (.log js/console "response fail" (clj->json response)))

(defn login-response-fail [response]
  (.log js/console "response fail" (clj->json response))
  [login-form])

(defn user-bar []
  (let [content (get-interaction {:name "user-bar" :init-val [login-form]})]
    (fn []
      @content)))

;; if the component always on first create runs the POST like it does now it will overwrite rejuvenated state
;; with a login using empty email and password
(defn login-handler [email password]
  (let [object (clj->json {:email email :password password})
        login-state (get-interaction {:name "login-state" :init-val [:div "Logging in..."]})]
    (.log js/console object)
    (POST "http://localhost:3000/api/session" 
          {:body object 
          :headers {:Content-Type "application/json"}
          :error-handler (interactions "user-bar" (fn [x response] [login-response-fail response]))
          :handler (interactions "login-state" (fn [x response] [login-response-success response email]))})
    (fn [] 
      @login-state)))
