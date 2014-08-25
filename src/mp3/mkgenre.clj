;; Create genre file using stdin from lame --genre-list

(defn cvt [s]
  (let [id (.trim (subs s 0 3))
        txt (subs s 4)]
    (println (str "\"" id "\"" " \"" txt "\""))))

(doall
 (map cvt
      (line-seq (java.io.BufferedReader. *in*))))
