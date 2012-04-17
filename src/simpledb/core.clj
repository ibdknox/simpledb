(ns simpledb.core
  (:refer-clojure :exclude [get get-in remove])
  (:import java.util.concurrent.TimeUnit
           java.util.concurrent.Executors))

(defonce *db* (atom {}))
(defonce *timer* (. Executors newScheduledThreadPool 1))

(defn put! [k v]
  (swap! *db* assoc k v)
  [k v])

(defn get [k]
  (clojure.core/get @*db* k))

(defn get-in [k ks]
  (clojure.core/get-in (get k) ks))

(defn remove! [k]
  (swap! *db* dissoc k)
  k)

(defn update! [k f & args]
  (clojure.core/get 
    (swap! *db* #(assoc % k (apply f (clojure.core/get % k) args))) 
    k))

(defn persist-db [db-filename]
  (let [cur @*db*]
    (println "SimpleDB: Persisting " (count cur) " keys.")
    (spit db-filename (pr-str cur))))

(defn read-db [db-filename]
  (let [content (try 
                  (read-string (slurp db-filename))
                  (catch Exception e
                    (println "SimpleDB: Could not find " db-filename " file. Starting from scratch")
                    {}))]
    (reset! *db* content)
    (let [not-empty? (complement empty?)]
      (when (not-empty? content)
        (println "SimpleDB: " (count content) " keys are loaded.")
        true))))

(defn clear! []
  (reset! *db* {})
  (persist-db))

(defn init [& filename]
	(let [db-filename (or (first filename) "./sdb.db")]
	  (read-db db-filename)
	  (.. Runtime getRuntime (addShutdownHook (Thread. #(persist-db db-filename))))
	  ;(. *timer* (scheduleAtFixedRate #(persist-db db-filename) (long 5) (long 5) (. TimeUnit MINUTES)))
	))
