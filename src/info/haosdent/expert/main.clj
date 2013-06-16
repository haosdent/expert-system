(ns info.haosdent.expert.main
  (:use [clojure.set])
  (:require (monger [core :as mg]
                    [collection :as mc]))
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(mg/connect!)
(mg/set-db! (mg/get-db "expert-system"))

(defn init-db []
  (mc/insert "rules" {:reasons '("毛发") :result "哺乳动物"})
  (mc/insert "rules" {:reasons '("奶") :result "哺乳动物"})
  (mc/insert "rules" {:reasons '("羽毛") :result "鸟"})
  (mc/insert "rules" {:reasons '("飞" "下蛋") :result "鸟"})
  (mc/insert "rules" {:reasons '("肉") :result "食肉动物"})
  (mc/insert "rules" {:reasons '("犬齿" "爪" "眼盯前方") :result "食肉动物"})
  (mc/insert "rules" {:reasons '("哺乳动物" "蹄") :result "有蹄动物"})
  (mc/insert "rules" {:reasons '("哺乳动物" "反刍动物") :result "有蹄动物"})
  (mc/insert "rules" {:reasons '("哺乳动物" "食肉动物" "黄褐色" "暗斑点") :result "豹"})
  (mc/insert "rules" {:reasons '("哺乳动物" "食肉动物" "黄褐色" "黑条纹") :result "虎"})
  (mc/insert "rules" {:reasons '("有蹄动物" "长腿" "长脖子" "暗斑点") :result "长颈鹿"})
  (mc/insert "rules" {:reasons '("有蹄动物" "黑条纹") :result "斑马"})
  (mc/insert "rules" {:reasons '("鸟" "长腿" "长脖子" "黑色" "不飞") :result "鸵鸟"})
  (mc/insert "rules" {:reasons '("鸟" "游泳" "黑色" "不飞") :result "企鹅"})
  (mc/insert "rules" {:reasons '("鸟" "善飞") :result "信天翁"}))

(defn search [facts]
  (let [rules (mc/find-maps "rules")
        size (mc/count "rules")
        match (fn [{:keys [facts n]}]
                (let [rule (nth rules n)]
                  {:facts (if (and
                               (subset? (set (get rule :reasons))
                                       (set facts))
                               (not (subset? #{(get rule :result)}
                                       (set facts))))
                            (do
                              (println (get rule :result))
                              (conj facts (get rule :result)))
                            facts)
                   :n (inc n)}))]
    (loop [facts facts]
      (let [result (get (nth 
                         (iterate match {:facts facts :n 0})
                         size)
                        :facts)]
        (if (not= (count facts)
               (count result))
          (recur result))))))
