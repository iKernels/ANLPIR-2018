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

def get_maps(out):
    num_queries = float(len(out))
    MAP = 0.0
    map_per_query = dict()
    for qid in out:
        candidates = out[qid]
        # compute the number of relevant docs
        # get a list of precisions in the range(0,th)
        avg_prec = 0.0
        precisions = []
        num_correct = 0.0
        for i, candidate in enumerate(candidates, 1):
            if candidate == "true":
                num_correct += 1.0
                precisions.append(float(num_correct)/float(i))

        if precisions:
            avg_prec = float(sum(precisions))/float(len(precisions))
        map_per_query[qid]=avg_prec
        #print "%s AVR_REG: %.3f" %(qid,avg_prec)
        MAP += avg_prec
    return MAP*100.0/float(num_queries), map_per_query



def get_mrrs(out,th=15):
    mrr_per_query = dict()

    n = len(out)
    MRR = 0.0
    for qid in out:
        candidates = out[qid]
        this_mrr = 0.0
        for i in xrange(min(th, len(candidates))):
          if candidates[i] == "true":
            MRR += 1.0 / (float(i) + float(1))
            this_mrr = 1.0 / (float(i) + float(1))
            break
        mrr_per_query[qid] = this_mrr
    return MRR * 100.0 / n, mrr_per_query


def get_precisions(out,th):
    precision_per_query = dict()
    precisions = [0.0]*th
    n = 0.0
    for qid in out:
        cur_prec=0.0
        candidates = out[qid]
        if all(x == "false" for x in candidates):
            continue
        for i in xrange(min(th, len(candidates))):
            if candidates[i] == "true":
                precisions[i] += 1.0
                cur_prec=1.0
                break
        n += 1.0
        precision_per_query[qid]=cur_prec
    for i in xrange(1, th):
        precisions[i] += precisions[i-1]

    return [p*100/float(len(out)) for p in precisions], precision_per_query

if __name__ == '__main__':
    args = sys.argv
    home = "/Users/kateryna/Documents/qapipeline_current/sandbox_data/"
    '''
    args = ["", home+"wikiqa-test-ch-noprune-threshfocus-qc_as_rootchild-2015-11-19/svm.relevancy",
            "true",
            home+"wikiqa-test-ch-noprune-threshfocus-qc_as_rootchild-2015-11-19/svm_T.pred",
            home+"wikiqa-test-ch-noprune-threshfocus-qc_as_rootchild-2015-11-19/svm_1,2,3_fixed_+.pred"]
    '''
    if (len(args)<2):
        print "Usage: python permutation.py <relevancy-file> <ignore_noanswer/all_answer> <baseline-outputs> <list-of-system-outputs-delimited-by-space>"
        exit()

    gold_file = args[1]
    ignore_noans = False
    ignore_allans = False
    if args[2]=="true":
        ignore_noans=True
        ignore_allans=True

    baseline_file = args[3]
    system_files = args[4:]

    print "SYSTEM\tMAP\tp_MAP\tn_MAP\tMRR\tp_MRR\tn_MRR\tP@1\tp_P@1\tn_P@1"
    str_format = "%s\t%.2f\t%.5f\t%d\t%.2f\t%.5f\t%d\t%.2f\t%.5f\t%d"
    ir, bsl = read_res_pred_files(gold_file, baseline_file, "trec",reranking_th=None, ignore_noanswer=ignore_noans, ignore_allanswer=ignore_allans)
    MAP_bsl, maps_per_query_bsl = get_maps(bsl)
    MRR_bsl, MRR_per_query_bsl = get_mrrs(bsl)
    P1_bsl, P1_per_query_bsl = get_precisions(bsl,1)
    print str_format % (baseline_file, MAP_bsl, 1.0, 1, MRR_bsl, 1.0, 1, P1_bsl[0], 1.0, 1)

    for syst_file in system_files:
        if not os.path.isfile(syst_file):
            print syst_file, "not found"
            continue
        ir, syst = read_res_pred_files(gold_file, syst_file, "trec",reranking_th=None,ignore_noanswer=ignore_noans, ignore_allanswer=ignore_allans)

        MAP_syst, maps_per_query_syst = get_maps(syst)

        p_map,n_map = perm_test(maps_per_query_bsl,maps_per_query_syst)
        MRR_syst, MRR_per_query_syst = get_mrrs(syst)

        p_mrr, n_mrr=perm_test(MRR_per_query_bsl,MRR_per_query_syst)
        P1_syst, P1_per_query_syst = get_precisions(syst,1)
        p_p1, n_p1=perm_test(P1_per_query_bsl,P1_per_query_syst)
        print str_format % ("PERMUTATION: "+ syst_file, MAP_syst, p_map,n_map, MRR_syst,p_mrr,n_mrr, P1_syst[0],p_p1,n_p1)


        p_map = t_test(maps_per_query_bsl,maps_per_query_syst)
        p_mrr = t_test(MRR_per_query_bsl,MRR_per_query_syst)
        p_p1 = t_test(P1_per_query_bsl,P1_per_query_syst)
        print str_format % ("TTEST: "+syst_file, MAP_syst, p_map,0, MRR_syst,p_mrr,0, P1_syst[0],p_p1,0)

        '''
        arr = []
        with open(os.path.join(home, "rer_vs_class+.txt"), 'w') as f:
            for key in maps_per_query_bsl:
                if not key in P1_per_query_bsl or (not key in P1_per_query_syst):
                    print key
                else:
                    f.write("%s\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\tt%.5f\t%.5f\t%.5f\n" %(key,maps_per_query_bsl[key], maps_per_query_syst[key],
                                                                     MRR_per_query_bsl[key], MRR_per_query_syst[key],
                                                                     P1_per_query_bsl[key], P1_per_query_syst[key], -1.0*maps_per_query_bsl[key]+ maps_per_query_syst[key],
                                                                     -1.0*MRR_per_query_bsl[key]+MRR_per_query_syst[key],
                                                                     -1.0*P1_per_query_bsl[key]+P1_per_query_syst[key]))
                    arr.append((key,maps_per_query_bsl[key], maps_per_query_syst[key],
                                                                     MRR_per_query_bsl[key], MRR_per_query_syst[key],
                                                                     P1_per_query_bsl[key], P1_per_query_syst[key]))
        n = 31
        totlen=0
        '''
        '''
        with open(os.path.join(home, "T_vs_2,3+_samples.txt"), 'w') as fs:
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
        '''
    print "done"


