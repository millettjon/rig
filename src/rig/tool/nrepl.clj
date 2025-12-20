(ns rig.tool.nrepl
  (:require
   [rig.deps.edn :as deps]
   [rig.shell    :refer [$&]]))

(def PORTAL_INIT
  "(require '[portal.api :as p])
     (p/open)
     (add-tap p/submit)
     (tap> :rig/hello)")

(def MODULES
  {"cider"
   {:aliases [:cider :refactor-nrepl]
    :middleware '[cider.nrepl/cider-middleware
                  ;; Refactor-nrepl depends on the cider middleware.
                  refactor-nrepl.middleware/wrap-refactor]}

   "clj-reload"
   {:aliases [:clj-reload]}

   "portal"
   {:aliases [:portal]
    :init PORTAL_INIT}

   "reveal"
   {:aliases [:reveal]
    :middleware '[vlaaad.reveal.nrepl/middleware]}})

(defn ^:export nrepl
  [modules]

  (doseq [module modules]
    (assert (MODULES  module) "unknown module"))

  (let [modules (-> modules
                    (into #{"clj-reload" "refactor-nrepl"}))
        aliases (->> modules
                    (mapv (comp :aliases MODULES))
                    flatten
                    (into [:nrepl]))
        middleware (->> modules
                        (mapv (comp :middleware MODULES))
                        flatten
                        (filterv some?))
        inits      (->> modules
                        (mapv (comp :init MODULES))
                        (filterv some?))
        args       (when (seq middleware)
                     ["--middleware" (pr-str middleware)])]

  (apply deps/clojure-main2 aliases inits "nrepl.cmdline" args)))

;; BB only
(defn ^:export bb-nrepl
  [modules]
  (doseq [module modules]
    (assert (#{"portal"} module) "unknown module"))

  (let [modules    (->> modules (map keyword) set)
        deps       (deps/merge-aliases modules)
        deps-init  (format "(babashka.deps/add-deps '%s)" (pr-str deps))
        portal?    (modules :portal)
        nrepl-port 1667 ; default
        proc       ($& {:inherit   true
                        :extra-env (when portal?
                                     {"BABASHKA_PRELOADS"
                                      (str
                                       deps-init
                                       " "
                                       PORTAL_INIT)})}
                       "bb" "--nrepl-server" (str nrepl-port))]
    (spit ".nrepl-port" nrepl-port)
    (.deleteOnExit (java.io.File. ".nrepl-port"))
    @proc))
