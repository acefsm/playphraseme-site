(ns playphraseme.api.queries.favorites
  (:require [monger.core :as mg]
            [mount.core :as mount]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [playphraseme.api.general-functions.doc-id :refer :all]
            [playphraseme.db.core :refer :all]
            [playphraseme.api.queries.phrases :as phrases]))

(def coll "favoritePhrase")

(defn get-favorite-by-id
  [^String favorite-id]
  (stringify-id
   (get-doc-by-id coll (str->id favorite-id))))

(defn get-favorite-by-phrase-id
  [^String phrase-id ^String user-id]
  (stringify-id
   (find-doc coll {:phrase (str->id phrase-id) :user (str->id user-id)})))

(defn insert-favorite! [^String phrase-id ^String user-id ]
  (let [exists (get-favorite-by-phrase-id phrase-id user-id)]
   (if-not exists
     (let [phrase (phrases/get-phrase-by-id phrase-id)]
       (stringify-id
        (add-doc coll (merge
                       {:user (str->id user-id)
                        :phrase (str->id phrase-id)
                        :movieInfo (-> phrase :video_info :info)}
                       (select-keys phrase [:text])))))
     exists)))

(defn update-favorite!
  [^String favorite-id {:keys [email name password refresh-token] :as user-data}]
  (update-doc-by-id coll (str->id favorite-id) user-data))

(defn delete-favorite!
  "Delete a single user matching provided id"
  [^String favorite-id]
  (delete-doc-by-id coll (str->id favorite-id)))

(defn delete-favorite-by-phrase-id! [phrase-id user-id]
  (when-let [doc (get-doc coll {:phrase (str->id phrase-id) :user (str->id user-id)})]
   (delete-doc-by-id coll (:_id doc))))

(defn get-favorites-by-user
  [^String user-id skip limit]
  (stringify-id
   (find-docs coll {:pred     {:user (str->id user-id)}
                    :skip  skip
                    :limit limit})))

(defn get-favorites-count
  [^String user-id]
  (count-docs coll {:user (str->id user-id)}))



(comment
  (get-favorites-count "5ad1e1f48079eb152db33a0e")

  (insert-favorite! "543bd8c8d0430558da9bfeb1" "5ad1e1f48079eb152db33a0e")

  (delete-favorite-by-phrase-id! "543bd8c8d0430558da9bfeb1" "5ad1e1f48079eb152db33a0e")

  (get-favorites-by-user "5ad1e1f48079eb152db33a0e" 0 20)


  )
