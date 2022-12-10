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
<img width="568" alt="스크린샷 2022-12-10 오후 4 55 11" src="https://user-images.githubusercontent.com/85989698/206839571-0ce912d2-beee-4d58-9588-b3c01fd15413.png">

run "master ipAddress: master port" -I "input path" -O "output path"

```
sbt
sbt:distributedSorting> run 2.2.2.107:50051 -I /home/pink/64/input -O /home/pink/64/output
```

We make the input file using gensort -a
( -b option is not appropriate because key should be ASCIICode.)


## Result
-----------------------------------------------------------------------------
4 Workers with individualy 2 inputs
Each file is 32MB. So sum of the all inputs is 256MB (32 *2 *4)
Master is VM 7 and workers are VM 3/4/5/6.

- Master (VM 7) 

1) Starting Master
<img width="369" alt="스크린샷 2022-12-10 오후 4 59 53" src="https://user-images.githubusercontent.com/85989698/206840027-3ce83462-56ed-4fad-a2fc-a64fea4c3362.png">

2) When all workers are connected
<img width="424" alt="스크린샷 2022-12-10 오후 5 01 12" src="https://user-images.githubusercontent.com/85989698/206840058-605d2bbd-60e2-4be9-8ce0-a5e79fc17e37.png">

Master print all workers IP addresses.

3)When master finished all process and terminates.
<img width="449" alt="스크린샷 2022-12-10 오후 5 02 22" src="https://user-images.githubusercontent.com/85989698/206840105-c51481ba-462e-4a31-819e-b1b20c7d2ca9.png">

Master print the sum of all outputfiles sizes from individual workers.
Input was 256MB and the result byte is 256000000.
So we can say that number of records in the input are same with the number of records in the output.


Worker : vm 5
<img width="193" alt="스크린샷 2022-12-10 오후 5 07 47" src="https://user-images.githubusercontent.com/85989698/206840314-a2593ff4-695d-48b5-8528-309d735a25b0.png">

Worker : vm 4
<img width="202" alt="스크린샷 2022-12-10 오후 5 08 34" src="https://user-images.githubusercontent.com/85989698/206840338-caaadde3-39ad-4db4-9a7b-33aca9a7dee8.png">

Worker : vm 6
<img width="344" alt="스크린샷 2022-12-10 오후 5 09 05" src="https://user-images.githubusercontent.com/85989698/206840356-6568bbf7-d941-4572-a6e6-068162868a7a.png">

Worker :vm 3
<img width="269" alt="스크린샷 2022-12-10 오후 5 10 23" src="https://user-images.githubusercontent.com/85989698/206840418-a55e9a0d-8227-4ef4-8b71-2981a2c70959.png">

