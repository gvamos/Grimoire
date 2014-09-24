#!/usr/bin/python3
#
# Get data: <curl http://user-agent-polling.herokuapp.com/read > agent.json>
#

#
# Check cases:
#   
# 
import re
import json
from pprint import pprint
import difflib

UNSTABLE_PARAMS={'when-installed', 'ScreenSize'}
json_data=open('agent.json')
data = json.load(json_data)
json_data.close()

def chk(s1,s2):
    s = difflib.SequenceMatcher(a=s1, b=s2)
    for block in s.get_matching_blocks():
        print("match at a[%d] and b[%d] of length %d" % block)

def uniques(rows):  return (set(rows))


def tabular():
    for row in data:
        line = row['userAgent'] + "\t" + row['parameters']
        print(line)

def asJson(paramString):
    pairs = paramString[1:].split("&")
    dictionary = {}
    for pair in pairs:
        [k,v]=pair.split("=")
        dictionary[k]=v
    return dictionary
    

def paramInvariants(params):
    invariant = params.copy()
    for key in UNSTABLE_PARAMS: invariant.pop(key,None)

    if ('os' in invariant):
        os = invariant['os']
        invariant['osfamily'] = os.split("_")[0].split(" ")[0].split("-")[0]
   
    return invariant

def test(n):
    parametersString = parameters[n]
    agentString = userAgents[n]
    
    json = asJson(parametersString)
    paramsJson = paramInvariants(json)
    agentJson = agentMap(agentString)
    print("params")
    print(paramsJson)
    print("agent")
    print(agentJson)
    print('score')
    print(score(paramsJson,agentJson))
    return [paramsJson,agentJson]

def val(m,k):
    if k in m: return m[k]
    return None

def valMatch(a,p,k):
    if (k in a and k in p):
      if (a[k] == p[k]): return 1.0
      if (k in a and a[k].find(p[k])) : return 0.6
      if (k in p and p[k].find(a[k])): return 0.6
    if (k in p and any(p[k] == e for e in a['lead'] )): return .6
    if (a['tail'].find(p[k]) > -1): return 0.5
    return 0.0


def score(p,a):
    points = 0.0
    val = valMatch(a,p,'osfamily')
    if (val < 0.5): return 0.0
    points += val
    val = valMatch(a,p,'lang')
    if (val < 0.5): return 0.0
    points += val
    return val


def stringScore(p,a):
    paramsJson = paramInvariants(asJson(p))
    agentJson = agentMap(a)
    return score(paramsJson,agentJson)

def caseScore(n):
    return stringScore(parameters[n],userAgents[n])
    

def apToken(agent,param):
    return "" + agent + str(paramInvariants(asJson(param)))

userAgents = [ row['userAgent'] for row in data]
parameters = [ row['parameters'] for row in data]
parameterTypes = [ str(paramInvariants(asJson(row['parameters']))) for row in data]
#pairsA = [ row['userAgent'] + ':' + paramInvariants(row['parameters']) for row in data]
pairsA = [ apToken(row['userAgent'], row['parameters']) for row in data]
pairsP = [ str(paramInvariants(asJson(row['parameters']))) + ':' +  row['userAgent']
           for row in data]

#####################################################################
#
# Digest the data
#
#####################################################################

UNIQUE_AGENT_PARAM_PAIRS = set(pairsA)
agentWordSet = set()
paramWordSet = set()
paramWordUsages = {}
paramAttributeWords = {}

userAgentToParamsMap = {}
paramsToUserAgentsMap = {}

def interesting(s):
    return len(s) > 1 and len(s) < 30

def detox(s):
    s0 = s.replace(" ","_")
    if (s0.endswith("-")): return s0[0:len(s0)-1]
    return s0

def agentMap(s):
    lcAgentString = s.lower()
    found = {}
    parts = s.split(")")
       
    lead = parts.pop(0)
    found['tail']=")".join(parts).strip()
    leadParts = lead.split(" (")
    found['browserBase'] = leadParts.pop(0)
    majors = [s.strip() for s in "".join(leadParts).split(";")]
            
    found['osfamily'] = majors.pop(0)
    if lcAgentString.find('android'):
        found['osfamily'] = 'android'
    else:
        if lcAgentString.find('ios'):
            found['osfamily']='ios'
    found['lead'] = [ detox(major) for major in majors if interesting(major)]
    return found


def a(i):
    agentString = userAgents[i]
    res = agentMap(agentString)
    return res

def r(i):
    return(asJson(parameters[i]))
    

cases=[]
n=0
for row in data:
    agentStr = row['userAgent']
    agent=agentMap(agentStr)
    param = paramInvariants(asJson(row['parameters']))
    paramStr = str(param)
    
    for attribute in param.keys():
        value = param[attribute]
        paramWordUsages.setdefault(value, set()).add(attribute)
        paramAttributeWords.setdefault(attribute, set()).add(value)
    
    userAgentToParamsMap.setdefault(agentStr, set()).add(paramStr)
    paramsToUserAgentsMap.setdefault(paramStr, set()).add(agentStr)
    
    case = {'n':n, 'row':row, 'agent':agent, 'param':param, 'score':score(param,agent)}
    cases.append(case)
    n+=1

paramWordList = paramWordUsages.keys()

ambiguousAgents = [a for a in userAgentToParamsMap.keys() if len(userAgentToParamsMap[a]) > 1]
ambigousParams = [p for p in paramsToUserAgentsMap.keys() if len(paramsToUserAgentsMap[p]) > 1]

sortedPairsA = sorted(pairsA)

def reportPairs():
    for l in sortedPairsA: print(l+"\n")

def report():
    print("Rows................................= " + str(len(data)))
    print("User agents.........................= " + str(len(set(userAgents))))
    print("Param classes ......................= " + str(len(set(parameterTypes))))
    print("Agents with multiple param matches..= " + str(len(set(ambiguousAgents))))
    print("Params with multiple agent matches..= " + str(len(set(ambigousParams))))

report()


###############################################################
#
# We consider a user agent and parameter set matched if we have
# seen them matched before, such as in the AppThwack data
#
# Note that only invariant parameters are to be checked
#
###############################################################
def isMatched(agent,param):
    return  apToken(agent,param) in UNIQUE_AGENT_PARAM_PAIRS

################################################################
#
#  Unit atest: A pair should match itself but not another pair
#
#################################################################
agent0 = data[0]['userAgent']
agent1 = data[1]['userAgent']

params0 = data[0]['parameters']
params1 = data[1]['parameters']

assert(isMatched(agent0,params0))
assert(not isMatched(agent0,params1))

strangeCases = [case['n'] for case in cases if case['score']==0.0]
perfectCases = [case['n'] for case in cases if case['score']==1.0]
broken = float(len(strangeCases))/float(len(cases))



n=0
parametersString = parameters[n]
agentString = userAgents[n]
    
paramsJson = paramInvariants(asJson(parametersString))
agentJson = agentMap(agentString)

p = paramsJson
a = agentJson

z = test(n)
    
# 8,42,54,55,81,197,135, ... 260+
#
# 8: Kindle fire, browser: "Silk/1.0.22.153...
# 42: Android with Opera browser
# 54: Amdrpod with Opera
# 55: Kindle_fire with Silk
# 81: HTC Sensation
#
# Fails badly on iOS
#





