(ns pomofront.user-bar
  (:require [pomofront.interactions :refer [get-interaction interactions]]
            [reagent.core :as reagent]))

;(defn clj->json [d]
;  (.stringify js/JSON (clj->js d)))
;
; (defn login-response-success [response]
;   (.log js/console (clj->json response))
;   (session/put! :token (response :token))
;   (declare user-header)
;   (session/put! :header #'user-header))
;
;(defn register-response-success [response]
;  (.log js/console (clj->json response)))
;
;(defn response-fail [response]
;  (.log js/console (clj->json response)))
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
;(defn login-handler [email password]
;  (let [object (clj->json {:email email :password password})]
;    (.log js/console object)
;    (POST "http://localhost:3000/api/session" 
;          {:body object 
;          :headers {:Content-Type "application/json"}
;          :error-handler response-fail
;          :handler login-response-success})))
;
;
(defn atom-trim-str [strink]
  (-> @strink str clojure.string/trim))

(defn user-detail []
  (fn []
      [:div [:p "juu aar nau lokked in"]]))

(defn login-handler [email password]
  (.log js/console @email @password)
  ;(atom-trim-str email)
  ;(atom-trim-str password)
  (user-detail))

(defn login-form []
  (let [email (reagent/atom "")
        password (reagent/atom "")
        listener (interactions "user-bar" (fn [x] [user-detail]))]
    (fn []
      [:div
       [:input {:type "text" :value @email :on-change #(reset! email (-> % .-target .-value))}]
       [:input {:type "text" :value @password :on-change #(reset! password (-> % .-target .-value))}]
       [:input {:type "button" :value "Log in!" :on-click #(listener)}]])))

(defn user-bar []
  (.log js/console "derpa herp")
  (let [content (get-interaction {:name "user-bar" :init-val [login-form]})]
    (fn []
      (.log js/console "hurpa d;rp")
      (.log js/console @content)
      (.log js/console "soosit")
      @content)))
;        interaction @(get-interaction {:name "user-bar" :init-val form})]))

