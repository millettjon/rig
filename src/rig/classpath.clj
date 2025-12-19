(ns rig.classpath
  (:require
   [clojure.string :as str]
   [rig.fs         :as fs]
   [rig.locate     :as loc]
   [rig.shell      :refer [$>]]))

(defn split
  [s]
  (str/split s #":"))

(defn join
  [cp]
  (str/join ":" cp))

;; TODO determine project type and handle other types of projects
;; lein classpath
;; boot with-cp -w -f -
;; npx shadow-cljs classpath

(defn raw-classpath
  "Returns the project classpath as a vector of strings."
  []
  ($> "clojure" "-Spath"))

(defn classpath
  []
  (-> (raw-classpath) split))

(defn project-paths
  "Returns paths on the project classpath that are file paths in the project."
  []
  (let [project-home (loc/project-home)]
    (->> (classpath)
         (mapv fs/file-name)
         (filterv #(->> % (fs/path project-home) fs/exists?)))))
#_ (project-paths)
