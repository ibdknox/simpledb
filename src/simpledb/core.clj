(ns simpledb.core
  (:refer-clojure :exclude [get remove])
  (:import java.util.concurrent.TimeUnit
           java.util.concurrent.Executors))

(defonce *db* (atom {}))
(defonce *timer* (. Executors newScheduledThreadPool 1))

(defn put! [k v]
  (swap! *db* assoc k v)
  [k v])

(defn get [k]
  (clojure.core/get @*db* k))

(defn remove! [k]
  (swap! *db* dissoc k)
  k)

(defn update! [k f & args]
  (get 
    (swap! *db* #(assoc % k (apply f (clojure.core/get % k) args))) 
    k))

(defn persist-db []
  (let [cur @*db*]
    (println "Persisting " (count cur) " keys.")
    (spit "sdb.db" (pr-str cur))))

(defn read-db []
  (let [content (try 
                  (read-string (slurp "sdb.db"))
                  (catch Exception e
                    {}))]
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
