(ns mp3.player
  (:import [javazoom.jl.player Player])
  (:gen-class))

(defn -main
  [& args]
  (doseq [filename args]
    (println "Playing" filename)
    (with-open [is (clojure.java.io/input-stream filename)]
      (doto (Player. is)
        (.play)
        (.close)))))


;; (-main)
