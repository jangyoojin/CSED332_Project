# 332project
[CSED332]TeamProject - Team Pink

## Progress Document
https://large-drain-0aa.notion.site/CS332-Team-Project-bb4c00c8aa5f4f96a583a9539c78d1a3

## How to build

Master :
<img width="565" alt="스크린샷 2022-12-10 오후 4 54 15" src="https://user-images.githubusercontent.com/85989698/206839542-4bd2a7a0-b770-4231-8d55-e413ef2aaecf.png">

First, write the command "sbt" to the terminal first.
run "workerNum"

```
sbt
sbt:distributedSorting> run 4
```

Multiple main classes detected. Select one to run:
 [1] network.Master
 [2] network.Worker

Enter number: 1


Worker:
run "master ipAddress: master port" -I "input path" -O "output path"

```
sbt
sbt:distributedSorting> run 2.2.2.107:50051 -I /home/pink/64/input -O /home/pink/64/output
```

We make the input file using gensort -a
( -b option is not appropriate because key should be ASCIICode.)


## Result
-----------------------------------------------------------------------------
