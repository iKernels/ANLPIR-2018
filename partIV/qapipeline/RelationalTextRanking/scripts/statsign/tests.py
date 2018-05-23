#from __future__ import division
__author__ = 'kateryna'
import random
import sys
import os
from ev import read_res_pred_files
from scipy import stats
import numpy as np
'''
the following procedure was copypasted from the thesis of Xuchen Yao, 2015
'''
SEED=994158012

def t_test(ev1, ev2):
    a_f1 = []
    b_f1 = []
    for key in ev1:
        a_f1.append(float(ev1[key]))
        b_f1.append(float(ev2[key]))
    arr1=np.asarray(a_f1)
    arr2=np.asarray(b_f1)
    t = arr1.shape
    ttest, pvalue = stats.ttest_rel(arr1,arr2)
    return pvalue

def perm_test(ev1, ev2, k = 100000):
    random.seed(SEED)
    a_f1 = []
    b_f1 = []
    for key in ev1:
        a_f1.append(ev1[key])
        b_f1.append(ev2[key])
    diff = [a-b for a,b in zip(a_f1, b_f1)]
    mu_diff = abs(sum(diff))
    n=0.0
    for i in range(k):
        new_diff = [d if random.random()>0.5 else -d for d in diff]
        mu_new_diff = abs(sum(new_diff))
        if mu_new_diff >= mu_diff:
            n += 1.0
        #if i%10000==0:
         #   print "%d permutations done" % (i)
    #print "p = %f (n = %d)" % (n*1.0/float(k), n)
    return n*1.0/float(k), n

def get_maps(tseed,out):

    num_queries = float(len(out))
    MAP = 0.0
    map_per_query = dict()
    for qid in out:
        candidates = out[qid]

        map_per_query[qid]=random.random()
        avg_prec = map_per_query[qid];
        #print "%s AVR_REG: %.3f" %(qid,avg_prec)
        MAP += avg_prec
    return MAP*100.0/float(num_queries), map_per_query



def get_mrrs(tseed,out,th=15):
    mrr_per_query = dict()

    n = float(len(out))
    MRR = 0.0
    for qid in out:
        candidates = out[qid]
        this_mrr = 1.0/float(random.randint(1,50))
        mrr_per_query[qid] = this_mrr
        MRR = MRR+this_mrr
    return MRR * 100.0 / n, mrr_per_query


def get_precisions(tseed,th,out):
    precision_per_query = dict()
    precisions = 0.0
    n = 0.0
    for qid in out:
        precision_per_query[qid]=float(random.randint(0,1))
        precisions = precisions+precision_per_query[qid]

    return [precisions*100/float(len(out))], precision_per_query

if __name__ == '__main__':

    random.seed(SEED)

    home = "/Users/kateryna/Documents/qapipeline_current/sandbox_data/"
    print "SYSTEM\tMAP\tp_MAP\tn_MAP\tMRR\tp_MRR\tn_MRR\tP@1\tp_P@1\tn_P@1"
    str_format = "%s\t%.2f\t%.5f\t%d\t%.2f\t%.5f\t%d\t%.2f\t%.5f\t%d"
    bslids = range(0,243)
    systds = range(0,243)
    MAP_bsl, maps_per_query_bsl = get_maps(1000,bslids)
    MRR_bsl, MRR_per_query_bsl = get_mrrs(1000,bslids)
    P1_bsl, P1_per_query_bsl = get_precisions(1000,1,bslids)
    print str_format % ("BASELINE dist:", MAP_bsl, 1.0, 1, MRR_bsl, 1.0, 1, P1_bsl[0], 1.0, 1)


    MAP_syst, maps_per_query_syst = get_maps(874589,systds)

        #p_map,n_map = perm_test(maps_per_query_bsl,maps_per_query_syst)
    MRR_syst, MRR_per_query_syst = get_mrrs(874589,systds)

        #p_mrr, n_mrr=perm_test(MRR_per_query_bsl,MRR_per_query_syst)
    P1_syst, P1_per_query_syst = get_precisions(874589,1, systds)
        #p_p1, n_p1=perm_test(P1_per_query_bsl,P1_per_query_syst)
        #print str_format % (syst_file, MAP_syst, p_map,n_map, MRR_syst,p_mrr,n_mrr, P1_syst[0],p_p1,n_p1)

    if True:
        p_map = t_test(maps_per_query_bsl,maps_per_query_syst)
        p_mrr = t_test(MRR_per_query_bsl,MRR_per_query_syst)
        p_p1 = t_test(P1_per_query_bsl,P1_per_query_syst)
        print str_format % ("TTEST: RANDOM", MAP_syst, p_map,  1.00, MRR_syst,p_mrr,1.0, P1_syst[0],p_p1,1.0)

        arr = []
        with open(os.path.join(home, "random.txt"), 'w') as f:
            for key in maps_per_query_bsl:
                f.write("%s\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\n" %(key,maps_per_query_bsl[key], maps_per_query_syst[key],
                                                                     MRR_per_query_bsl[key], MRR_per_query_syst[key],
                                                                     P1_per_query_bsl[key], P1_per_query_syst[key]))
                arr.append((key,maps_per_query_bsl[key], maps_per_query_syst[key],
                                                                     MRR_per_query_bsl[key], MRR_per_query_syst[key],
                                                                     P1_per_query_bsl[key], P1_per_query_syst[key]))
        n = 24
        totlen=0

        with open(os.path.join(home, "random_samples.txt"), 'w') as fs:
            fs.write("Size\tAVG_MAP\tSTDDEV_MAP\tAVG_MRR\tSTDEV_MRR\tAVG_P@1\tSTDEV_P@1\n");
            size = len(arr)/n
            for i in range(0,len(arr), size):

                sample = arr[i:min(i+size,len(arr))]
                sz = len(sample)
                print i, min(i+len(arr)/n, len(arr)), sz

                diffmap=np.mean([p[2]-p[1] for p in sample])
                stdmap=np.std([p[2]-p[1] for p in sample])
                diffmrr=np.mean([p[4]-p[3] for p in sample])
                stdmrr=np.std([p[4]-p[3] for p in sample])
                diffp1=np.mean([p[6]-p[5] for p in sample])
                stdp1=np.std([p[6]-p[5] for p in sample])

                pattern= "%d\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\n"
                fs.write(pattern % (len(sample),diffmap,stdmap,diffmrr,stdmrr, diffp1, stdp1))
                totlen+=len(sample)
        fs.close()
        f.close()
        print totlen
    print "done"


