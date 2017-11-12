# Changelog for FiniteStateMachine project

## v1.0

First release

### v1.0-1

Fix work of toConfigString

## v1.1

* Add empty symbol in the end
* Bugs fixes

### v1.1-1

Fix errors texts

## v1.2

* Change types of input parameters of callbacks: `(String) -> Unit` => `(IObject<Any>, String) -> Unit`
* Added StateAction typealias instead of `(IObject<Any>, String) -> Unit`

### v1.2-1

Make Runner to be StateAction

### v1.2.2

Return old method `fun invoke(String): Unit`