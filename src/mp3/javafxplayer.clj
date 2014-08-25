(ns mp3.javafxplayer

  ;; Although javaFX is bundel with the jre, and the jar is available, class cannot be resolved.

  (:import [java.io File]
;;           [javafx.scene.media Media MediaPlayer]))
))

(System/getProperty "java.version")

(def source (-> (File. "resources/sample.mp3")
                .toURI
                .toString))
(comment
  (def media (Media. source))
  (def p (MediaPlayer. media))
  (.play p)
)
