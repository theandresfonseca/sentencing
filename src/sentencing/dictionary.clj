(ns sentencing.dictionary
  "functions that help generate the data dictionary"
  (:require [sentencing.common :as common]
            [sentencing.load :as load]))

(def sources
  {"jc" "judgement and conviction order"
   "psr" "pre-sentencing report"
   "jc/psr" "jc takes precedence"
   "sor" "statement of reasons"
   "psr/sor" "sor takes precedence"
   "plea" "plea agreement"
   "ind" "indictement/information"
   "r" "research variable"})
