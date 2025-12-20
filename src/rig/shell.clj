(ns rig.shell
  (:require
   [babashka.fs      :as fs]
   [babashka.process :as p]
   [clojure.string   :as str])
  (:refer-clojure :exclude [var-set]))

;; ?  ; like $? in shell; returns status (does not throw)
;; >  ; captures output to string
;; &  ; run in background

;; '$>    ; waits, throws on non 0
;; '$?>   ; waits, throws on non 0
;; '$     ; waits, throws on non 0
;; '$?    ; waits, doesn't throw

;; '$&    ; background
;; '$&>

(defn $&
  [& args]
  (let [[opts args] (if (map? (first args))
                      [(first args) (rest args)]
                      [nil args])
        opts (merge opts {:inherit true})]
    #_(prn ::$? :opts opts :args args)
    (apply p/process opts args)))

(defn $?
  [& args]
  (-> (apply $& args)
      deref))

(defn $
  [& args]
  (-> (apply $? args)
      p/check))

#_(def $ p/shell)

(defn $>
  [& args]
  (let [opts {:out :string
              :err :string}
        [opts args] (if (map? (first args))
                      [(merge opts (first args)) (rest args)]
                      [opts args])
        result (apply p/shell opts args)]
    (-> result
        :out
        str/trim-newline)))

;; NOTE: SHELL is the default or login shell and may not be the same
;; as the currently running shell.
;; May have to find the parent process to see which shell started bb.
(def ^:dynamic *shell*
  (case (or (some-> (System/getenv "DF_SHELL"))
            (some-> (System/getenv "SHELL")
                    (fs/file-name)))
    "fish" :fish
    "zsh"  :zsh
    "bash" :bash))

(defn shell-dispatch
  [& _]
  *shell*)

#_ (ns-unmap *ns* 'var-set)
(defmulti var-set shell-dispatch)
;; TODO var-delete
(defmulti prepend-path shell-dispatch)

(defn fish-escape
  [v]
  (-> v
      (str/replace "\\" "\\\\")
      (str/replace "'" "\\'")))

(defmethod var-set :fish
  [k v]
  (format "set -gx %s '%s'" k (fish-escape v)))
#_ (var-set #_:fish "foo" "a\\b'c")
;; => "set -gx foo 'abc"

(defmethod prepend-path :fish
  [path]
  (format "fish_add_path --path --prepend --move '%s'" (fish-escape path)))
