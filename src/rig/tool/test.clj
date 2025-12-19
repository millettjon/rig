(ns rig.tool.test
  (:require
   [rig.deps.edn :as deps]
   [rig.fs       :as fs]
   [rig.locate   :as loc]))

(defn config-file
  []
  (fs/path (loc/project-home) "tests.edn"))

(defn create-config-file
  []
  (let [file (config-file)]
    (println "Creating test config file:" file)
    (spit file "#kaocha/v1 {}")))

(defn kaocha
  [& args]
  (or (fs/exists? (config-file))
      (create-config-file))

  (apply deps/clojure-main [:kaocha :log/no-op] "kaocha.runner" args))

(defn ^:export run
  "Upgrade outdated dependencies."
  [& args]
  (apply kaocha args))

;; bin/kaocha

;; # Watch for changes
;; bin/kaocha --watch

;; # Exit at first failure
;; bin/kaocha --fail-fast

;; # Only run the `unit` suite
;; bin/kaocha unit

;; # Only run a single test
;; bin/kaocha --focus my.app.foo-test/bar-test

;; # Use an alternative config file
;; bin/kaocha --config-file tests_ci.edn

;; # See all available options
;; bin/kaocha --test-help
