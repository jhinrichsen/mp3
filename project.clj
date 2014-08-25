(defproject mp3 "0.2.0"
  :aot :all
  :description "mp3 tagging utilities"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.typed "0.2.65"]

                 [clj-logging-config "1.9.10"]
                 [org/jaudiotagger "2.0.3"]
                 [javazoom/jlayer "1.0.1"]
                 [me.raynes/conch "0.7.0"]]
  :main mp3.flac2mp3)
