(ns vc.core
  "EDN constructors for W3C Verifiable Credential documents.")

(def context-key (keyword "@context"))

(def vc-context-v2 "https://www.w3.org/ns/credentials/v2")

(defn ensure-vector [x]
  (cond
    (nil? x) []
    (vector? x) x
    (sequential? x) (vec x)
    :else [x]))

(defn types [& xs]
  (vec (distinct (cons "VerifiableCredential" (map name xs)))))

(defn presentation-types [& xs]
  (vec (distinct (cons "VerifiablePresentation" (map name xs)))))

(defn credential
  [{:keys [id type issuer valid-from valid-until subject proof status schema]
    :as opts}]
  (cond-> {context-key vc-context-v2
           :type (if type (types type) (types))
           :issuer issuer
           :credentialSubject subject}
    id (assoc :id id)
    valid-from (assoc :validFrom valid-from)
    valid-until (assoc :validUntil valid-until)
    proof (assoc :proof proof)
    status (assoc :credentialStatus status)
    schema (assoc :credentialSchema schema)
    (:context opts) (assoc context-key (ensure-vector (:context opts)))))

(defn presentation
  [{:keys [id type holder credentials proof context]}]
  (cond-> {context-key (or context vc-context-v2)
           :type (if type (presentation-types type) (presentation-types))
           :verifiableCredential (ensure-vector credentials)}
    id (assoc :id id)
    holder (assoc :holder holder)
    proof (assoc :proof proof)))

(defn credential? [x]
  (and (map? x)
       (some #{"VerifiableCredential"} (ensure-vector (:type x)))
       (contains? x :credentialSubject)))

(defn presentation? [x]
  (and (map? x)
       (some #{"VerifiablePresentation"} (ensure-vector (:type x)))))

(defn errors [doc]
  (cond
    (credential? doc)
    (cond-> []
      (nil? (:issuer doc)) (conj {:error :vc/missing-issuer})
      (nil? (:credentialSubject doc)) (conj {:error :vc/missing-subject}))

    (presentation? doc)
    []

    :else
    [{:error :vc/unknown-document-type}]))

(defn validate [doc]
  (let [es (errors doc)]
    {:valid? (empty? es) :errors es}))
