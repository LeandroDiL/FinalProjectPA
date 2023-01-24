# FinalProjectPA
Abstract # ↰
Design and develop a server that, based on a text- and message-oriented protocol, takes requests of computation consisting of one or more mathematical expressions and input values and replies with the results.

Specifications # ↰
Domain definitions # ↰
Let 
�
e be a mathematical expression composed of the binary operators 
�
=
+
,
−
,
×
,
÷
,
pow
O=+,−,×,÷,pow and of zero or more named variables 
�
�
∈
�
V 
e
​
 ∈V.

Example: with 
�
=
�
+
1
�
−
2
�
e= 
y−2 
x
 
x+1
​
 , 
�
�
=
�
,
�
V 
e
​
 =x,y.
Let 
�
:
�
→
�
∗
a:V→R 
∗
  be a variable-values function that associates a list of numerical values 
�
(
�
)
∈
�
∗
a(v)∈R 
∗
  with a variable 
�
v.

Protocol # ↰
Upon connection with a client 
�
C, the server 
�
S performs iteratively these operations:

waits for a request 
�
r
closes the connection or replies with a response 
�
s, depending on the content of 
�
r
Request format # ↰
A request is a line of text with the following format (literal text is shown between double quotes "", regexes between single quotes ''):

Request = QuitRequest
        | StatRequest
        | ComputationRequest
The format of a quit request is:

QuitRequest = "BYE"
The format of a stat request is:

StatRequest = "STAT_REQS"
            | "STAT_AVG_TIME"
            | "STAT_MAX_TIME"
The format of a computation request is:

ComputationRequest = ComputationKind"_"ValuesKind";"VariableValuesFunction";"Expressions
ComputationKind = "MIN"
                | "MAX"
                | "AVG"
                | "COUNT"
ValuesKind = "GRID"
           | "LIST"
A variable-values function can be specified with the following format:

VariableValuesFunction = VariableValues
                       | VariableValuesFunction","VariableValues
VariableValues = VarName":"JavaNum":"JavaNum":"JavaNum
VarName = '[a-z][a-z0-9]*'
and JavaNum is a string that can be correctly parsed to a double using the Java Double.parseDouble() method. A list of expressions can be specified with the following format:

Expressions = Expression
            | Expressions";"Expression
Expression = VarName
           | Num
           | "("Expression""Op""Expression")"
Num = '[0-9]+(\.[0-9]+)?'
Op = "+"
   | "-"
   | "*"
   | "/"
   | "^"
Examples # ↰
Some examples of valid requests are (one per line):

Valid requests:

BYE
STAT_MAX_TIME
MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)
COUNT_LIST;x0:1:0.001:100;x1
Some examples of not valid requests are:

Not valid requests:

bye
MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)
COUNT_LIST;x0:1:0.001:100;
MAX_LIST;x0:0:0,1:2;(x0+1)
Response format # ↰
A response is a line of text with the following format:

Response = ErrorResponse
         | OkResponse
The format of an error response is:

ErrorResponse = ERR";"'[^;]*'
The format of an ok response is:

OkResponse = OK";"JavaNum";"JavaNum
where [^;]* does not include new line characters.

Request processing specifications # ↰
If the request 
�
r is a quit request, the server 
�
S must immediately close the connection with the client 
�
C.

Otherwise, 
�
S must reply with a response 
�
s. If 
�
s is an error response, the part of 
�
s following ERR; must be a human-comprehensible, succint textual description of the error. Otherwise, if 
�
s is an ok response, the first of two numbers following OK; must be the response time, i.e., the number of seconds 
�
S took to process 
�
r, with at least 3 digits after the decimal separator (millisecond precision).

Stat requests # ↰
If 
�
r is a stat request, 
�
S replies with an ok response where the second number is:

the number of ok responses served by 
�
S (excluding 
�
r) to all clients since it started, if 
�
r is STAT_REQS;
the average response time of all ok responses served by 
�
S (excluding 
�
r) to all clients since it started, if 
�
r is STAT_AVG_TIME;
the maximum response time of all ok responses served by 
�
S (excluding 
�
r) to all clients since it started, if 
�
r is STAT_MAX_TIME.
Computation requests # ↰
If 
�
r is a computation request, 
�
S does the following steps:

parse a variable-values function 
�
a from the VariableValuesFunction part of 
�
r
build a list 
�
T of value tuples from 
�
a, each value tuple specifying one value for each 
�
v of the variables for which 
�
(
�
)
≠
∅
a(v)

=∅, depending on the ValuesKind part of 
�
r
parse a non-empty list 
�
=
(
�
1
,
…
,
�
�
)
E=(e 
1
​
 ,…,e 
n
​
 ) of expressions from the Expressions part of 
�
r
compute a value 
�
o on 
�
T and 
�
E depending on the ComputationKind part of 
�
r
If any of the steps above fails, 
�
S replies with an error response. Otherwise 
�
S replies with an ok response 
�
s where the second number in 
�
s is 
�
o.

Step 1: parsing of VariableValuesFunction to 
�
a # ↰
First, a list 
�
I of tuples 
(
�
,
�
lower
,
�
step
,
�
upper
)
(v,x 
lower
​
 ,x 
step
​
 ,x 
upper
​
 ) is obtained by parsing each VariableValues. If, for any tuple, 
