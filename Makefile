clean:
	rm -rf target

run:
	clojure -M:dev

repl:
	clojure -M:dev:nrepl

test:
	clojure -M:test

uberjar:
	clojure -T:build all
