# kotoba-lang/vc

EDN-first constructors and validators for W3C Verifiable Credential documents.

The library models credentials and presentations as plain Clojure maps with
JSON-LD keywords such as `:@context`, `:id`, `:type`, `:issuer`, and
`:credentialSubject`.
