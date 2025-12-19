(ns rig.deps.edn
  (:require
   [clojure.edn :as edn]
   [rig.locate  :as loc]
   [rig.shell   :refer [$?]]))

(defn select-aliases
  [aliases]
  (-> loc/deps-edn
    slurp
    edn/read-string
    (select-keys [:aliases])
    (update :aliases select-keys aliases)))
#_ (select-aliases [:carve :log/no-op])

(defn -M
  [aliases]
  (->> aliases
       (into ["-M"])
       (apply str)))
#_ (-M [:carve :log/no-op])

(defn clojure-main
  [aliases main & args]
  (let [deps (select-aliases aliases)]
    (apply
     $?
     {:dir (loc/project-home)}
     "clojure"
     "-Sdeps" (pr-str deps)
     (-M aliases)
     "-m" main
     args)))
