(ns rig.cli
  (:refer-clojure :exclude [test]))

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

(defn nrepl
  [args]
  (invoke 'nrepl/nrepl args))

(defn nrepl:bb
  [args]
  (invoke 'nrepl/bb-nrepl args))

(defn outdated
  []
  (invoke 'outdated/outdated))

(defn outdated:upgrade
  []
  (invoke 'outdated/upgrade))

(defn test
  [args]
  (apply invoke 'test/run args))

(defn unused
  []
  (invoke 'unused/unused))

(def HELP
  "RIG - Run a tool in a project.

USAGE
  rig <command> [option ...]

COMMANDS
  help                                 display help
  lint                                 lint source files
  nrepl [cider] [portal] [reveal]      start clj nrepl server
  nrepl:bb [portal]                    start bb nrepl server
  outdated                             list oudated dependencies
  outdated:upgrade                     upgrade outdated dependencies
  test [--help] [--watch]              run tests
  unused                               find unused public vars
  zprint                               format source
")

(defn help
  []
  (println HELP))

(defn dispatch
  [args]
  (let [command (first args)
        args'   (rest args)]
    (case command
      "lint"             (lint)
      "nrepl"            (nrepl args')
      "nrepl:bb"         (nrepl:bb args')
      "outdated"         (outdated)
      "outdated:upgrade" (outdated:upgrade)
      "test"             (test args')
      "unused"           (unused)

      ;; else
      (help))))

(defn ^:export main
  [& args]
  (dispatch args))
