(ns rig.tool.outdated
  (:require
   [rig.deps.edn :as deps]))

(defn antq
  [& args]
  (apply deps/clojure-main [:antq :log/no-op] "antq.core" args))

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
