(ns cmql-app-clj.examples.forum34lookupExprindexUse
  (:refer-clojure :only [])
  (:use cmql-core.operators.operators
        cmql-core.operators.qoperators
        cmql-core.operators.uoperators
        cmql-core.operators.stages
        cmql-core.operators.options
        cmql-j.driver.cursor
        cmql-j.driver.document
        cmql-j.driver.settings
        cmql-j.driver.transactions
        cmql-j.driver.utils
        cmql-j.arguments
        cmql-j.commands
        cmql-j.macros
        flatland.ordered.map
        clojure.pprint)
  (:refer-clojure)
  (:require [clojure.core :as c]
            [clojure.data.json :as tojson])
  (:import (com.mongodb.client MongoClients MongoCollection MongoClient AggregateIterable)
           (com.mongodb MongoClientSettings)
           (org.bson.types ObjectId)
           (java.util Date Calendar)
           (com.mongodb.client.model Indexes)))


;;https://stackoverflow.com/questions/70155821/mongodb-weird-performance-in-lookup/70158881#70158881

(update-defaults :client-settings (-> (MongoClientSettings/builder)
                                      (.codecRegistry clj-registry)
                                      (.build)))

(update-defaults :client (MongoClients/create ^MongoClientSettings (defaults :client-settings)))


(try (drop-collection :testdb.testcoll1) (catch Exception e ""))
(try (drop-collection :testdb.testcoll2) (catch Exception e ""))
(try (drop-collection :testdb.testcoll3) (catch Exception e ""))

(defn add-docs [ndocs start]
  (loop [n start
         docs []]
    (if (= n (+ ndocs start))
      (do (insert :testdb.testcoll1 docs)
          (insert :testdb.testcoll2 docs)
          (insert :testdb.testcoll3 docs))
      (recur (inc n) (conj docs {:a n :b n})))))

(dotimes [x 1] (add-docs 1000 (* x 1000)))

;(create-index :testdb.testcoll1 (index [:a]))
(create-index :testdb.testcoll2 (index [:a]))
;(create-index :testdb.testcoll3 (index [:a]))


(time (c-take-all (q :testdb.testcoll1
                     (lookup :a :testcoll2.a :joined)
                     (= (count :joined) 2)
                     )))

(time (c-print-all (q :testdb.testcoll1
                     (lookup :a :testcoll2.a :joined)
                     (lookup :a :testcoll3.a :joined1)
                     (= (count :joined1) 2)
                     )))

#_(time (c-take-all (q :testdb.testcoll
                     (contains? [66688 66688] :a))))

#_(time (c-take-all (q :testdb.testcoll
                     (or (= 66688 :a) (= 66689 :a)))))

#_(time (c-take-all (q :testdb.testcoll
                     {"$match" {"a" {"$in" [66688 66688]}}})))