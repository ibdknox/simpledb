(ns simpledb.core
  (:refer-clojure :exclude [get remove])
  (:import java.util.concurrent.TimeUnit
           java.util.concurrent.Executors))

(defonce *db* (atom {}))
(defonce *timer* (. Executors newScheduledThreadPool 1))

(defn store! [k v]
  (swap! *db* assoc k v)
  nil)

(defn get [k]
  (clojure.core/get @*db* k))

(defn remove! [k]
  (swap! *db* dissoc k)
  nil)

(defn update! [k f & args]
  (swap! *db* #(assoc % k (apply f (clojure.core/get % k) args))))

(defn persist-db []
  (let [cur @*db*]
    (println "Persisting " (count cur) " keys.")
    (spit "sdb.db" (pr-str cur))))

(defn read-db []
  (let [content (or (read-string (slurp "sdb.db")) {})]
    (reset! *db* content)
    (if (seq content)
      true
      nil)))

(defn clear! []
  (reset! *db* {})
  (persist-db))

(defn init []
  (read-db)
  (.. Runtime getRuntime (addShutdownHook (Thread. persist-db)))
  (. *timer* (scheduleAtFixedRate persist-db (long 5) (long 5) (. TimeUnit MINUTES))))

(init)

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

