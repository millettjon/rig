(ns rig.git
  (:require
   [rig.shell :refer [$>]]))

;; FIXME return nil instead of throwing when not in a git repo
(defn top-level
  []
  ($> "git rev-parse --show-toplevel"))
