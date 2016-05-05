(ns pomofront.interactions
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce interaction (reagent/atom {}))

;; we create it here since it might be called again afterwards
(defn get-state [{:keys [name init-val]}]
  (let [name-key (keyword name)]
  (if-not (@interaction name-key)
    (swap! interaction assoc name-key (reagent/atom init-val)))
  (@interaction name-key)))

(defn act [funcs actable & args]
  (let [result
        (reduce
          (fn [acc func]
            (func acc args))
          @actable
          funcs)]
    (reset! actable result)
    actable))

;; interaction listener returns a function that when called
;; causes a redraw of components relying on that interaction
(defn set-state [interaction-name & funcs]
  (let [name-key (keyword interaction-name)]
    (fn [& args]
      (act funcs (@interaction name-key) args))))
