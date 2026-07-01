(ns vc.core-test
  (:require [clojure.test :refer [deftest is]]
            [vc.core :as vc]))

(deftest builds-credential
  (let [c (vc/credential {:id "urn:vc:1"
                          :issuer "did:web:issuer.example"
                          :subject {:id "did:example:alice" :name "Alice"}
                          :type :EmployeeCredential
                          :valid-from "2026-07-01T00:00:00Z"})]
    (is (= "https://www.w3.org/ns/credentials/v2" ((keyword "@context") c)))
    (is (= ["VerifiableCredential" "EmployeeCredential"] (:type c)))
    (is (:valid? (vc/validate c)))))

(deftest builds-presentation
  (let [p (vc/presentation {:holder "did:example:alice"
                            :credentials [{:id "urn:vc:1"}]})]
    (is (vc/presentation? p))
    (is (= 1 (count (:verifiableCredential p))))))
