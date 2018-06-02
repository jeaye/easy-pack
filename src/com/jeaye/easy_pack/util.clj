(ns com.jeaye.easy-pack.util
  (:require [clojure.spec.alpha :as s]))

(s/def ::non-empty-string (s/and string? not-empty))
(s/def ::path ::non-empty-string)
