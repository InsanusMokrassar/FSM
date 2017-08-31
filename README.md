# FSM

Very simple realisation of finite state machine.

## Example

In [Example](src/main/kotlin/com/github/insanusmokrassar/FSM/Example.kt) you can see constructing of FSM table for
expression which in regex can be represent as __"\d*;"__.

Here I use next expression:

```
<expr>::=<num>;
<num>::=num<num>
<num>::=eps

0<expr>::=3<num>4;
1<num>::=5num6<num>
2<num>::=7eps
```

This can be translated in next table:

| n | accept | error | stack | return | next | symbs |
|---|--------|-------|-------|--------|------|-------|
| 0 | false | true | false | false | 3 | \d |
| 1 | false | false | false | false | 5 | \d |
| 2 | false | true | false | false | 7 | ; |
| 3 | false | true | true | false | 1 | \d |
| 4 | true | true | false | true | - | ; |
| 5 | true | true | false | false | 6 | \d |
| 6 | false | true | false | false | 1 | \[\d;\] |
| 7 | false | true | false | true | - | ; |
