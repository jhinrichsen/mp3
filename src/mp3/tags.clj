(ns mp3.tags
  (:import [org.jaudiotagger.audio AudioFileIO]
           [org.jaudiotagger.tag FieldKey]))

(defn tags [file]
  (let [fields (apply conj {} (map (fn [n] [(keyword (. (. n toString) toLowerCase)) n]) (. FieldKey values)))
        tag (. file (getTag))]
    (apply conj {}
           (filter (fn [[name val]] (and val (not (empty? val))))
                   (map (fn [[name val]]
                          [name (seq (map #(. % getContent) (. tag (getFields val))))])
                        fields)))))

(defn audioheader [file]
  (bean (. file (getAudioHeader))))

(defn metadata [^java.io.File f]
  (let [audiofile (AudioFileIO/read f)]
    {:tags (tags audiofile)
     :audioheader (audioheader audiofile)}))


(if false
  (metadata
   (clojure.java.io/file
    (clojure.java.io/resource "sample.flac"))))
