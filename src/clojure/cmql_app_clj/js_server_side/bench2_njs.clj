(ns cmql-app-clj.js-server-side.bench2_njs
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
  (:require [clojure.core :as c])
  (:import (com.mongodb.client MongoClients MongoCollection MongoDatabase MongoClient)
           (com.mongodb MongoClientSettings)
           (java.util Collections Random ArrayList)
           (quickstart_java.models Grade Score)
           (org.bson RawBsonDocument Document)
           (org.bson.types ObjectId)
           (com.mongodb.client.model FindOneAndReplaceOptions ReturnDocument)))

(update-defaults :client-settings (-> (MongoClientSettings/builder)
                                      (.codecRegistry clj-registry) ;;Remove this if you want to decode in Java Document
                                      (.build)))

(update-defaults :client (MongoClients/create ^MongoClientSettings (defaults :client-settings)))

(try (drop-collection :testdb.testcoll) (catch Exception e ""))

(defn add-docs [n]
  (let [docs (into [] (take n (repeat {:mynumber 1000})))]
    (insert :testdb.testcoll docs)))

;;add one million documents like above
(dotimes [_ 100] (add-docs 10000))

;;Results (10 levels of nesting)
;;ejs = 29 sec  (run 10 functions on the server)
;;njs = 12 sec  (run 1 function on the server,the one cMQL generated)
;;no js = 4 sec (use aggregation framework operators)

(compile-functions :myadd :mysub)

(time (.toCollection (q :testdb.testcoll
                      {:newobject (ejs :mysub
                                       [(ejs :myadd
                                             [(ejs :mysub
                                                   [(ejs :myadd
                                                         [(ejs :mysub
                                                               [(ejs :myadd
                                                                     [(ejs :mysub
                                                                           [(ejs :myadd
                                                                                 [(ejs :mysub
                                                                                       [(ejs :myadd
                                                                                             [:mynumber 2])
                                                                                        2])
                                                                                  2])
                                                                            2]) 2])
                                                                2])
                                                          2])
                                                    2]) 2])
                                        2])}
                      (out :testdb.testcoll1))))

(time (.toCollection (q :testdb.testcoll
                      {:newobject (ejs (njs :mysub
                                            [(njs :myadd
                                                  [(njs :mysub
                                                        [(njs :myadd
                                                              [(njs :mysub
                                                                    [(njs :myadd
                                                                          [(njs :mysub
                                                                                [(njs :myadd
                                                                                      [(njs :mysub
                                                                                            [(njs :myadd
                                                                                                  [:mynumber 2])
                                                                                             2])
                                                                                       2])
                                                                                 2]) 2])
                                                                     2])
                                                               2])
                                                         2]) 2])
                                             2]))}
                      (out :testdb.testcoll1))))


(time (.toCollection (q :testdb.testcoll
                        {:newobject (- (+ (- (+ (- (+ (- (+ (- (+ :mynumber 2) 2) 2) 2) 2) 2) 2) 2) 2) 2)}
                        (out :testdb.testcoll1))))


(time (.toCollection (q :testdb.testcoll
                        {:newobject (ejs :mysub
                                         [(ejs :myadd
                                               [(ejs :mysub
                                                     [(ejs :myadd
                                                           [(ejs :mysub
                                                                 [(ejs :myadd
                                                                       [(ejs :mysub
                                                                             [(ejs :myadd
                                                                                   [(ejs :mysub
                                                                                         [(ejs :myadd
                                                                                               [:mynumber 2])
                                                                                          2])
                                                                                    2])
                                                                              2]) 2])
                                                                  2])
                                                            2])
                                                      2]) 2])
                                          2])}
                        (out :testdb.testcoll1))))