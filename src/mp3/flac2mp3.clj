(ns mp3.flac2mp3
  (:gen-class)
  (:import [java.io File])
  (:require [clojure.core.typed :as t]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [me.raynes.conch :refer [programs with-programs let-programs]]
            [mp3.tags :as tags]))

(* 2 3)

(defmacro with-resource [bnd close-fn & body]
  `(let ~bnd
     (try
      (do ~@body)
      (finally
       (~close-fn ~(bnd 0))))))

(def ^:dynamic *basedir* (java.io.File. (str (System/getProperty "user.home") "/Music/audio")))
(assert (.exists *basedir*))

(defn pad [s]
  (if (= 1 (count s))
    (str "0" s)
    s))

(defn hierarchy
  "<A..Z>/<artist>/<year>--<album>/<2 digit track>-<title>.
   TODO safe escape non filesystem compliant characters such as /."
  [tags]
  {:pre [(:artist tags)
         (:year tags)
         (:album tags)
         (:track tags)
         (:title tags)]}
  (let [ts [(.toUpperCase (.toString (first (first (:artist tags)))))
            (first (:artist tags))
            (str (first (:year tags))
                 "--"
                 (first (:album tags)))
            (str (pad (first (:track tags)))
                 "-"
                 (first (:title tags)))]
        clean (map #(clojure.string/replace % #"/" "-") ts)]
    (clojure.string/join "/" clean)))

(t/ann flac->wav [File File -> t/AnyInteger])
(defn flac->wav [flac wav]
  (let-programs [app "flac"]
    (let [args ["-f"                         ; overwrite any existing file
                "--silent" 	                 ; output is useless because we are multiplexing goroutines
		            "-d" (.getAbsolutePath flac) ; decode file (input)
		            "-o" (.getAbsolutePath wav)  ; output file
                {:dir (.getParent wav)
                 :verbose true}
               ]
          _ (log/info "Using commandline arguments" args)
          p (apply app args)
          rc @(p :exit-code)]
      (if (zero? rc)
        (log/debug "flac completes successfully.")
        (log/error "Error: flac returns" rc))
      rc)))

(t/ann wav->mp3 [File File t/HMap -> t/AnyInteger])
(defn wav->mp3 [wav mp3 tags]
  (log/info "Using output location")
  (let-programs [lame "lame"]
    (let [args ["--noreplaygain" ; TODO
                "-V" "4"         ; high variable bitrate
                "--nohist"       ; don't show histogram
                "--silent"       ; output is useless because we are in parallel threads
                "--tt" (first (:title tags))
                "--ta" (first (:artist tags))
                "--tg" (first (:genre tags))
                "--tl" (first (:album tags))
                "--ty" (first (:year tags))
                "--tn" (first (:track tags))
                "--add-id3v2"
                "--id3v2-only"
                (.getName wav)
                (.getAbsolutePath mp3)
                {:dir (.getParent wav)
                 :verbose true}]
          _ (println "Using commandline arguments" args)
          p (apply lame args)
          rc @(p :exit-code)]
      (if (zero? rc)
        (log/debug "lame completes successfully.")
        (log/error "Error: lame returns" rc))
      rc)))

(t/ann convert [String -> nil])
(defn convert [^String filename]
  (let [flacF (io/file filename)
        _ (assert flacF
                  "flac file is nil")
        _ (assert (.isFile flacF)
                  (str "flac file "
                       flacF
                       " does not exist"))]
    (with-resource [wavF (File/createTempFile "flac2mp3-"
                                              ".wav")]
      #(do
         (log/info "Deleting temporary file" %)
         (.delete %))
      (flac->wav flacF wavF)
      (let [tags (:tags (tags/metadata flacF))
            dest (io/file (str *basedir* "/" (hierarchy tags) ".mp3"))]

        (.mkdirs (.getParentFile dest))
        (wav->mp3 wavF dest tags)))))

; (convert "/Users/jochen/git-repos/mp3/01-pharrell_williams-marilyn_monroe.flac")

(t/ann -main [String -> t/AnyInteger])
(defn -main [& args]
  (try
    (doall
     (pmap convert args))
    (System/exit 0)
    (catch Exception xc
      (.printStackTrace xc)
      (println xc)
      (System/exit -1))
    (finally
     (shutdown-agents))))

; (t/check-ns)