�
step
≤
0
x 
step
​
 ≤0, the step fails.

Second, 
�
:
�
→
�
(
�
)
a:V→P(R) is built as follows: if no tuple for 
�
v exists in 
�
I, then 
�
(
�
)
=
∅
a(v)=∅; otherwise, 
�
(
�
)
=
(
�
lower
+
�
�
step
:
�
lower
+
�
�
step
≤
�
upper
)
�
∈
�
a(v)=(x 
lower
​
 +kx 
step
​
 :x 
lower
​
 +kx 
step
​
 ≤x 
upper
​
 ) 
k∈N
​
 .

Example: x0:-1:0.1:1,x1:-10:1:20 is parsed such that 
�
(
a(x0
)
=
(
−
1
,
−
0.9
,
…
,
0.9
,
1
)
)=(−1,−0.9,…,0.9,1), 
�
(
a(x1
)
=
(
−
10
,
−
9
,
…
,
19
,
20
)
)=(−10,−9,…,19,20), and 
�
(
�
)
=
∅
a(v)=∅ for any other 
�
v.
Step 2: building of value tuples 
�
T from 
�
a # ↰
If ValuesKind is GRID, than 
�
T is the cartesian product of all the non empty lists in the image of 
�
a.

Otherwise, if ValuesKind is LIST, if the non empty lists in the image of 
�
a do not have the same lenght, the step fails. Otherwise, 
�
T is the element-wise merging of those lists.

For example, for an 
�
a parsed from x:1:1:3,y:2:2:6:

�
=
(
(
1
,
2
)
,
(
2
,
2
)
,
(
3
,
2
)
,
…
,
(
1
,
6
)
,
(
2
,
6
)
,
(
3
,
6
)
)
T=((1,2),(2,2),(3,2),…,(1,6),(2,6),(3,6)) if ValuesKind is GRID;
�
=
(
(
1
,
2
)
,
(
2
,
4
)
,
(
3
,
6
)
)
T=((1,2),(2,4),(3,6)) if ValuesKind is LIST.
where x and y are omitted in 
�
T elements for brevity.

Step 3: parsing of Expressions to 
�
E # ↰
For each Expression token in Expressions, an expression 
�
e is built and added to 
�
E by parsing the Expression token based on the corresponding context-free grammar. If any of the expression parsing fails, the step fails.

A sample code for performing this step is provided here in the form of a few Java classes. The student may freely get inspiration from or reuse this code.

Step 4: computation of 
�
o from 
�
T and 
�
E # ↰
Let 
�
�
∈
�
V 
t
​
 ∈V be the set of variables for which a tuple 
�
t defines the values and let 
�
(
�
)
∈
�
e(t)∈R be the value of the expression 
�
e for the variables values given by 
�
t such that 
�
�
⊇
�
�
V 
t
​
 ⊇V 
e
​
 .

Then:

if ComputationKind is MIN, 
�
=
min
⁡
�
∈
�
,
�
∈
�
�
(
�
)
o=min 
e∈E,t∈T
​
 e(t), or the step fails if 
∃
�
∈
�
:
�
�
⊉
�
�
∃e∈E:V 
t
​
 

⊇V 
e
​
 ;
if ComputationKind is MAX, 
�
=
max
⁡
�
∈
�
,
�
∈
�
�
(
�
)
o=max 
e∈E,t∈T
​
 e(t), or the step fails if 
∃
�
∈
�
:
�
�
⊉
�
�
∃e∈E:V 
t
​
 

⊇V 
e
​
 ;
if ComputationKind is AVG, 
�
=
1
∣
�
∣
∑
�
∈
�
�
1
(
�
)
o= 
∣T∣
1
​
 ∑ 
t∈T
​
 e 
1
​
 (t), or the step fails if 
�
�
⊉
�
�
1
V 
t
​
 

⊇V 
e 
1
​
 
​
 ;
if ComputationKind is COUNT, 
�
=
∣
�
∣
o=∣T∣.
Examples of request-response pairs # ↰
Some examples of request-response pairs:

Request: MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(21.1-x0));(x1*x0)

Response: OK;0.040;52168.009950

Request: COUNT_LIST;x0:1:0.001:100;x1

Response: OK;0.070;99001.000000

Request: MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)

Response: ERR;(ComputationException) Unvalued variable log

Request: STAT_MAX_TIME

Response: OK;0.000;0.070000

Non-protocol specifications # ↰
The server must:

log on the standard output or standard error significant runtime events as:
new connection from client
disconnection from client
errors
listen on port 
�
p specified as command-line argument
handle multiple clients at the same time
never terminate, regardless of clients behavior
at any time, do at most 
�
n computation for processing computation requests at the same time, with 
�
n being equal to the number of available processors on the machine where the server is running. Note that the server must still be able to serve more than 
�
n clients at the same time.
Moreover, the server must:

be a Java application delivered as a .jar named after the student last name and first name in upper camel case notation (e.g., MedvetEric.jar);
be executable with the following syntax java -jar MedvetEric.jar 
�
p (e.g., java -jar MedvetEric.jar 10000 for 
�
=
10000
p=10000)
