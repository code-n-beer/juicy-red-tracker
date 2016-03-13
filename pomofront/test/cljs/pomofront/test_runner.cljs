(ns pomofront.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [pomofront.core-test]))

(enable-console-print!)

(doo-tests 'pomofront.core-test)
