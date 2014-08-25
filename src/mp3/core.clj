(ns mp3.core
  (:import [org.jaudiotagger.audio.mp3 MP3File])
  (:require
            [clojure.java.io :as io]
            [clojure.pprint :only [print-table] :as pp]
            [clojure.reflect :as r]))

(defn mp3?
  [file]
  (-> file (.getName) (.endsWith ".mp3")))

(defn dir?
  [file]
  (-> file (.isDirectory)))

(defn -main
  [& argv]

    ; (def f (io/file "/Users/jochen/Music/audio/0-9/3 Doors Down/2000--The Better Life/3 DOORS DOWN - (THE BETTER LIFE 10 - SMACK).mp3"))
    (def f (io/file "/Users/jochen/Music/audio/0-9/3 Doors Down/2000--The Better Life"))
    (mp3? (io/file f))
    (.length f)

    (filter mp3? (file-seq f))

    (def m (MP3File. f))
    m
    (.hasID3v1Tag m)
    (.hasID3v2Tag m)
    ; (.displayStructureAsPlainText m)

    (def t (.getID3v2Tag m))
    (iterator-seq (.getFields t))


    (keyword "test")
    {(keyword "key1") "value1"}

    (defn tags
      [file]
      (into {} (map #(hash-map (keyword (.getId %)) (.getContent %))
                    (iterator-seq (.getFields (.getID3v2Tag file))))))

    ; (map #(hash-map (keyword (.getId %)) (.getContent %)) (iterator-seq (.getFields t)))

    (tags m)

    (pp/print-table (tags m))

  )
