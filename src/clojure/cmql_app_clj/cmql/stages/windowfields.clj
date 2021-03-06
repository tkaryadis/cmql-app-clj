(ns cmql-app-clj.cmql.stages.windowfields
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
  (:import (com.mongodb.client MongoClients MongoCollection MongoClient)
           (com.mongodb MongoClientSettings)))


(pprint (wfields :state
                 (cmql-core.operators.stages/sort :orderDate)    ;;if in q enviroment no need for namespace
                 {:cumulativeQuantityForState (sum :quantity)
                  :documents ["unbounded" "current"]}))

(pprint (wfields :state
                 (cmql-core.operators.stages/sort :orderDate)    ;;if in q enviroment no need for namespace
                 {:cumulativeQuantityForState (dense-rank)}))