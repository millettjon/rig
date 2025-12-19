(ns rig.tool.outdated
  (:require
   [babashka.tasks :refer [clojure]]))

;; TODO Consider adding antq as a cli command to pass through raw options.
(defn antq
  "Check for outdated dependencies."
  [& args]
  (let [deps '{:deps
               {com.github.liquidz/antq {:mvn/version "RELEASE"}}

               :aliases
               {:log/no-op {:extra-deps {org.slf4j/slf4j-nop {:mvn/version "RELEASE"}}
                            :jvm-opts ["-Dslf4j.internal.verbosity=WARN"]}}}]
    ;; The antq library is not compatible with babashka so run clojure.
    (apply
     clojure
     ;; Use nil deps.edn to prevent clojure from loading deps.edn which has bb only deps.
     ;; Note: -Sdeps-file is only available in babashka.deps
     ;; "-Sdeps-file" nil
     "-Sdeps" (str deps)
     "-M:log/no-op"
     "-m" "antq.core"
     args)))

;; NOTE By default, when clojure returns a non zero exit code, bb will exit with the same code.
;; FIXME ? how to control this?
;; TODO ? what is returned on success?

(defn ^:export outdated
  "Check for outdated dependencies."
  []
  (antq))

(defn ^:export upgrade
  "Upgrade outdated dependencies."
  []
  ;; TODO Consider using gum to page through changelogs before prompting.
  ;;   - report as json
  ;;   - download changelog
  ;;   - page through changelog
  (antq "--upgrade"))
