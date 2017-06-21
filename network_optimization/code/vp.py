from pulp import *
import time
import sys

time1 = time.time()


def liner(a,stable):
    prob = LpProblem("model", LpMinimize)

    x = LpVariable.matrix("x", list(range(len(a[1]) - 3)), -30, +300)

    prob += lpSum(x)

    for i in xrange(1, len(a)):
        prob += lpSum(a[i][j] * x[j - 3] for j in xrange(3, len(a[1]))) >= a[i][0]
        prob += lpSum(a[i][j] * x[j - 3] for j in xrange(3, len(a[1]))) <= a[i][1]

    for each in stable:
        prob += x[each] == 0

    prob.writeLP("model.lp")

    prob.solve(COIN())

    # # Print the status of the solved LP
    # print("Status:", LpStatus[prob.status])
    #
    # # Print the value of the variables at the optimum
    # for v in prob.variables():
    #     print(v.name, "=", v.varValue)

    # Print the value of the objective
    # print("objective=", value(prob.objective))
    # print
    # print
    # print
    return prob, LpStatus[prob.status]

if len(sys.argv)==1:
    f = open('data.txt')
else:
    f = open(sys.argv[1])
a=[]
for line in f:
    each = line.split(',')
    a.append(each)
f.close()

for i in range(0,len(a)):
    for j in range(0,len(a[i])):
        a[i][j]=int(a[i][j])

for i in range(1,len(a)):
    a[i][0]=a[i][0]-a[i][2]
    a[i][1] = a[i][1] - a[i][2]
    for j in range(3,len(a[i])):
        if a[i][j] != 0:
            a[i][j] = 1
        else:
            a[i][j] = 0

stable = []
result =[]
while(True):
    ai, ok = liner(a, stable)
    if ok=='Infeasible':
        stable = stable[0:-1]
        break
    min=9999
    minNum=-1
    result=[]
    for index, item in enumerate(ai.variables()):
        result.append(item.varValue)
        if abs(item.varValue)<min and item.varValue !=0:
            min=abs(item.varValue)
            minNum=index
    stable.append(minNum)

deltaNC=''
for i in range(1,len(a)):
    deltaNC = deltaNC+','+str(a[i][2])
deltaNC=deltaNC[1:]+'\n'

deltaNC1=''
for i in range(1,len(a)):
    fk=a[i][2]
    for j in range(3,len(a[i])):
        if result[j-3]!=0 and a[i][j]!=0:
            fk+=result[j-3]
            fk=int(fk)
    deltaNC1=deltaNC1+','+str(fk)
deltaNC1=deltaNC1[1:]+'\n'

deltaC=''
for i in range(len(a[0])):
    deltaC=deltaC+','+str(a[0][i])
deltaC=deltaC[1:]+'\n'

deltaC1=''
for i in range(len(a[0])):
    deltaC1=deltaC1+','+str(a[0][i]+int(result[i]))
deltaC1=deltaC1[1:]+'\n'

time2 = time.time()
print deltaNC[:-1]
print deltaNC1[:-1]
print deltaC[:-1]
print deltaC1[:-1]
print
print "ues time:",time2 - time1
print

if len(sys.argv)!=3:
    f = open('result.csv', 'w')
else:
    f = open(sys.argv[2], 'w')

f.write(deltaNC)
f.write(deltaNC1)
f.write(deltaC)
f.write(deltaC1)
f.write(str(time2 - time1))
f.close()






