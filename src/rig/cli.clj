(ns rig.cli)

(defn bb-proc?
  [x]
  (= "babashka.process.Process" (-> x class .getName)))

(defn result->code
  [result]
  (cond
    ;; When a literal exception, return 9.
    (instance? Throwable result) 9

    ;; When a bb process, return the exit code.
    (bb-proc? result) (-> result deref :exit)

    ;; When truthy return 0
    (boolean result) 0

    ;; Else non truthy result.
    :else 1))

(defn invoke
  [tool-sym & args]
  (let [fn-sym (symbol (str "rig.tool." (namespace tool-sym))
                       (name tool-sym))
        f      (requiring-resolve fn-sym)]
    (-> (apply f args)
        result->code
        System/exit)))

(defn lint
  []
  (invoke 'lint/lint))

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
  lint              lint source files
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
      "lint"             (lint)
      "outdated"         (outdated)
      "outdated:upgrade" (outdated:upgrade)

      ;; else
      (help))))

(defn main
  [& args]
  (dispatch args))
