(ns sentencing.common)

(defn parse-int
  [s]
  (try
    (Integer/parseInt s)
    (catch Exception _ nil)))

(defn parse-double
  [s]
  (try
    (Double/parseDouble s)
    (catch Exception _ nil)))

(defn parse-bool
  [s]
  (when (#{"0" "1"} s)
    (= s "1")))

(defn not-unknown [s] (when (and (not-empty s) (not= s ".")) s))

(defn mean [xs] (/ (double (reduce + xs)) (count xs)))

(defn round
  [n places]
  (/ (Math/round (* n (Math/pow 10 places)))
     (Math/pow 10 places)))

(defn prefix-k [prefix k] (keyword (str (name prefix) "-" (name k))))

(defn remove-empty-keys
  [m]
  (reduce (fn [acc [k v]]
            (if (not-empty v)
              (assoc acc k v)
              acc))
          {}
          m))
