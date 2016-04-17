(ns pomofront.interactions
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [reagi.core :as reagi]
            [cljs.core.async :as a :refer [<! ]]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST]]
            [accountant.core :as accountant]))

;; frp
;; -----------------------
(defonce interaction (reagent/atom {}))

;; we create it here since it might be called again afterwards
(defn get-interaction [{:keys [name init-val]}]
  (let [name-key (keyword name)]
  (if-not (@interaction name-key)
    (swap! interaction assoc name-key (reagent/atom init-val)))
  (@interaction name-key)))

(defn act [funcs actable]
  (.log js/console "actin")
  (let [result
        (reduce
          (fn [acc func]
            (func acc))
          @actable
          funcs)]
    (reset! actable result)
    actable))

;; interaction listener returns a function that when called
;; causes a redraw of components relying on that interaction
(defn interactions [interaction-name & funcs]
  (.log js/console "interactions asdfasdf")
  (let [name-key (keyword interaction-name)]
    (fn [& args]
      (act funcs (@interaction name-key)))))
