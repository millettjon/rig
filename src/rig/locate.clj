(ns rig.locate
  "Locate things on the filesystem."
  (:require
   [babashka.fs :as fs]
   [rig.git     :as git]))

(def user-home
  "the user's home directory"
  (System/getenv "HOME"))

(defn find-ancestor
  [ancestor path]
  (loop [path path]
    (when (some-> path fs/exists?)
      (or (when (= ancestor (fs/file-name path))
            (str path))

          (let [path' (fs/path path ancestor)]
            (when (fs/exists? path')
              (str path')))

          (recur (fs/parent path))))))

;; FIXME *file* can be nil for some repl evals.
(def bb-edn
  "this projects bb.edn"
  (->> *file* fs/canonicalize (find-ancestor "bb.edn")))

(def deps-edn
  "this projects deps.edn"
  (->> *file* fs/canonicalize (find-ancestor "deps.edn")))

(def rig-home
  (-> bb-edn fs/parent str))

;; FIXME memoize since slow down startup
(defn project-home
  []
  (git/top-level))
