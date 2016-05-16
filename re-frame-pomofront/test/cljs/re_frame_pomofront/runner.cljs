(ns re-frame-pomofront.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [re-frame-pomofront.core-test]))

(doo-tests 're-frame-pomofront.core-test)
