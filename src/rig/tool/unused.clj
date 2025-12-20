(ns rig.tool.unused
  (:require
   [rig.classpath :as cp]
   [rig.deps.edn  :as deps]))

(defn- carve
  []
  (let [opts (pr-str {:paths (cp/project-paths) :report {:format :text}})]
    (deps/clojure-main [:carve :no-log] "carve.main" "--opts" opts)))

(defn ^:export unused
  "Check for outdated dependencies."
  []
  (carve))
