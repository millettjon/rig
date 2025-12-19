(ns rig.cli)

(defn bb-proc?
  [x]
  (= "babashka.process.Process" (some-> x class .getName)))

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
        f      (requiring-resolve fn-sym)
        code   (-> (apply f args) result->code)]
    (or (zero? code)
        (println (format "(exited with error code: %d)" code)))
    (System/exit code)))

(defn lint
  []
  (invoke 'lint/lint))

(defn outdated
  []
  (invoke 'outdated/outdated))

(defn outdated:upgrade
  []
  (invoke 'outdated/upgrade))

(defn unused
  []
  (invoke 'unused/unused))

(def HELP
  "RIG - Run a tool in a project.

USAGE
  rig <command>

COMMANDS
  help               display help
  lint               lint source files
  outdated           list oudated dependencies
  outdated:upgrade   upgrade outdated dependencies
  unused             find unused public vars
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
      "unused"           (unused)

      ;; else
      (help))))

(defn ^:export main
  [& args]
  (dispatch args))
