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
#_ (select-aliases [:carve :no-log])

(defn merge-aliases
  [aliases]
  (->> aliases
       select-aliases
       :aliases
       (mapv (comp :extra-deps val))
       (apply merge)
       (assoc {} :deps)))
#_ (merge-aliases [:carve :no-log])

(defn -M
  [aliases]
  (->> aliases
       (into ["-M"])
       (apply str)))
#_ (-M [:carve :no-log])

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

(defn clojure-main2
  [aliases inits main & args]
  (let [deps (select-aliases aliases)
        cmd (concat [{:dir (loc/project-home)}
                     "clojure"
                     "-Sdeps" (pr-str deps)
                     (-M aliases)]
                    (reduce (fn [acc init]
                              (into acc ["--eval" init]))
                            [] inits)
                    ["-m" main]
                    args)]
    (apply $? cmd)))
