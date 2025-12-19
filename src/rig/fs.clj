(ns rig.fs
  (:require
   [babashka.fs :as fs]))

;; coerce result to string
(defn path
  [& args]
  (some-> (apply fs/path args)
          str))

;; plumb through
(def create-dir fs/create-dir)
(def exists?    fs/exists?)
(def file-name  fs/file-name)
