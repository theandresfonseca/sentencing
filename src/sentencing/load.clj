(ns sentencing.load
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [sentencing.common :as common]
            [squeezer.core :as sc]))

;; I/O

(defn sparse-csv-row->map
  [header row]
  (reduce (fn [acc [idx value]]
            (if (not-empty value)
              (assoc acc (get header idx) value)
              acc))
          {}
          (map-indexed (fn [idx value] [idx value]) row)))

(defn naive-kebab
  [k]
  (-> k name string/lower-case (string/replace #" |_}" "-") keyword))

(defn csv-data->maps
  [csv-data]
  (let [header (->> csv-data first (mapv naive-kebab))]
    (mapv (partial sparse-csv-row->map header) (rest csv-data))))
 
(defn read-zip
  [file-name]
  (->> file-name sc/reader-compr csv/read-csv csv-data->maps time))

(defn read-csv
  [file-name]
  (with-open [reader (io/reader file-name)]
    (-> reader csv/read-csv csv-data->maps doall time)))

(defn dictionary [] (read-csv "data/data-dictionary.csv"))

(defn name->path
  [s]
  (->> (string/split s #"\.")
       (map #(string/replace % #"_" "-"))
       (mapv (fn [k]
               (if (common/number-str? k)
                 (common/parse-int k)
                 (keyword k))))))

(defn nesting-instructions
  [dictionary]
  (->> dictionary
       (filter :developer-friendly-name)
       (map (fn [{:keys [variable-name dev-category developer-friendly-name api-data-type]}]
              [(keyword variable-name)
               {:path (cons (keyword dev-category) (name->path developer-friendly-name))
                :parse-fn (case api-data-type
                            "integer" common/parse-int
                            "boolean" common/parse-bool
                            common/not-unknown)}]))
       (into {})))

(defn clean-sentence
  [nesting-instructions sentence]
  (reduce (fn [acc [id {:keys [path parse-fn]}]]
            (if-let [v (parse-fn (get sentence id))]
              (assoc-in acc path v)
              acc))
          {}
          nesting-instructions))
