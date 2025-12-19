(ns rig.tool.lint
  (:require
   [rig.classpath :as cp :refer [raw-classpath]]
   [rig.fs        :as fs]
   [rig.locate    :as loc]
   [rig.shell     :refer [$ $?]]))

(defn kondo-dir
  []
  (fs/path (loc/project-home) ".clj-kondo"))

(defn kondo-installed?
  []
  (fs/exists? (kondo-dir)))

(defn kondo-install
  []
  (println "Setting up" (kondo-dir))
  ;; Create the kondo directory.
  (fs/create-dir (kondo-dir))
  ;; TODO Consider copying in the default config.
  ;; TODO Consider adding .clj-kondo to git (and git ignore the cache directory)
  ;; ? can we detect if the classpath basis has changed and lint the whole classpath then to pick up new configs?
  ;; ? check timestamp on .cpcache against last run of copy configs?
  ;;   - lein would have a different mechanism
  ;; - maybe just check time on deps.edn and bb.edn

  ;; Copy configs into .clj-kondo.
  ;; NOTE This is only done at install as it is assumed clojure-lsp
  ;; will manage it during normal project use.
  ($ "clj-kondo" "--lint" (raw-classpath) "--dependencies" "--copy-configs" "--skip-lint"))

;; Ref: https://github.com/clj-kondo/clj-kondo/blob/master/doc/config.md
;; TODO Consider customizing the config.
(defn kondo-lint
  []
  (when-not (kondo-installed?)
    (kondo-install))

  (let [project-paths (cp/project-paths)
        cmd (-> (concat ["clj-kondo" "--lint"] project-paths ["--parallel"])
                vec)]
    (println "Linting paths:" (pr-str project-paths))
    ;; Kondo doesn't work from subdirectories of the project.
    ;; TODO print a message if switching from the cwd to the project home.
    (apply $? {:dir (loc/project-home)} cmd)))

(defn ^:export lint
  []
  (kondo-lint))
