(ns rig.cli)

(defn invoke
  [tool-sym & args]
  (let [fn-sym (symbol (str "rig.tool." (namespace tool-sym))
                       (name tool-sym))
        f      (requiring-resolve fn-sym)]
    (apply f args)))
;; how to handle return?
;;   throw -> error
;;   process get result exit code
;;   int use as exit code
;;   nil use 0
;;   true use 0
;;   exception use 1
;;   anything else use 1

(defn outdated
  []
  (invoke 'outdated/outdated))

(defn outdated:upgrade
  []
  (invoke 'outdated/upgrade))

(def HELP
  "RIG - Run a tool in a project.

USAGE
  rig <command>

COMMANDS
  help              display help
  outdated          check for oudated dependencies
  outdated:upgrade    upgrade outdated dependencies
")

(defn help
  []
  (println HELP))

(defn dispatch
  [args]
  (let [command   (first args)]
    (case command
      "outdated"         (outdated)
      "outdated:upgrade" (outdated:upgrade)

      ;; else
      (help))))

(defn main
  [& args]
  (dispatch args))
