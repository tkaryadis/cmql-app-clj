(ns cmql-app-clj.examples.forum12lookupNull
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
           (com.mongodb MongoClientSettings)))

;;https://www.mongodb.com/community/forums/t/handling-nullish-values-in-pipeline-expression/116055/2

(update-defaults :client-settings (-> (MongoClientSettings/builder)
                                      (.codecRegistry clj-registry) ;;Remove this if you want to decode in Java Document
                                      (.build)))

(update-defaults :client (MongoClients/create ^MongoClientSettings (defaults :client-settings)))

(try (drop-collection :testdb.t5) (catch Exception e ""))
(try (drop-collection :testdb.t6) (catch Exception e ""))

(def t5 [{"_id" 1, "id" 1, "age" 24, "name" "dvd"}
         {"_id" 2, "id" 1, "age" nil, "name" nil}])

(def t6 [{"_id" 1, "no" 2, "old" 25, "alias" "vdd"}
         {"_id" 2, "no" 1, "old" 24, "alias" "dvd"}
         {"_id" 3, "no" 3, "old" nil, "alias" nil}])

(insert :testdb.t5 t5)
(insert :testdb.t6 t6)

;;goal = dont join if both null

(c-print-all (q :testdb.t5
                (match {:age {"$ne" nil}})
                (lookup :age :t6.old :joined)
                {:print true}))

(c-print-all (q :testdb.t5
                (lookup-p :t6
                          [:p_age. :age]
                          [(= :p_age. :old)
                           (not= :old nil)]
                          :joined)
                {:print true}))


