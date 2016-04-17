(ns pomofront.user-bar
  (:require [pomofront.interactions :refer [get-interaction interactions]]))

(defn frp-thing []
      [:div "The count is:" @(get-interaction {:name "clicker" :init-val 0})
       [:div
       [:button {:on-click (interactions "clicker" inc)} "up"]
       [:button {:on-click (interactions "clicker" dec)} "down"]]])

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
;(defn atom-trim-str [strink]
;  (-> @strink str clojure.string/trim))

;-------------
(defn user-detail []
  [:div "juu aar nau lokked in"])

(defn submit-interaction []
  (.log js/console "tryin loggin")
  (.log js/console "sinep")
  (interactions "user-bar"
                (fn [oldval] 
                  (.log js/console "ohai")
                  (user-detail))))

(defn submit [listener]
  (fn [e]
    (.preventDefault e)
    (listener)))

(defn login-form []
  [:form {:on-submit (submit (submit-interaction))}
   [:input {:type "text" :name "email"}]
   [:input {:type "text" :name "password"}]
   [:input {:type "submit" :value "Log in nao?"}]])

(defn user-bar []
  @(get-interaction {:name "user-bar" :init-val (login-form)}))

