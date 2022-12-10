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

<img width="709" alt="스크린샷 2022-12-10 오후 5 19 49" src="https://user-images.githubusercontent.com/85989698/206840864-a58a1009-08fa-4c87-9a07-4950b677f9cd.png">

## Result
-----------------------------------------------------------------------------
4 Workers with individualy 2 inputs
Each file is 32MB. So sum of the all inputs is 256MB (32 *2 *4)
Master is VM 7 and workers are VM 3/4/5/6.

## Master (VM 7) 

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


## Workers  
### Worker : vm 5  
<img width="193" alt="스크린샷 2022-12-10 오후 5 07 47" src="https://user-images.githubusercontent.com/85989698/206840314-a2593ff4-695d-48b5-8528-309d735a25b0.png">

### Worker : vm 4  
<img width="202" alt="스크린샷 2022-12-10 오후 5 08 34" src="https://user-images.githubusercontent.com/85989698/206840338-caaadde3-39ad-4db4-9a7b-33aca9a7dee8.png">

### Worker : vm 6  
<img width="344" alt="스크린샷 2022-12-10 오후 5 09 05" src="https://user-images.githubusercontent.com/85989698/206840356-6568bbf7-d941-4572-a6e6-068162868a7a.png">

### Worker :vm 3  
<img width="269" alt="스크린샷 2022-12-10 오후 5 10 23" src="https://user-images.githubusercontent.com/85989698/206840418-a55e9a0d-8227-4ef4-8b71-2981a2c70959.png">

-------------------------------------------------------------

### Worker vm03   
range from : "{Y>:#%`  
range to : Vu1s$HZwQ;  
<img width="708" alt="스크린샷 2022-12-10 오후 5 17 35" src="https://user-images.githubusercontent.com/85989698/206840795-ddf4b95c-93da-4838-8994-17a1bac95a67.png">


### Worker vm06  
range from : Vu27GO]$g?  
range to : k$&X\sS}`T  
<img width="695" alt="스크린샷 2022-12-10 오후 5 21 01" src="https://user-images.githubusercontent.com/85989698/206840909-2d356ef8-5f58-4245-be7d-71b20f8b3e39.png">


### Worker vm04  
range from : k$'a~gf{Z.  
range to : pW3\|:I8,%  
<img width="705" alt="스크린샷 2022-12-10 오후 5 15 15" src="https://user-images.githubusercontent.com/85989698/206840702-8c8a1cbb-a187-4f1e-8bb7-8ce31d6684b9.png">

### Worker vm05  
range from : pW9cf)7^Z@  
range to : ~~}+tO+-g}  
<img width="705" alt="스크린샷 2022-12-10 오후 5 21 24" src="https://user-images.githubusercontent.com/85989698/206840919-22df9cd2-53e3-4325-9b52-c495dc4b69a7.png">

  
    
Seeing the individual workers' range, all keys are ordered in ascending order in ASCII code from worker to worker.
To test whether the ouput is sorted in each workers, we made an testing code.


In each worker, to test the individual worker's sorting of the output file we should put command below.
```
sbt
test
```

Then the SortingTest that we made show whether the sorting is correctly done in each worker.  
<img width="510" alt="스크린샷 2022-12-10 오후 5 35 20" src="https://user-images.githubusercontent.com/85989698/206841376-6dc2cc1f-ba70-43c3-935b-86749e5ec2da.png">


We can check the sampling / partitioning/ shuffling / subpartitioning is well going with checking the temp directory.
You can check the individual process is well going in our progress notion of week 8.  
https://large-drain-0aa.notion.site/8th-week-b8c4ab5e540e423c9fb2505674ad106d


