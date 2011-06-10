(ns simpledb.test.core
  (:use [simpledb.core])
  (:use [clojure.test]))

(deftest replace-me ;; FIXME: write
  (is false "No tests have been written."))

(defn write-test []
  (let [t (System/currentTimeMillis)]
    (loop [iter 0]
      (if (>= (- (System/currentTimeMillis) t) 1000)
        (println iter)
        (do
          (store! iter iter)
          (recur (inc iter)))))))

(defn read-test []
  (let [t (System/currentTimeMillis)]
    (loop [iter 0]
      (if (>= (- (System/currentTimeMillis) t) 1000)
        (println iter)
        (do
          (get iter)
          (recur (inc iter)))))))


